package com.example.weatherapp.fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.activities.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class RadarFragment extends Fragment {

    private View rootView;

    private WebView webView;

    private String longitude = "", latitude = "";
    private String defaultCity = "";
    private MainActivity activityMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_radar, container, false);

        init();

        // lấy city dựa theo GPS trên hệ thống
        defaultCity = activityMain.getCityCurrentLocation();
        if (defaultCity.isEmpty()) {
            defaultCity = "California";
            getDataCityLonLat(defaultCity);

        } else {
            getDataCityLonLat(defaultCity);
        }

        return rootView;
    }

    private void init() {

        webView = rootView.findViewById(R.id.webView);

        activityMain = (MainActivity) getActivity();
    }

    public void getDataCityLonLat(String data) {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q=" + data + "&appid=0f1e40dd68d9878070b3735eb494976f")
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            okhttp3.Response responses = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {

                    try {
                        //khai báo json root
                        JSONObject jsonObjectRoot = new JSONObject(response.body().string());

                        // coord
                        JSONObject jsonObjectCoord = jsonObjectRoot.getJSONObject("coord");
                        longitude = jsonObjectCoord.getString("lon"); // kinh độ
                        latitude = jsonObjectCoord.getString("lat"); // vĩ dộ

                        setWebView(webView, latitude, longitude);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setWebView(final WebView webView, final String lat, final String lon) {
        activityMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("https://openweathermap.org/weathermap?basemap=map&cities=false&layer=windspeed&lat=" + lat + "&lon=" + lon + "&zoom=25");
            }
        });
    }
}