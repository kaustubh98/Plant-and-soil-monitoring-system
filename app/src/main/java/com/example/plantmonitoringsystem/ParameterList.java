package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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

public class ParameterList extends AppCompatActivity {

    ArrayList<String> params = new ArrayList<String>();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_list);

        //toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar_param);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final ArrayList<Long> timestamp = new ArrayList<>();
        String parameter;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            try {
            parameter = extras.getString("Parameter");

            //get the detailed list
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference(user.getUid());
            reference.child("Average/"+parameter).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String temporary = dataSnapshot.getValue(String.class);
                    String value = temporary.split("_")[0];
                    Long mTimeStamp = Long.valueOf(temporary.split("_")[1]);
                    params.add(0,value);
                    timestamp.add(mTimeStamp);

                    ListView listView = findViewById(R.id.parameterList);
                    ParameterListAdapter adapter = new ParameterListAdapter(getApplicationContext(),params,timestamp);
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

            actionBar = getSupportActionBar();

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
