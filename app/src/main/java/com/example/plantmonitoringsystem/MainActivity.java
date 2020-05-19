package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.plantmonitoringsystem.SupportClasses.CreatePDF;
import com.example.plantmonitoringsystem.SupportClasses.PagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseUser user;
    private DatabaseReference reference;
    private ConstraintLayout nullView,loadingView;
    private AppBarLayout appBarLayout;
    ArrayList<String> values = new ArrayList<String>();
    int count = 0;
    Uri ImageUri;
    byte[] bytesFile;
    private int WRITE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //prompt to sign in for non registered users
        user = FirebaseAuth.getInstance().getCurrentUser();
        nullView = findViewById(R.id.textView7);
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
                            nullView.setVisibility(View.GONE);

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
            case R.id.setting:
                // go to settings activity
                Intent i = new Intent(this,Settings.class);
                startActivity(i);
                break;
            case R.id.FarmImage:
                shareImages();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImages() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Please select an image"),REQUEST_IMAGE_CAPTURE);

    }

    //share the data as PDF file
    private void sharePDF() {

        try {


//            StorageReference reference = FirebaseStorage.getInstance().getReference(user.getUid()+"/Abstract.pdf");
//            reference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//
//                    bytesFile = bytes;
//                    storeTestFile();
//                }
//            });

            //create the file
            String path = getFilesDir() + "/FarmData.pdf";

            CreatePDF pdf = new CreatePDF(path);
            File file = pdf.getFile();

            //testing of saving file
            //storeTestFile();

            //share the file
            Uri uri = FileProvider.getUriForFile(this, "com.example.plantmonitoringsystem", file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.e("PDF", "Just Opening Intent");
            startActivity(Intent.createChooser(intent, "Share Farm Details"));
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

    private void storeTestFile(){

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Granted.
                    File testFile = new File(Environment.getExternalStorageDirectory(),"Abstract.pdf");
//                    String s = "Andar ka maal dikh raha h ho tera code chal raha h... Aish kar!";
                    FileOutputStream stream;
                    try {
                        stream = new FileOutputStream(testFile);
                        stream.write(bytesFile);
                        stream.close();
                        Toast.makeText(this,"File Generated...",Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("Test", e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Test", e.getMessage());
                    }


                }
                else{
                    //Denied.
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    //sign Out when hardware not configured
    public void Configure(View view){
        Intent intent = new Intent(this,ConfigureHardware.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE  && data != null && data.getData() != null){
            ImageUri = data.getData();
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Image...");
            dialog.setTitle("Please wait");
            dialog.show();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(user.getUid()+"/"+timestamp.toString());
            storageReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Image could not be Uploaded",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    //when back button is pressed, close the app
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}