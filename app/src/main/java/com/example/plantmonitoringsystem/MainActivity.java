package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.plantmonitoringsystem.SupportClasses.CardViewAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.Mode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private ConstraintLayout nullView;
    String moisture,humidity,temp,light;
    ProgressDialog progressDialog;
    ArrayList<String> values = new ArrayList<String>();
    RecyclerView recyclerView;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prompt to sign in for non registered users
        user = FirebaseAuth.getInstance().getCurrentUser();
        nullView = findViewById(R.id.NullView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");

        recyclerView = findViewById(R.id.recyclerView);

        if(user == null){
            Log.e("MainActivityUser","User null");
            //open sign in activity
            Intent intent = new Intent(this,signIn.class);
            startActivity(intent);
        }else{
            progressDialog.show();
            reference = FirebaseDatabase.getInstance().getReference(user.getUid());
            populateView();
        }

    }

    //populate the card views with correct sensor data
    private void populateView() {
        values.clear();
        try {
            Log.e("PopulateView","Listening for Parameters");
            //check for hardware configuration configuration
            reference.child("NumberOfUnits").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        if (dataSnapshot.getValue(Integer.class) != 0) {
                            ListenForParameter("Moisture");
                            ListenForParameter("Temperature");
                            ListenForParameter("LightIntensity");
                            ListenForParameter("Humidity");

                        } else {
                            progressDialog.dismiss();
                            recyclerView.setVisibility(View.GONE);
                            nullView.setVisibility(View.VISIBLE);
                        }
                    }catch(NullPointerException ne){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"System Error Occured",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            Log.e("Adapter", e.getMessage());
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void ListenForParameter(final String parameter) {

        Log.e("PopulateView","Listening for "+parameter );

        reference.child("Average/"+parameter).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    float t = dataSnapshot.getValue(Float.class);
                    final Intent i = new Intent(MainActivity.this,ParameterList.class);
                    switch (parameter){
                        case "Moisture":
                            moisture = String.valueOf(t);
                            break;
                        case "Humidity":
                            humidity = String.valueOf(t);
                            progressDialog.dismiss();
                            break;
                        case "Temperature":
                            temp = String.valueOf(t);
                            break;
                        case "LightIntensity":
                            light = String.valueOf(t);
                            break;
                    }

                    values.add(0,temp);
                    values.add(1,humidity);
                    values.add(2,moisture);
                    values.add(3,light);
                    CardViewAdapter adapter = new CardViewAdapter(values);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

                    //for handling click events
                    adapter.setListener(new CardViewAdapter.Listener() {
                        @Override
                        public void onClick(int position) {
                            switch (position){
                                case 0:
                                    i.putExtra("Parameter","Temperature");
                                    break;
                                case 1:
                                    i.putExtra("Parameter","Humidity");
                                    break;
                                case 2:
                                    i.putExtra("Parameter","Moisture");
                                    break;
                                case 3:
                                    i.putExtra("Parameter","LightIntensity");
                                    break;
                            }
                            startActivity(i);
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                    Log.e("Listener",e.getMessage());
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            /**this method is called is when something has changed in the data. Our project will only push the data and not update it
             * Hence, any event like this should be regarded as an error and ignored
             */
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //to ensure that user does not come back to main activity without sign in
    @Override
    protected void onRestart() {
        if(user == null){
            //open sign in activity
            Intent intent = new Intent(this,signIn.class);
            startActivity(intent);
        }else{
            reference = FirebaseDatabase.getInstance().getReference(user.getUid());
            populateView();
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(this,signIn.class);
                startActivity(intent);
                break;
            case R.id.sendData:
                sharePDF();
                break;
            case R.id.addSlave:
                addSlave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //add a slave unit to the farm
    private void addSlave() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Describe the location of sensor unit");

        //create the edit text
        final EditText input = new EditText(this);
        dialog.setView(input);

        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                reference.child("NumberOfUnits").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String description = input.getText().toString().trim();
                        int k = dataSnapshot.getValue(Integer.class);
                        if(count != k){
                            count = k+1;
                            reference.child("zone" + count).child("Description").setValue(description);
                            reference.child("NumberOfUnits").setValue(count);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        dialog.setNegativeButton("Cancel",null);
        dialog.show();
    }

    //share the data as PDF file
    private void sharePDF() {

        try {
            //create the file
            String path = getFilesDir()+"/FarmData.pdf";
            File file = new File(path);
            OutputStream stream = new FileOutputStream(file);

            //add required content in the PDF file
            Document document = new Document();
            PdfWriter.getInstance(document,stream );
            document.open();
            document.add(new Paragraph("Temperature: "+temp));
            document.add(new Paragraph("Humidity: "+humidity));
            document.add(new Paragraph("Moisture: "+moisture));
            document.add(new Paragraph("Light Intensity: "+light));
            document.close();

            //share the file
            Uri uri = FileProvider.getUriForFile(this,"com.example.plantmonitoringsystem",file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.e("PDF", "Just Opening Intent" );
            startActivity(Intent.createChooser(intent, "Share Farm Details"));
            stream.close();

        } catch (DocumentException e) {
            e.printStackTrace();
            Log.e("PDF", "DocumentExpection: " +e.getMessage());
            Toast.makeText(this,"Could not generate the PDF file",Toast.LENGTH_SHORT).show();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("PDF", "FileNotFound: " +e.getMessage());
            Toast.makeText(this,"System Error occured while locating the File",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("PDF", "Execption: "+e.getMessage() );
            Toast.makeText(this,"System Error occured",Toast.LENGTH_SHORT).show();
        }
    }

    //sign Out when hardware not configured
    public void Configure(View view){
        Intent intent = new Intent(this,ConfigureHardware.class);
        startActivity(intent);
    }

    //when back button is pressed, close the app
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}