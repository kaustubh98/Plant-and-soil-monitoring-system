package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    String moistureLevel,Humidity,Temperature,Light;
    private TableLayout tableLayout;
    private ConstraintLayout nullView;
    TextView moisture,humidity,temp,light;
    ProgressDialog progressDialog;
    ArrayList<Integer> TemperatureList = new ArrayList<Integer>();
    ArrayList<Integer> HumidityList = new ArrayList<Integer>();
    ArrayList<Integer> MoistureList = new ArrayList<Integer>();
    ArrayList<Integer> LightList = new ArrayList<Integer>();

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
            Log.e("MainActivityUser","User null");
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

        TemperatureList.clear();
        HumidityList.clear();
        MoistureList.clear();
        LightList.clear();
        Log.e("PopulateView","Populating View");

        //temperature
//        reference.child("Temperature").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                progressDialog.dismiss();
//
//                try{
//                    Log.e("Adding Temperature","Entered Methode");
//                    int temperature = dataSnapshot.getValue(Integer.class);
//                    Log.e("Adding Temperature","The temperature is "+temperature);
//                    TemperatureList.add(0,temperature);
//                    Log.e("Adding Temperature","Added Temperature is: "+TemperatureList.get(0));
//                    temp.setText(String.valueOf(TemperatureList.get(0)));
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
////        //humidity
//        reference.child("Humidity").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                try{
//                    int humid = dataSnapshot.getValue(Integer.class);
//                    HumidityList.add(0,humid);
//                    humidity.setText(String.valueOf(HumidityList.get(0)));
//                    Log.e("Adding Humidity","Added Humidity is: "+humidity.getText());
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        //soil Moisture
//        reference.child("Moisture").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                try{
//                    int temporary = dataSnapshot.getValue(Integer.class);
//                    MoistureList.add(0,temporary);
//                    moisture.setText(String.valueOf(MoistureList.get(0)));
//                    Log.e("Adding Moisture","Added moisture is: "+moisture.getText());
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        //light intensity
//        reference.child("LightIntensity").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                try{
//                    int temporary = dataSnapshot.getValue(Integer.class);
//                    LightList.add(0,temporary);
//                    light.setText(String.valueOf(LightList.get(0)));
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        try {
            Log.e("PopulateView","Listening for Parameters");
            //check for hardware configuration configuration
            reference.child("Temperature").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        ListenForParameter("Moisture", moisture, MoistureList);
                        ListenForParameter("Temperature", temp, TemperatureList);
                        ListenForParameter("LightIntensity", light, LightList);
                        ListenForParameter("Humidity", humidity, HumidityList);
                    }else{
                        progressDialog.dismiss();
                        tableLayout.setVisibility(View.GONE);
                        nullView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void ListenForParameter(String parameter, final TextView textView, final ArrayList<Integer> dataList) {

        dataList.clear();
        Log.e("PopulateView","Listening for "+parameter );

        reference.child(parameter).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                progressDialog.dismiss();
                try{
                    Log.e("Message",dataSnapshot.getValue().toString());
                    int temporary = dataSnapshot.getValue(Integer.class);
                    dataList.add(0,temporary);
                    textView.setText(String.valueOf(dataList.get(0)));
                    Log.e("Adding Temperature","Added Temperature is: "+textView.getText());

                }catch(Exception e){
                    e.printStackTrace();
                    Log.e("Listener",e.getMessage());
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

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

    //show temperature details
    public void paramsTemp(View view){
        Intent i = new Intent(this,ParameterList.class);
        i.putExtra("Parameter","Temperature");
        i.putExtra("DataList", TemperatureList);
        startActivity(i);
    }

    //show humidity details
    public void paramsHumid(View view){
        Intent i = new Intent(this,ParameterList.class);
        i.putExtra("Parameter","Humidity");
        i.putExtra("DataList", HumidityList);
        startActivity(i);
    }

    //show temperature details
    public void paramsMoist(View view){
        Intent i = new Intent(this,ParameterList.class);
        i.putExtra("Parameter","Moisture");
        i.putExtra("DataList", MoistureList);
        startActivity(i);
    }

    //show temperature details
    public void paramsLight(View view){
        Intent i = new Intent(this,ParameterList.class);
        i.putExtra("Parameter","Light");
        i.putExtra("DataList", LightList);
        startActivity(i);
    }

    //when back button is pressed, close the app
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}