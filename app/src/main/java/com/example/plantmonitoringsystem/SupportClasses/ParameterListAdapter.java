package com.example.plantmonitoringsystem.SupportClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.plantmonitoringsystem.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ParameterListAdapter extends ArrayAdapter {

    ArrayList<Integer> count;

    //constructor for calling the class
    public ParameterListAdapter(@NonNull Context context, ArrayList<String> parameterList,ArrayList<Integer> countList) {
        super(context,0, parameterList);
        count = countList;
        Log.e("Count", "ParameterListAdapter count: "+count.size() );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listView = convertView;
        //Return a view when required. This is done to recycle the views
        if(listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.parameter_list, parent, false);
        }

        TextView textView = listView.findViewById(R.id.value);
        TextView timeView = listView.findViewById(R.id.time);
        String parameter = (String) getItem(position);
        textView.setText(parameter);
        String time = "Before "+count.get(position)+ " hours";
        //String time = "Past Record";
        timeView.setText(time);

        return listView;
    }
}
