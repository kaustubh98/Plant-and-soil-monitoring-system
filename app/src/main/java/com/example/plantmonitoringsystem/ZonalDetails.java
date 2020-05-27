package com.example.plantmonitoringsystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.plantmonitoringsystem.SupportClasses.ZonalDetailAdapater;
import com.google.android.material.tabs.TabLayout;

public class ZonalDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zonal_details);

        //setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarZonal);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String zoneID = null;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            String title = extras.getString("Title");
            zoneID = extras.getString("ZoneID");
            Log.e("zonalDetails", "ZoneID: "+zoneID );
            actionBar.setTitle(title);
        }

        //set up the fragments
        ZonalDetailAdapater adapter = new ZonalDetailAdapater(getSupportFragmentManager(),getBaseContext(),zoneID);
        ViewPager viewPager = findViewById(R.id.view_pager_Zonal);
        TabLayout tabLayout = findViewById(R.id.tabsZonal);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
