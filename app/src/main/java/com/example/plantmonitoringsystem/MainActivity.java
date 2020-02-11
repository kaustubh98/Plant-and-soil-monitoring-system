package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
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

import com.example.plantmonitoringsystem.Fragments.FragmentAverage;
import com.example.plantmonitoringsystem.Fragments.FragmentZonal;
import com.example.plantmonitoringsystem.SupportClasses.CardViewAdapter;
import com.example.plantmonitoringsystem.SupportClasses.PagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
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
    private ConstraintLayout nullView,loadingView;
    private AppBarLayout appBarLayout;
    ArrayList<String> values = new ArrayList<String>();
    RecyclerView recyclerView;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //prompt to sign in for non registered users
        user = FirebaseAuth.getInstance().getCurrentUser();
        nullView = findViewById(R.id.NullView);
        loadingView = findViewById(R.id.loadingView);
        appBarLayout = findViewById(R.id.appBarLayout);

        if(user == null){
            Log.e("MainActivityUser","User null");
            //open sign in activity
            Intent intent = new Intent(this,signIn.class);
            startActivity(intent);
        }else{
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
                            PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
                            ViewPager viewPager = findViewById(R.id.view_pager);
                            viewPager.setAdapter(adapter);
                            TabLayout tabLayout = findViewById(R.id.tabs);
                            tabLayout.setupWithViewPager(viewPager);
                            appBarLayout.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);

                        } else {
                            appBarLayout.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);
                            TabLayout tabLayout = findViewById(R.id.tabs);
                            tabLayout.setVisibility(View.GONE);
                            nullView.setVisibility(View.VISIBLE);
                        }
                    }catch(NullPointerException ne){
                        appBarLayout.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
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
            document.add(new Paragraph("Temperature: "+FragmentAverage.getTemp()));
            document.add(new Paragraph("Humidity: "+FragmentAverage.getHumidity()));
            document.add(new Paragraph("Moisture: "+ FragmentAverage.getMoisture()));
            document.add(new Paragraph("Light Intensity: "+FragmentAverage.getLight()));
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