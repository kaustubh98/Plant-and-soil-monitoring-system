package com.example.plantmonitoringsystem.SupportClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.plantmonitoringsystem.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ParameterListAdapter extends ArrayAdapter {

    private ArrayList<Long> timeStamp;

    //constructor for calling the class
    public ParameterListAdapter(@NonNull Context context, ArrayList<String> parameterList, ArrayList<Long> countList) {
        super(context,0, parameterList);
        timeStamp = countList;
        Log.e("Count", "ParameterListAdapter timeStamp: "+ timeStamp.size() );
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
        TextView dateView = listView.findViewById(R.id.dateView);
        String parameter = (String) getItem(position);
        textView.setText(parameter);

        //get the time and date
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        String time = format.format(new Date(timeStamp.get(position)));
        DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy");
        String date = dateFormat.format(new Date(timeStamp.get(position)));

        //String timeStamp = "Past Record";
        timeView.setText(time);
        dateView.setText(date);

        return listView;
    }
}
