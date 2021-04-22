package com.example.weatherapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.MultiView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.marcinmoskala.arcseekbar.ArcSeekBar;
import java.util.ArrayList;
import java.util.List;

import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_DETAIL;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_WARS;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_SUN;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_RAIN_CHART;

public class MultiViewAdapter extends RecyclerView.Adapter {

    private List<MultiView> multiViewList;

    public MultiViewAdapter(List<MultiView> multiViewList) {
        this.multiViewList = multiViewList;
    }

    @Override
    public int getItemViewType(int position) {

        switch (multiViewList.get(position).getViewType()) {
            case 0:
                return NOW_FRAGMENT_DETAIL;
            case 1:
                return NOW_FRAGMENT_WARS;
            case 2:
                return NOW_FRAGMENT_SUN;
            case 3:
                return NOW_FRAGMENT_RAIN_CHART;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case NOW_FRAGMENT_DETAIL:
                View nowFragmentDetail = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.now_fragment_detail, viewGroup, false);
                return new NowFragmentDetailHolder(nowFragmentDetail);

            case NOW_FRAGMENT_WARS:
                View nowFragmentWars = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.now_fragment_wars, viewGroup, false);
                return new NowFragmentWarsHolder(nowFragmentWars);

            case NOW_FRAGMENT_SUN:
                View nowFragmentSun = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.now_fragment_sun, viewGroup, false);
                return new NowFragmentSunlHolder(nowFragmentSun);

            case NOW_FRAGMENT_RAIN_CHART:
                View nowFragmentRainChart = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.now_fragment_rain_chart, viewGroup, false);
                return new NowFragmentRainChartHolder(nowFragmentRainChart);
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (multiViewList.get(position).getViewType()) {
            case NOW_FRAGMENT_DETAIL:
                String feelsLike = multiViewList.get(position).getFeelsLike();
                String humidity = multiViewList.get(position).getHumidity();
                String visibility = multiViewList.get(position).getVisibility();
                String uv = multiViewList.get(position).getUv();
                ((NowFragmentDetailHolder) holder).setDataNowFragmentDetail(feelsLike, humidity, visibility, uv);
                break;

            case NOW_FRAGMENT_WARS:
                String windSpeed = multiViewList.get(position).getWindSpeed();
                String windDirection = multiViewList.get(position).getWindDirection();
                String windGust = multiViewList.get(position).getWindGust();
                String atmosphericPressure = multiViewList.get(position).getAtmosphericPressure();
                String atmosphericTemperature = multiViewList.get(position).getAtmosphericTemperature();
                String rainVolume = multiViewList.get(position).getRainVolume();
                String snowVolume = multiViewList.get(position).getSnowVolume();
                ((NowFragmentWarsHolder) holder).setDataNowFragmentWars(windSpeed, windDirection, windGust, atmosphericPressure, atmosphericTemperature, rainVolume, snowVolume);
                break;

            case NOW_FRAGMENT_SUN:
                String sunrise = multiViewList.get(position).getSunrise();
                String sunset = multiViewList.get(position).getSunset();
                int setMax = multiViewList.get(position).getSetMax();
                int setProgress = multiViewList.get(position).getSetProgress();
                int imageSunrise = multiViewList.get(position).getImageSunrise();
                int imageSunset = multiViewList.get(position).getImageSunset();
                ((NowFragmentSunlHolder) holder).setDataNowFragmentSun(sunrise, sunset, setMax, setProgress, imageSunrise, imageSunset);
                break;

            case NOW_FRAGMENT_RAIN_CHART:
                ArrayList<String> xAxisData = multiViewList.get(position).getxAxisData();
                ArrayList<BarEntry> valueSet = multiViewList.get(position).getValueSet();
                ((NowFragmentRainChartHolder) holder).setDataNowFragmentRainChart(xAxisData, valueSet);
                break;

            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return multiViewList.size();
    }

    //ViewHolder
    class NowFragmentDetailHolder extends RecyclerView.ViewHolder {

        private TextView txt_feelsLike, txt_humidity, txt_visibility, txt_uv;

        public NowFragmentDetailHolder(@NonNull View itemView) {
            super(itemView);
            txt_feelsLike = itemView.findViewById(R.id.txt_feelsLike);
            txt_humidity = itemView.findViewById(R.id.txt_humidity);
            txt_visibility = itemView.findViewById(R.id.txt_visibility);
            txt_uv = itemView.findViewById(R.id.txt_uv);
        }

        private void setDataNowFragmentDetail(String feelsLike, String humidity, String visibility, String uv) {
            txt_feelsLike.setText(feelsLike);
            txt_humidity.setText(humidity);
            txt_visibility.setText(visibility);
            txt_uv.setText(uv);
        }
    }

    class NowFragmentWarsHolder extends RecyclerView.ViewHolder {

        private TextView txt_windSpeed, txt_windDirection, txt_windGust, txt_atmosphericPressure, txt_atmosphericTemperature, txt_rainVolume, txt_snowVolume;

        public NowFragmentWarsHolder(@NonNull View itemView) {
            super(itemView);
            txt_windSpeed = itemView.findViewById(R.id.txt_windSpeed);
            txt_windDirection = itemView.findViewById(R.id.txt_windDirection);
            txt_windGust = itemView.findViewById(R.id.txt_windGust);
            txt_atmosphericPressure = itemView.findViewById(R.id.txt_atmosphericPressure);
            txt_atmosphericTemperature = itemView.findViewById(R.id.txt_atmosphericTemperature);
            txt_rainVolume = itemView.findViewById(R.id.txt_rainVolume);
            txt_snowVolume = itemView.findViewById(R.id.txt_snowVolume);
        }

        private void setDataNowFragmentWars(String windSpeed, String windDirection, String windGust, String atmosphericPressure, String atmosphericTemperature, String rainVolume, String snowVolume) {
            txt_windSpeed.setText(windSpeed);
            txt_windDirection.setText(windDirection);
            txt_windGust.setText(windGust);
            txt_atmosphericPressure.setText(atmosphericPressure);
            txt_atmosphericTemperature.setText(atmosphericTemperature);
            txt_rainVolume.setText(rainVolume);
            txt_snowVolume.setText(snowVolume);
        }
    }

    class NowFragmentSunlHolder extends RecyclerView.ViewHolder {

        private TextView txt_sunrise, txt_sunset;
        private ArcSeekBar arcSeekBarSun;
        private ImageView img_sunrise, img_sunset;

        public NowFragmentSunlHolder(@NonNull View itemView) {
            super(itemView);
            txt_sunrise = itemView.findViewById(R.id.txt_sunrise);
            txt_sunset = itemView.findViewById(R.id.txt_sunset);
            arcSeekBarSun = itemView.findViewById(R.id.arcSeekBarSun);
            img_sunrise = itemView.findViewById(R.id.img_sunrise);
            img_sunset = itemView.findViewById(R.id.img_sunset);

        }

        private void setDataNowFragmentSun(String sunrise, String sunset, int setMax, int setProgress, int imageSunrise, int imageSunset) {

            txt_sunrise.setText(sunrise);
            txt_sunset.setText(sunset);
            arcSeekBarSun.setMaxProgress(setMax);
            arcSeekBarSun.setProgress(setProgress);
            img_sunrise.setImageResource(imageSunrise);
            img_sunset.setImageResource(imageSunset);
        }

    }

    class NowFragmentRainChartHolder extends RecyclerView.ViewHolder {

        private BarChart chart;

        public NowFragmentRainChartHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.chart);

        }

        private void setDataNowFragmentRainChart(ArrayList<String> xAxisData, ArrayList<BarEntry> valueSet) {

            // trục x
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawLabels(true);
            xAxis.setTextSize(13f);

            // trục y bên trái
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setAxisMaxValue(8f); // số tối đa của trục y bên trái
            leftAxis.setDrawTopYLabelEntry(false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawZeroLine(true);
            leftAxis.setDrawAxisLine(true);
            leftAxis.setTextSize(15f); //15

            // trục y bên phải
            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawAxisLine(false);
            rightAxis.setTextColor(Color.WHITE);
            rightAxis.setDrawGridLines(false);

            // description ở dưới char
            BarDataSet barDataSet = new BarDataSet(valueSet, "");
            barDataSet.setColor(Color.BLUE);
            barDataSet.setValueTextSize(15f); // textSize trên cột

            // set data vào barchart
            BarData data = new BarData(xAxisData, barDataSet);
            chart.setData(data);

            chart.setDescription("");
            chart.setNoDataTextDescription("No data");
            chart.setTouchEnabled(true); // enable touch gestures
            chart.setDragEnabled(true);
            chart.setHorizontalScrollBarEnabled(true);
            chart.setScrollbarFadingEnabled(true);
            chart.setScrollContainer(true);
            chart.setScaleEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setHighlightPerDragEnabled(true);
            chart.getLegend().setEnabled(false);   // Hide the legend
            chart.setBackgroundColor(Color.WHITE);
            chart.setVisibleXRangeMaximum(5); // allow 5 values to be displayed
            chart.moveViewToX(-1);// set the left edge of the chart to x-index 1
            chart.animateXY(2000, 2000);
            chart.invalidate();

        }
    }
}
