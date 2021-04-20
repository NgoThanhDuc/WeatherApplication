package com.example.weatherapp.models;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MultiView {

    public static final int NOW_FRAGMENT_DETAIL = 0;
    public static final int NOW_FRAGMENT_WARS = 1;
    public static final int NOW_FRAGMENT_SUN = 2;
    public static final int NOW_FRAGMENT_RAIN_CHART = 3;

    private int viewType;

    public MultiView(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    // used row_fragment_detail
    private String feelsLike;
    private String humidity;
    private String visibility;
    private String uv;

    public MultiView(int viewType, String feelsLike, String humidity, String visibility, String uv) {
        this.viewType = viewType;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.visibility = visibility;
        this.uv = uv;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getUv() {
        return uv;
    }

    // used row_fragment_detail

    //-------------------------------------------------------------------------------------------------------------------------------------

    // used row_fragment_wars
    private String windSpeed;
    private String windDirection;
    private String windGust;
    private String atmosphericPressure;
    private String atmosphericTemperature;
    private String rainVolume;
    private String snowVolume;

    public MultiView(int viewType, String windSpeed, String windDirection, String windGust, String atmosphericPressure, String atmosphericTemperature, String rainVolume, String snowVolume) {
        this.viewType = viewType;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.windGust = windGust;
        this.atmosphericPressure = atmosphericPressure;
        this.atmosphericTemperature = atmosphericTemperature;
        this.rainVolume = rainVolume;
        this.snowVolume = snowVolume;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindGust() {
        return windGust;
    }

    public String getAtmosphericPressure() {
        return atmosphericPressure;
    }

    public String getAtmosphericTemperature() {
        return atmosphericTemperature;
    }

    public String getRainVolume() {
        return rainVolume;
    }

    public String getSnowVolume() {
        return snowVolume;
    }

    // used row_fragment_war

    //----------------------------------------------------------------------------------------------------------------------------------

    // used row_fragment_sun
    private String sunrise;
    private String sunset;
    private int setMax;
    private int setProgress;
    private int imageSunrise;
    private int imageSunset;

    public MultiView(int viewType, String sunrise, String sunset, int setMax, int setProgress, int imageSunrise, int imageSunset) {
        this.viewType = viewType;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.setMax = setMax;
        this.setProgress = setProgress;
        this.imageSunrise = imageSunrise;
        this.imageSunset = imageSunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public int getSetMax() {
        return setMax;
    }

    public int getSetProgress() {
        return setProgress;
    }

    public int getImageSunrise() {
        return imageSunrise;
    }

    public int getImageSunset() {
        return imageSunset;
    }

    // used row_fragment_sun

    //----------------------------------------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------------------------
    // used row_fragment_rain_chart
    private ArrayList<String> xAxisData;
    private ArrayList<BarEntry> valueSet;

    public MultiView(int viewType, ArrayList<String> xAxisData, ArrayList<BarEntry> valueSet) {
        this.viewType = viewType;
        this.xAxisData = xAxisData;
        this.valueSet = valueSet;
    }

    public ArrayList<String> getxAxisData() {
        return xAxisData;
    }

    public ArrayList<BarEntry> getValueSet() {
        return valueSet;
    }

    // used row_fragment_rain_chart
    //----------------------------------------------------------------------------------------------------------------------------------

}
