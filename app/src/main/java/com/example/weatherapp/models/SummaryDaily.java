package com.example.weatherapp.models;

public class SummaryDaily {

    private String day;
    private String image;
    private String tempMax;
    private String temMin;
    private String desc;

    public SummaryDaily(String day, String image, String tempMax, String temMin, String desc) {
        this.day = day;
        this.image = image;
        this.tempMax = tempMax;
        this.temMin = temMin;
        this.desc = desc;
    }

    public String getDay() {
        return day;
    }

    public String getImage() {
        return image;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getTemMin() {
        return temMin;
    }

    public String getDesc() {
        return desc;
    }
}
