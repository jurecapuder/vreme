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

import java.util.ArrayList;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private Context context;

    private ArrayList<WeatherRVModal> weatherRVModalArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherRVModalArrayList) {

        this.context = context;

        this.weatherRVModalArrayList = weatherRVModalArrayList;

    }

    @NonNull

    @Override

    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);

        return new ViewHolder(view);

    }

    @Override

    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {

        WeatherRVModal modal = weatherRVModalArrayList.get(position);

        holder.rainTV.setText(modal.getRain() + " mm");

        holder.temperatureTV.setText(modal.getTemperature()+" °C");

        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionIV);

        String time = modal.getTime();

        time = time.replace(':', ' ');

        String[] timeA = time.split(" ");

        time = (timeA[timeA.length - 2] + ":" + timeA[timeA.length - 1]);

        holder.timeTV.setText(time);

    }

    @Override

    public int getItemCount() {

        return weatherRVModalArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView temperatureTV, timeTV, rainTV;

        private ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            temperatureTV = itemView.findViewById(R.id.idTVTemperature);

            timeTV = itemView.findViewById(R.id.idTVTime);

            conditionIV = itemView.findViewById(R.id.idIVCondition);

            rainTV = itemView.findViewById(R.id.idTVRain);

        }

    }

}
