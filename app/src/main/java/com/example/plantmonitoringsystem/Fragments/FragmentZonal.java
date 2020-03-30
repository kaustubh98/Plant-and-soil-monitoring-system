package com.example.plantmonitoringsystem.Fragments;

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

import com.example.plantmonitoringsystem.R;
import com.example.plantmonitoringsystem.SupportClasses.CardViewAdapter;
import com.example.plantmonitoringsystem.SupportClasses.ZoneDisplayAdapter;
import com.example.plantmonitoringsystem.ZonalDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FragmentZonal extends Fragment {

    private ArrayList<Float> Llist = new ArrayList<Float>();
    private ArrayList<Float> Tlist = new ArrayList<Float>();
    private ArrayList<Float> Hlist = new ArrayList<Float>();
    private ArrayList<Float> Mlist = new ArrayList<Float>();
    private ArrayList<String> desList = new ArrayList<String>();
    private DatabaseReference reference;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private Float moisture;
    private Float humidity;
    private Float temperature;
    private Float Light;

    public FragmentZonal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_zonal, container, false);
        recyclerView = root.findViewById(R.id.recyclerView_zones);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(user.getUid()+"/Zones");

        Tlist.clear();
        Hlist.clear();
        Llist.clear();
        Mlist.clear();
        desList.clear();


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                DataSnapshot humid = dataSnapshot.child("Humidity");
                DataSnapshot temp = dataSnapshot.child("Temperature");
                DataSnapshot light = dataSnapshot.child("LightIntensity");
                DataSnapshot moitst = dataSnapshot.child("Moisture");

                for (DataSnapshot child : humid.getChildren()) {
                    humidity = child.getValue(Float.class);
                }

                for (DataSnapshot child : temp.getChildren()) {
                    temperature = child.getValue(Float.class);
                }

                for (DataSnapshot child : moitst.getChildren()) {
                    moisture = child.getValue(Float.class);
                }

                for (DataSnapshot child : light.getChildren()) {
                    Light = child.getValue(Float.class);
                }

                Log.e(TAG, "onChildAdded: " + humidity);
                Tlist.add(temperature);
                Hlist.add(humidity);
                Llist.add(Light);
                Mlist.add(moisture);
                desList.add(dataSnapshot.child("Description").getValue(String.class));

                //load the recycler view
                ZoneDisplayAdapter adapter = new ZoneDisplayAdapter(Tlist, Hlist, Mlist, Llist, desList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

                adapter.setListener(new ZoneDisplayAdapter.Listener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(getContext(), ZonalDetails.class);
                        intent.putExtra("Title",desList.get(position));
                        int temp = position+1;
                        intent.putExtra("ZoneID","zone"+temp);
                        startActivity(intent);
                    }
                });

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

        return root;
    }


}
