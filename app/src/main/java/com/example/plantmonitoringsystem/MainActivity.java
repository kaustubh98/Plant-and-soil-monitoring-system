package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.plantmonitoringsystem.SupportClasses.CardViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        Log.e("PopulateView","Populating View");

        try {
            Log.e("PopulateView","Listening for Parameters");
            //check for hardware configuration configuration
            reference.child("Temperature").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        ListenForParameter("Moisture");
                        ListenForParameter("Temperature");
                        ListenForParameter("LightIntensity");
                        ListenForParameter("Humidity");

                    }else{
                        progressDialog.dismiss();
                        recyclerView.setVisibility(View.GONE);
                        nullView.setVisibility(View.VISIBLE);
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

        reference.child(parameter).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    Log.e("Message",dataSnapshot.getValue().toString());
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
                    Log.e("ValueCheck", "onDataChange: "+values.get(0) );
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

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /**this method is called is when something has changed in the data. Our project will only push the data and not update it
                 * Hence, any event like this should be regarded as an error and ignored
                 */
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