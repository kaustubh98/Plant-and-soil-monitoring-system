package com.example.plantmonitoringsystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plantmonitoringsystem.SupportClasses.ParameterListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParameterList extends AppCompatActivity {

    ArrayList<String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_list);

        ArrayList<String> parameterList = new ArrayList<String >();
        String parameter;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            params = extras.getStringArrayList("DataList");
            Log.e("Parameters","Parameters are: "+params);
            //parameterList = convertToList();
            parameter = extras.getString("Parameter");

            try {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(parameter);
            }catch (NullPointerException ne){
                ne.printStackTrace();
            }

            ListView listView = findViewById(R.id.parameterList);
            ParameterListAdapter adapter = new ParameterListAdapter(this,params);
            listView.setAdapter(adapter);

            }else {
            Toast.makeText(this,"Could not load the data due to system error",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }



    }
}
