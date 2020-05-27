package com.example.plantmonitoringsystem.SupportClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantmonitoringsystem.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {


    //private String temp = Context.getString(R.string.Temperature);

    private ArrayList<String> parameter = new ArrayList<>();
    private ArrayList<String> value;
    private int[] icon = {R.drawable.temperature_icon,R.drawable.humidity,R.drawable.soilmoistureicon,R.drawable.lightintensityicon};
    private Listener listener;

    //interface for implementing clicks
    public interface Listener{
        void onClick(int position);
    }

    public CardViewAdapter(Context context,ArrayList<String> value) {

        this.value = value;
        parameter.add(context.getResources().getString(R.string.Temperature));
        parameter.add(context.getResources().getString(R.string.Humidity));
        parameter.add(context.getResources().getString(R.string.Moisture));
        parameter.add(context.getResources().getString(R.string.LightIntensity));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //code to instantiate view holder
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_parameter,parent,false);
        return new ViewHolder(cv);
    }

    //activities and fragments will use this method to register as a listener
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //set the cardview in the viewholder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        //get the card view
        CardView cardView = holder.cardView;

        //load the image
        ImageView imageView = cardView.findViewById(R.id.icon_image);
        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(),icon[position]);
        imageView.setImageDrawable(drawable);

        //set the parameter name
        TextView textView = cardView.findViewById(R.id.parameter);
        textView.setText(parameter.get(position));

        //set the value
        TextView textView2 = cardView.findViewById(R.id.parametervalue);
        textView2.setText(value.get(position));

        //attach the listener
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
