package com.example.weatherapp.models;

public class HourlyWeather {

    private String timeHourly;
    private String imageHourly;
    private String tempHourly;

    public HourlyWeather(String timeHourly, String imageHourly, String tempHourly) {
        this.timeHourly = timeHourly;
        this.imageHourly = imageHourly;
        this.tempHourly = tempHourly;
    }

    public String getTimeHourly() {
        return timeHourly;
    }

    public String getImageHourly() {
        return imageHourly;
    }

    public String getTempHourly() {
        return tempHourly;
    }
}
