package com.example.weatherapp.models;

import java.io.Serializable;

public class SearchHistory implements Serializable {

    private String hitoryCity;

    public SearchHistory(String hitoryCity) {
        this.hitoryCity = hitoryCity;
    }

    public String getHitoryCity() {
        return hitoryCity;
    }
}
