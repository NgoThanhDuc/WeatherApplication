package com.example.weatherapp.models;

public class DetailsDaily {

   /* private int icon;*/
    private String icon;
    private String desc;
    private String tempMorn;
    private String tempEve;
    private String tempDay;
    private String tempNight;
    private String feelsLikeMorn;
    private String feelsLikeEve;
    private String feelsLikeDay;
    private String feelsLikeNight;
    private String cloud;
    private String rain;
    private String dewPoint;
    private String uv;
    private String dateDetailsDaily;

    public DetailsDaily(String icon, String desc, String tempMorn, String tempEve, String tempDay,
                        String tempNight, String feelsLikeMorn, String feelsLikeEve, String feelsLikeDay,
                        String feelsLikeNight, String cloud, String rain, String dewPoint, String uv, String dateDetailsDaily) {
        this.icon = icon;
        this.desc = desc;
        this.tempMorn = tempMorn;
        this.tempEve = tempEve;
        this.tempDay = tempDay;
        this.tempNight = tempNight;
        this.feelsLikeMorn = feelsLikeMorn;
        this.feelsLikeEve = feelsLikeEve;
        this.feelsLikeDay = feelsLikeDay;
        this.feelsLikeNight = feelsLikeNight;
        this.cloud = cloud;
        this.rain = rain;
        this.dewPoint = dewPoint;
        this.uv = uv;
        this.dateDetailsDaily = dateDetailsDaily;
    }

    public String getIcon() {
        return icon;
    }

    public String getDesc() {
        return desc;
    }

    public String getTempMorn() {
        return tempMorn;
    }

    public String getTempEve() {
        return tempEve;
    }

    public String getTempDay() {
        return tempDay;
    }

    public String getTempNight() {
        return tempNight;
    }

    public String getFeelsLikeMorn() {
        return feelsLikeMorn;
    }

    public String getFeelsLikeEve() {
        return feelsLikeEve;
    }

    public String getFeelsLikeDay() {
        return feelsLikeDay;
    }

    public String getFeelsLikeNight() {
        return feelsLikeNight;
    }

    public String getCloud() {
        return cloud;
    }

    public String getRain() {
        return rain;
    }

    public String getDewPoint() {
        return dewPoint;
    }

    public String getUv() {
        return uv;
    }

    public String getDateDetailsDaily() {
        return dateDetailsDaily;
    }
}
