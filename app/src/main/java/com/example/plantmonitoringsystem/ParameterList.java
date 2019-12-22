package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plantmonitoringsystem.SupportClasses.ParameterListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParameterList extends AppCompatActivity {

    ArrayList<String> params = new ArrayList<String>();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_list);

        final ArrayList<Integer> count = new ArrayList<Integer>();
        String parameter;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            try {
            parameter = extras.getString("Parameter");

            //get the detailed list
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference(user.getUid());
            reference.child(parameter).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Float temporary = dataSnapshot.getValue(Float.class);
                    params.add(0,String.valueOf(temporary));

                    //get the count
                    count.clear();
                    for(int i=0;i<params.size();i++){
                        count.add(i);
                    }

                    ListView listView = findViewById(R.id.parameterList);
                    ParameterListAdapter adapter = new ParameterListAdapter(getApplicationContext(),params,count);
                    listView.setAdapter(adapter);

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

            ActionBar actionBar = getSupportActionBar();

            switch (parameter){
                case "Temperature":
                    actionBar.setTitle("Temperature");
                    break;
                case "Humidity":
                    actionBar.setTitle("Humidity");
                    break;
                case "Moisture":
                    actionBar.setTitle("Soil Moisture Level");
                    break;
                case "LightIntensity":
                    actionBar.setTitle("Light Intensity");
                    break;

            }

            }catch (NullPointerException ne){
                ne.printStackTrace();
            }
            }else {
            Toast.makeText(this,"Could not load the data due to system error",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }



    }
}
