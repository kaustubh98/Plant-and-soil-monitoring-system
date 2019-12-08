package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    String moistureLevel,Humidity,Temperature,Light;
    private TableLayout tableLayout;
    private ConstraintLayout nullView;
    TextView moisture,humidity,temp,light;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prompt to sign in for non registered users
        user = FirebaseAuth.getInstance().getCurrentUser();
        tableLayout = findViewById(R.id.DataLayout);
        nullView = findViewById(R.id.NullView);
        moisture = findViewById(R.id.moistureLevel);
        humidity = findViewById(R.id.humidity);
        temp = findViewById(R.id.temperature);
        light = findViewById(R.id.lightIntensity);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");

        if(user == null){
            Log.e("MainActivity","User null");
            //open sign in activity
            Intent intent = new Intent(this,signIn.class);
            startActivity(intent);
        }else{
            progressDialog.show();
            reference = FirebaseDatabase.getInstance().getReference(user.getUid());
            populateView(moisture,humidity,temp,light);
        }

    }

    //populate the textviews with correct sensor data
    private void populateView(final TextView moisture, final TextView humidity, final TextView temp, final TextView light) {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                try{
                    moistureLevel = dataSnapshot.child("moisture").getValue().toString().trim();
                    Humidity = dataSnapshot.child("humidity").getValue().toString().trim();
                    Light = dataSnapshot.child("lightIntensity").getValue().toString().trim();
                    Temperature = dataSnapshot.child("temperature").getValue().toString().trim();
                }catch(NullPointerException ne){
                    ne.printStackTrace();
                    tableLayout.setVisibility(View.GONE);
                    nullView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Not configured...",Toast.LENGTH_SHORT).show();
                }


                moisture.setText(moistureLevel);
                humidity.setText(Humidity);
                temp.setText(Temperature);
                light.setText(Light);

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
            populateView(moisture,humidity,temp,light);
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
                Intent intent = new Intent(this,signIn.class);
                startActivity(intent);
                break;
            case R.id.sendData:
                //send data in PDF form
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //sign Out when hardware not configured
    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,signIn.class);
        startActivity(intent);
    }

    //when back button is pressed, close the app

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
