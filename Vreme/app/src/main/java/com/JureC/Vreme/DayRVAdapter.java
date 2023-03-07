package com.JureC.Vreme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DayRVAdapter extends RecyclerView.Adapter<DayRVAdapter.ViewHolder> {

     private Context Daycontext;

     private ArrayList<DayRVModal> dayRVModalArrayList;

    public DayRVAdapter(Context daycontext, ArrayList<DayRVModal> dayRVModalArrayList) {

        Daycontext = daycontext;

        this.dayRVModalArrayList = dayRVModalArrayList;

    }

    @NonNull
    @Override
    public DayRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(Daycontext).inflate(R.layout.day_rv_item,parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DayRVAdapter.ViewHolder holder, int position) {

        DayRVModal modal = dayRVModalArrayList.get(position);

        holder.temperatureRVTV.setText(modal.getTemperature() + " °C");

        holder.rainRVTV.setText(modal.getRain() + "%");

        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionRVIV);

        String weekC = modal.getDay();

        // Priprava formatov datuma in časa

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat outputA = new SimpleDateFormat("EEEE");

        SimpleDateFormat outputB = new SimpleDateFormat("d.M");

        String day = "", week2 = "";

        // Pridobivanje angleškega dneva v tednu in datuma

        try {

            Date weekA = input.parse(weekC);

            day = outputA.format(weekA);

            Date weekB = input.parse(weekC);

            week2 = outputB.format(weekB);

        } catch (ParseException e) {

            e.printStackTrace();

        }
        //

        String[] week = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        String[] teden = {"Ponedeljek", "Torek", "Sreda", "Četrtek", "Petek", "Sobota", "Nedelja"};

        for (int i = 0; i < week.length; i++) {

            if (week[i].equals(day)) {

                day = teden[i];

            }

        }

        day = day + ", " + week2;

        holder.dayRVTV.setText(day);

    }

    @Override
    public int getItemCount() {

        return dayRVModalArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dayRVTV, rainRVTV, temperatureRVTV;
        private ImageView conditionRVIV;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            dayRVTV = itemView.findViewById(R.id.idRVTVDay);
            rainRVTV = itemView.findViewById(R.id.idRVTVRain);
            temperatureRVTV = itemView.findViewById(R.id.idRVTVTemperature);
            conditionRVIV = itemView.findViewById(R.id.idRVIVCondition);

        }

    }

}
