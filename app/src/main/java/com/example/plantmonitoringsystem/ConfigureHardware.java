package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfigureHardware extends AppCompatActivity {

    EditText e_id,e_des;
    private DatabaseReference reference;
    private FirebaseUser user;
    private int count=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_hardware);

        Toolbar toolbar = findViewById(R.id.toolbar_configure);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Hardware unit");

        e_des = findViewById(R.id.description);
        e_id = findViewById(R.id.device_id);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    //add everything to firebase
    public void configure(View view){
        final String id = e_id.getText().toString().trim();
        final String des = e_des.getText().toString().trim();

        if(ValidateUI(id,des)){

            reference.child(user.getUid()+"/NumberOfUnits").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        int temp = dataSnapshot.getValue(Integer.class);
                        //registering user and device IDs
                        reference.child(user.getUid()).child("Device").push().setValue(id);
                        reference.child("Devices").child(id).setValue(user.getUid());

                        //adding slave unit
                        if(count != temp){
                            count = temp+1;
                            reference.child(user.getUid()).child("Zones/zone" + count).child("Description").setValue(des);
                            reference.child(user.getUid() + "/NumberOfUnits").setValue(count);
                            Toast.makeText(getApplicationContext(),"Hardware unit added successfully",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ConfigureHardware.this,MainActivity.class);
                            startActivity(i);
                        }

                    }catch (NullPointerException ne){
                        Log.e("Configure", ne.getMessage());
                        Toast.makeText(getApplicationContext(),"System Error Occured",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ConfigureHardware.this,MainActivity.class);
                        startActivity(i);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean ValidateUI(String id, String des) {
        //verify id
        if(TextUtils.isEmpty(id)){
            e_id.setError("Please Enter Device ID");
            e_id.requestFocus();
            return false;
        }

        //verify description
        if(TextUtils.isEmpty(des)){
            e_des.setError("Please descibe where will place the sensor unit");
            e_des.requestFocus();
            return false;
        }
        return true;
    }
}
