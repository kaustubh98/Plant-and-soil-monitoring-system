package com.example.plantmonitoringsystem.SupportClasses;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.plantmonitoringsystem.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ZoneDisplayAdapter extends RecyclerView.Adapter<ZoneDisplayAdapter.ViewHolder> {

    private ArrayList<Float> Tlist, Hlist, Mlist, Llist;
    private ArrayList<String> desList;

    public ZoneDisplayAdapter(ArrayList<Float> tlist, ArrayList<Float> hlist, ArrayList<Float> mlist, ArrayList<Float> llist, ArrayList<String> desList) {
        Tlist = tlist;
        Hlist = hlist;
        Mlist = mlist;
        Llist = llist;
        this.desList = desList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //instantiate card viewholder
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_zone,parent,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the cardview
        CardView cardView = holder.cardView;

        //get the corresponding text views
        TextView temperature = cardView.findViewById(R.id.text_temperature);
        TextView humidity = cardView.findViewById(R.id.text_humidity);
        TextView moisture = cardView.findViewById(R.id.text_soil);
        TextView light = cardView.findViewById(R.id.text_light);
        TextView description = cardView.findViewById(R.id.text_description);

        //load the views
        temperature.setText(String.valueOf(Tlist.get(position)));
        humidity.setText(String.valueOf(Hlist.get(position)));
        moisture.setText(String.valueOf(Mlist.get(position)));
        light.setText(String.valueOf(Llist.get(position)));
        description.setText(desList.get(position));

    }

    @Override
    public int getItemCount() {
        return desList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
