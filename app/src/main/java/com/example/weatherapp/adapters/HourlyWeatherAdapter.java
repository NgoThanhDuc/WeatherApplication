package com.example.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.HourlyWeather;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HourlyWeather> hourlyWeatherArrayList;

    public HourlyWeatherAdapter(Context context, ArrayList<HourlyWeather> hourlyWeatherArrayList) {
        this.context = context;
        this.hourlyWeatherArrayList = hourlyWeatherArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.view_holder_hourly_weather_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_timeHourly.setText(hourlyWeatherArrayList.get(position).getTimeHourly());
        holder.txt_tempHourly.setText(hourlyWeatherArrayList.get(position).getTempHourly());
        Picasso.with(context).load("http://openweathermap.org/img/wn/" + hourlyWeatherArrayList.get(position).getImageHourly() + ".png")
                .into(holder.img_hourly);
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_timeHourly, txt_tempHourly;
        ImageView img_hourly;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_timeHourly = itemView.findViewById(R.id.txt_timeHourly);
            txt_tempHourly = itemView.findViewById(R.id.txt_tempHourly);
            img_hourly = itemView.findViewById(R.id.img_hourly);
        }
    }
}
