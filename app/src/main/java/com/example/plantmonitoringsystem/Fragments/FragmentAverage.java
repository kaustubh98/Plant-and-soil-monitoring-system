package com.example.plantmonitoringsystem.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantmonitoringsystem.MainActivity;
import com.example.plantmonitoringsystem.ParameterList;
import com.example.plantmonitoringsystem.R;
import com.example.plantmonitoringsystem.SupportClasses.CardViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentAverage extends Fragment {

    private ArrayList<String> values = new ArrayList<String>();
    private DatabaseReference reference;
    private static String units = "-1";
    private static String name;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private LinearLayout nullview;
    private static String moisture,humidity,temp,light;

    public FragmentAverage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_average, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        nullview = root.findViewById(R.id.NullView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(user.getUid());

        ListenForParameter("Moisture");
        ListenForParameter("Humidity");
        ListenForParameter("LightIntensity");
        ListenForParameter("Temperature");

        //getting number of units and name for storing in PDF
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int mUnits = dataSnapshot.child("NumberOfUnits").getValue(Integer.class);
                units = String.valueOf(mUnits);
                name = dataSnapshot.child("Name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return root;
    }

    //setup listeners from getting data from firebase
    private void ListenForParameter(final String parameter) {

        Log.e("PopulateView","Listening for "+parameter );

        reference.child("Average/" + parameter)
        .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    String t = dataSnapshot.getValue(String.class);
                    t = t.split("_")[0];
                    Log.e("Reading Value","Split: "+t);
                    final Intent i = new Intent(getContext(), ParameterList.class);
                    switch (parameter) {
                        case "Moisture":
                            moisture = t;
                            break;
                        case "Humidity":
                            humidity = t;
                            break;
                        case "Temperature":
                            temp = t;
                            break;
                        case "LightIntensity":
                            light = t;
                            break;
                    }

                    values.add(0, temp);
                    values.add(1, humidity);
                    values.add(2, moisture);
                    values.add(3, light);
                    CardViewAdapter adapter = new CardViewAdapter(getContext(),values);
                    recyclerView.setVisibility(View.VISIBLE);
                    nullview.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

                    //for handling click events
                    adapter.setListener(new CardViewAdapter.Listener() {
                        @Override
                        public void onClick(int position) {

                            switch (position) {
                                case 0:
                                    i.putExtra("Parameter", "Temperature");
                                    break;
                                case 1:
                                    i.putExtra("Parameter", "Humidity");
                                    break;
                                case 2:
                                    i.putExtra("Parameter", "Moisture");
                                    break;
                                case 3:
                                    i.putExtra("Parameter", "LightIntensity");
                                    break;
                            }
                            startActivity(i);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Listener", e.getMessage());
                }
            }

            /**
             * this method is called is when something has changed in the data. Our project will only push the data and not update it
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

    public static String getMoisture(){
        return moisture;
    }

    public static String getHumidity() {
        return humidity;
    }

    public static String getTemp() {
        return temp;
    }

    public static String getLight() {
        return light;
    }

    public static String getNumberOfUnits() {
        return units;
    }

    public static String getName(){
        return name;
    }


}
