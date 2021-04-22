package com.example.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.interfaces.DetailsRecyclerViewClickInterface;
import com.example.weatherapp.R;
import com.example.weatherapp.models.DetailsDaily;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsDailyAdapter extends RecyclerView.Adapter<DetailsDailyAdapter.MyViewHoler> {

    private Context context;
    private ArrayList<DetailsDaily> detailsDailyList;
    private DetailsRecyclerViewClickInterface detailsRecyclerViewClickInterface;

    public DetailsDailyAdapter(Context context, ArrayList<DetailsDaily> detailsDailyList, DetailsRecyclerViewClickInterface detailsRecyclerViewClickInterface) {
        this.context = context;
        this.detailsDailyList = detailsDailyList;
        this.detailsRecyclerViewClickInterface = detailsRecyclerViewClickInterface;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_fragment_details_daily, parent, false);
        return new MyViewHoler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {

        holder.txtx_dateDetailsDaily.setText(detailsDailyList.get(position).getDateDetailsDaily());

        Picasso.with(context).load("http://openweathermap.org/img/wn/" + detailsDailyList.get(position).getIcon() + ".png")
                .into(holder.imageDetailsDaily);

        holder.txt_descDetailDaily.setText(detailsDailyList.get(position).getDesc());

        holder.txt_tempMornDetailsDaily.setText(detailsDailyList.get(position).getTempMorn());
        holder.txt_tempEveDetailsDaily.setText(detailsDailyList.get(position).getTempEve());
        holder.txt_tempDayDetailsDaily.setText(detailsDailyList.get(position).getTempDay());
        holder.txt_tempNightDetailsDaily.setText(detailsDailyList.get(position).getTempNight());

        holder.txt_feelsLikeMornDetailsDaily.setText(detailsDailyList.get(position).getFeelsLikeMorn());
        holder.txt_feelsLikeEveDetailsDaily.setText(detailsDailyList.get(position).getFeelsLikeEve());
        holder.txt_feelsLikeDayDetailsDaily.setText(detailsDailyList.get(position).getFeelsLikeDay());
        holder.txt_feelsLikeNightDetailsDaily.setText(detailsDailyList.get(position).getFeelsLikeNight());

        holder.txt_cloudDetailsDaily.setText(detailsDailyList.get(position).getCloud());
        holder.txt_rainDetailsDaily.setText(detailsDailyList.get(position).getRain());
        holder.txt_dewPointDetailsDaily.setText(detailsDailyList.get(position).getDewPoint());
        holder.txt_uvDetailsDaily.setText(detailsDailyList.get(position).getUv());
    }

    @Override
    public int getItemCount() {
        return detailsDailyList.size();
    }

    public class MyViewHoler extends RecyclerView.ViewHolder {

        TextView txtx_dateDetailsDaily;
        ImageView imageDetailsDaily;
        TextView txt_descDetailDaily;
        TextView txt_tempMornDetailsDaily, txt_tempEveDetailsDaily, txt_tempDayDetailsDaily, txt_tempNightDetailsDaily;
        TextView txt_feelsLikeMornDetailsDaily, txt_feelsLikeEveDetailsDaily, txt_feelsLikeDayDetailsDaily, txt_feelsLikeNightDetailsDaily;
        TextView txt_cloudDetailsDaily, txt_rainDetailsDaily, txt_dewPointDetailsDaily, txt_uvDetailsDaily;

        public MyViewHoler(@NonNull final View itemView) {
            super(itemView);

            txtx_dateDetailsDaily = itemView.findViewById(R.id.txt_dateDetailsDaily);

            imageDetailsDaily = itemView.findViewById(R.id.imageDetailsDaily);
            txt_descDetailDaily = itemView.findViewById(R.id.txt_descDetailDaily);

            txt_tempMornDetailsDaily = itemView.findViewById(R.id.txt_tempMornDetailsDaily);
            txt_tempEveDetailsDaily = itemView.findViewById(R.id.txt_tempEveDetailsDaily);
            txt_tempDayDetailsDaily = itemView.findViewById(R.id.txt_tempDayDetailsDaily);
            txt_tempNightDetailsDaily = itemView.findViewById(R.id.txt_tempNightDetailsDaily);

            txt_feelsLikeMornDetailsDaily = itemView.findViewById(R.id.txt_feelsLikeMornDetailsDaily);
            txt_feelsLikeEveDetailsDaily = itemView.findViewById(R.id.txt_feelsLikeEveDetailsDaily);
            txt_feelsLikeDayDetailsDaily = itemView.findViewById(R.id.txt_feelsLikeDayDetailsDaily);
            txt_feelsLikeNightDetailsDaily = itemView.findViewById(R.id.txt_feelsLikeNightDetailsDaily);

            txt_cloudDetailsDaily = itemView.findViewById(R.id.txt_cloudDetailsDaily);
            txt_rainDetailsDaily = itemView.findViewById(R.id.txt_rainDetailsDaily);
            txt_dewPointDetailsDaily = itemView.findViewById(R.id.txt_dewPointDetailsDaily);
            txt_uvDetailsDaily = itemView.findViewById(R.id.txt_uvDetailsDaily);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailsRecyclerViewClickInterface.onItemClickDetails(getAdapterPosition());
                }
            });
        }
    }
}
