package com.example.plantmonitoringsystem.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.plantmonitoringsystem.R;
import com.example.plantmonitoringsystem.SupportClasses.ParameterListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLight extends Fragment {

    ArrayList<String> params = new ArrayList<String>();
    private DatabaseReference reference;
    private ListView listView;
    private String zone;
    private ArrayList<Integer> count = new ArrayList<Integer>();

    public FragmentLight(String Zone) {
        // Required empty public constructor
        zone = Zone;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_light, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(user.getUid()+"/Zones/"+zone+"/LightIntensity");
        listView = root.findViewById(R.id.list_zone_light);
        params.clear();

        //populate the list
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Float temp = dataSnapshot.getValue(Float.class);
                params.add(0,String.valueOf(temp));

                //get the count
                count.clear();
                for(int i=0;i<params.size();i++){
                    count.add(i);
                }

                ParameterListAdapter adapter = new ParameterListAdapter(getContext(),params,count);
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
        return root;

    }

}
