package com.example.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.SummaryRecyclerViewClickInterface;
import com.example.weatherapp.models.SummaryDaily;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SummaryDailyAdapter extends RecyclerView.Adapter<SummaryDailyAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<SummaryDaily> summaryDailyList;
    private SummaryRecyclerViewClickInterface summaryRecyclerViewClickInterface;

    public SummaryDailyAdapter(Context context, ArrayList<SummaryDaily> summaryDailyList, SummaryRecyclerViewClickInterface summaryRecyclerViewClickInterface) {
        this.context = context;
        this.summaryDailyList = summaryDailyList;
        this.summaryRecyclerViewClickInterface = summaryRecyclerViewClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_fragment_summary_daily, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txt_daySummaryDaily.setText(summaryDailyList.get(position).getDay());
        holder.txt_tempMaxSummaryDaily.setText(summaryDailyList.get(position).getTempMax());
        holder.txt_tempMinSummaryDaily.setText(summaryDailyList.get(position).getTemMin());
        holder.txt_descSummaryDaily.setText(summaryDailyList.get(position).getDesc());
        Picasso.with(context).load("http://openweathermap.org/img/wn/" + summaryDailyList.get(position).getImage() + ".png")
                .into(holder.imageViewSummaryDaily);
    }

    @Override
    public int getItemCount() {
        return summaryDailyList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_daySummaryDaily, txt_tempMaxSummaryDaily, txt_tempMinSummaryDaily, txt_descSummaryDaily;
        private ImageView imageViewSummaryDaily;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_daySummaryDaily = itemView.findViewById(R.id.txt_daySummaryDaily);
            txt_tempMaxSummaryDaily = itemView.findViewById(R.id.txt_tempMaxSummaryDaily);
            txt_tempMinSummaryDaily = itemView.findViewById(R.id.txt_tempMinSummaryDaily);
            txt_descSummaryDaily = itemView.findViewById(R.id.txt_descSummaryDaily);
            imageViewSummaryDaily = itemView.findViewById(R.id.imageViewSummaryDaily);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    summaryRecyclerViewClickInterface.onItemClickSummary(getAdapterPosition());
                }
            });
        }
    }

}
