package com.example.plantmonitoringsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class ParameterList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_list);

        ArrayList<Integer> parameterList;
        String parameter;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            parameterList = extras.getIntegerArrayList("DataList");
            parameter = extras.getString("Parameter");
            Toast.makeText(this,parameterList.toString(),Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Could not load the data due to system error",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }

    }
}
