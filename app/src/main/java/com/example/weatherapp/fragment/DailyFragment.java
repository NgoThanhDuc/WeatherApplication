package com.example.weatherapp.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.DetailsRecyclerViewClickInterface;
import com.example.weatherapp.R;
import com.example.weatherapp.SummaryRecyclerViewClickInterface;
import com.example.weatherapp.activity.MainActivity;
import com.example.weatherapp.adapter.DetailsDailyAdapter;
import com.example.weatherapp.adapter.SummaryDailyAdapter;
import com.example.weatherapp.models.DetailsDaily;
import com.example.weatherapp.models.SummaryDaily;
import com.example.weatherapp.until.ConvertsUntil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class DailyFragment extends Fragment implements SummaryRecyclerViewClickInterface, DetailsRecyclerViewClickInterface {

    private View rootView;

    private RecyclerView recyclerViewSummary, recyclerViewDetails;
    private TextView txt_timeline;
    private BottomNavigationView bottomNavigationView;
    private CardView cardViewBottomNavigation;

    //until
    private ConvertsUntil convertsUntil;

    private String longitude = "", latitude = "";
    private String defaultCity = "";
    private MainActivity activityMain;

    //SummartiDaily
    private ArrayList<SummaryDaily> summaryDailyList;
    private SummaryDailyAdapter summaryDailyAdapter;
    private String timelineFirstly = "", timelineFinal = "", timeline = "";
    private ArrayList<String> timeLineList;

    //variables in JSOM
    private String day = "", rain = "";

    //DetailsDaily
    private ArrayList<DetailsDaily> detailsDailyList;
    private DetailsDailyAdapter detailsDailyAdapter;

    private LinearLayoutManager layoutManagerSummary;
    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager layoutManagerDetails;

    private String dateDetailsDaily;

    //summary
    private String tempDay, tempNight, tempMax, tempMin, tempMorn, tempEve;
    private String feelsLikeMorn, feelsLikeEve, feelsLikeDay, feelsLikeNight;
    private String dewPoint;
    private String clouds;
    private String uvi;

    //Weather
    private String codeIcon, main, desc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_daily, container, false);

        init();
        envents();

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
        activityMain = (MainActivity) getActivity();

        summaryDailyList = new ArrayList<>();
        detailsDailyList = new ArrayList<>();
        timeLineList = new ArrayList<>();

        txt_timeline = rootView.findViewById(R.id.txt_timeline);
        recyclerViewSummary = rootView.findViewById(R.id.recycler_view_summary);
        recyclerViewDetails = rootView.findViewById(R.id.recycler_view_details);

        bottomNavigationView = rootView.findViewById(R.id.bottom_navigation_view);
        cardViewBottomNavigation = rootView.findViewById(R.id.cardViewBottomNavigation);

        layoutManagerSummary = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManagerSummary.getOrientation());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(drawable);
        layoutManagerDetails = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        convertsUntil = new ConvertsUntil();

    }

    private void envents() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.mmu_summary:
                        if (recyclerViewSummary.isShown() && !recyclerViewDetails.isShown()) {
                            recyclerViewSummary.setVisibility(View.VISIBLE);
                            txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);

                        }else if (!recyclerViewSummary.isShown() && recyclerViewDetails.isShown()) {
                            recyclerViewDetails.setVisibility(View.GONE);
                            recyclerViewSummary.setVisibility(View.VISIBLE);
                            txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                        }
                        break;
                    case R.id.mmu_details:
                        if (recyclerViewDetails.isShown() && !recyclerViewSummary.isShown()) {
                            recyclerViewDetails.setVisibility(View.VISIBLE);
                            txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                        }else if (!recyclerViewDetails.isShown() && recyclerViewSummary.isShown()) {
                            recyclerViewSummary.setVisibility(View.GONE);
                            recyclerViewDetails.setVisibility(View.VISIBLE);
                            txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                        }
                        break;
                }

                return true;
            }
        });

        recyclerViewSummary.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && cardViewBottomNavigation.isShown()) {
                    cardViewBottomNavigation.setVisibility(View.GONE);

                } else if (dy < 0) {
                    cardViewBottomNavigation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        recyclerViewDetails.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && cardViewBottomNavigation.isShown()) {
                    cardViewBottomNavigation.setVisibility(View.GONE);

                } else if (dy < 0) {
                    cardViewBottomNavigation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        txt_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recyclerViewSummary.isShown()) {
                    recyclerViewSummary.setVisibility(View.GONE);
                    txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                    recyclerViewDetails.setVisibility(View.VISIBLE);
                    bottomNavigationView.setSelectedItemId(R.id.mmu_details);
                    cardViewBottomNavigation.setVisibility(View.VISIBLE);

                } else {
                    recyclerViewDetails.setVisibility(View.GONE);
                    txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    recyclerViewSummary.setVisibility(View.VISIBLE);
                    bottomNavigationView.setSelectedItemId(R.id.mmu_summary);
                    cardViewBottomNavigation.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void getDataCityLonLat(String data) {

        if (getContext() != null) {
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

                            summaryDailyList.removeAll(summaryDailyList);
                            detailsDailyList.removeAll(detailsDailyList);
                            timeLineList.removeAll(timeLineList);
                            getDataDailyWeather(latitude, longitude);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getDataDailyWeather(String lat, String lon) {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&%20exclude=hourly,daily&appid=0f1e40dd68d9878070b3735eb494976f")
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
                        //  khai báo json root
                        JSONObject jsonObjectRoot = new JSONObject(response.body().string());
                        //  daily
                        final JSONArray jsonArrayDaily = jsonObjectRoot.getJSONArray("daily");

                        for (int i = 0; i < jsonArrayDaily.length(); i++) {
                            JSONObject jsonObjectDaily = jsonArrayDaily.getJSONObject(i);

                            //  day
                            day = convertsUntil.convertTime(jsonObjectDaily.getString("dt"), "EEEE, MMM dd");
                            dateDetailsDaily = convertsUntil.convertTime(jsonObjectDaily.getString("dt"), "MMMM d yyyy");

                            //  timelineFirstly, timelineFinal from DailyFragment
                            for (int j = 0; j < jsonArrayDaily.length(); j = jsonArrayDaily.length() - 1) {
                                JSONObject jsonObjectDailyTimeLine = jsonArrayDaily.getJSONObject(j);
                                timeline = jsonObjectDailyTimeLine.getString("dt");
                                timeLineList.add(timeline);
                                if (timeLineList.size() > 2) {
                                    break;
                                }
                            }

                            //  temp
                            JSONObject jsonObjectTemp = jsonObjectDaily.getJSONObject("temp");
                            //  feelsLike
                            JSONObject jsonObjectFeelsLike = jsonObjectDaily.getJSONObject("feels_like");

                            if (activityMain.loadFileTemperature().equals("°C")) {
                                getCelsiusTemperature(jsonObjectTemp, jsonObjectFeelsLike, jsonObjectDaily);
                            } else {
                                getCelsiusFahrenheit(jsonObjectTemp, jsonObjectFeelsLike, jsonObjectDaily);
                            }

                            //  clouds
                            clouds = jsonObjectDaily.getString("clouds") + "%";
                            //  rain
                            try {
                                rain = jsonObjectDaily.getString("rain") + " mm";
                            } catch (Exception e) {
                                rain = "0 mm";
                            }
                            //  uvi
                            uvi = jsonObjectDaily.getString("uvi");

                            //Weather
                            JSONArray jsonArrayWeather = jsonObjectDaily.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            codeIcon = jsonObjectWeather.getString("icon");
                            main = jsonObjectWeather.getString("main");
                            desc = jsonObjectWeather.getString("description");

                            summaryDailyList.add(new SummaryDaily(day, codeIcon, tempMax, tempMin, desc));
                            detailsDailyList.add(new DetailsDaily(codeIcon, desc, tempMorn, tempEve, tempDay, tempNight, feelsLikeMorn,
                                    feelsLikeEve, feelsLikeDay, feelsLikeNight, clouds, rain, dewPoint, uvi, dateDetailsDaily));

                        }

                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDataRecyclerViewSummary(summaryDailyList);  //item_fragment_summary_daily
                                setDataRecyclerViewDetails(detailsDailyList); //item_fragment_details_daily
                            }
                        });

                        timelineFirstly = convertsUntil.convertTime(timeLineList.get(0).toString(), "MMM dd yyyy");
                        timelineFinal = convertsUntil.convertTime(timeLineList.get(1).toString(), "MMM dd yyyy");
                        setTexts(txt_timeline, timelineFirstly + " - " + timelineFinal);

                    } catch (JSONException e) {
                        day = getResources().getString(R.string.text_default);
                        timelineFirstly = getResources().getString(R.string.text_default);
                        timelineFinal = getResources().getString(R.string.text_default);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCelsiusFahrenheit(JSONObject jsonObjectTemp, JSONObject jsonObjectFeelsLike, JSONObject jsonObjectDaily) {
        try {
            tempDay = convertsUntil.convertKelvinToFahrenheit(jsonObjectTemp.getString("day")) + "°";
            tempNight = convertsUntil.convertKelvinToFahrenheit(jsonObjectTemp.getString("night")) + "°";
            tempMax = convertsUntil.convertKelvinToFahrenheit(jsonObjectTemp.getString("max")) + "°";
            tempMin = convertsUntil.convertKelvinToFahrenheit(jsonObjectTemp.getString("min")) + "°";
            tempMorn = convertsUntil.convertKelvinToFahrenheit(jsonObjectTemp.getString("morn")) + "°";
            tempEve = convertsUntil.convertKelvinToFahrenheit(jsonObjectTemp.getString("eve")) + "°";

            feelsLikeMorn = convertsUntil.convertKelvinToFahrenheit(jsonObjectFeelsLike.getString("morn")) + "°";
            feelsLikeEve = convertsUntil.convertKelvinToFahrenheit(jsonObjectFeelsLike.getString("eve")) + "°";
            feelsLikeDay = convertsUntil.convertKelvinToFahrenheit(jsonObjectFeelsLike.getString("day")) + "°";
            feelsLikeNight = convertsUntil.convertKelvinToFahrenheit(jsonObjectFeelsLike.getString("night")) + "°";
            //  dewPoint
            dewPoint = convertsUntil.convertKelvinToFahrenheit(jsonObjectDaily.getString("dew_point")) + "°";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCelsiusTemperature(JSONObject jsonObjectTemp, JSONObject jsonObjectFeelsLike, JSONObject jsonObjectDaily) {
        try {
            tempDay = convertsUntil.convertKelvinToCelsius(jsonObjectTemp.getString("day")) + "°";
            tempNight = convertsUntil.convertKelvinToCelsius(jsonObjectTemp.getString("night")) + "°";
            tempMax = convertsUntil.convertKelvinToCelsius(jsonObjectTemp.getString("max")) + "°";
            tempMin = convertsUntil.convertKelvinToCelsius(jsonObjectTemp.getString("min")) + "°";
            tempMorn = convertsUntil.convertKelvinToCelsius(jsonObjectTemp.getString("morn")) + "°";
            tempEve = convertsUntil.convertKelvinToCelsius(jsonObjectTemp.getString("eve")) + "°";

            feelsLikeMorn = convertsUntil.convertKelvinToCelsius(jsonObjectFeelsLike.getString("morn")) + "°";
            feelsLikeEve = convertsUntil.convertKelvinToCelsius(jsonObjectFeelsLike.getString("eve")) + "°";
            feelsLikeDay = convertsUntil.convertKelvinToCelsius(jsonObjectFeelsLike.getString("day")) + "°";
            feelsLikeNight = convertsUntil.convertKelvinToCelsius(jsonObjectFeelsLike.getString("night")) + "°";

            //  dewPoint
            dewPoint = convertsUntil.convertKelvinToCelsius(jsonObjectDaily.getString("dew_point")) + "°";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDataRecyclerViewSummary(ArrayList<SummaryDaily> arrayList) {
        summaryDailyAdapter = new SummaryDailyAdapter(getContext(), arrayList, this);
        recyclerViewSummary.setLayoutManager(layoutManagerSummary);
        recyclerViewSummary.addItemDecoration(dividerItemDecoration);
        recyclerViewSummary.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSummary.setAdapter(summaryDailyAdapter);
        recyclerViewSummary.setNestedScrollingEnabled(false);
    }

    private void setDataRecyclerViewDetails(ArrayList<DetailsDaily> arrayList) {
        detailsDailyAdapter = new DetailsDailyAdapter(getContext(), arrayList, this);
        recyclerViewDetails.setLayoutManager(layoutManagerDetails);
        recyclerViewDetails.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDetails.setAdapter(detailsDailyAdapter);
        recyclerViewDetails.setNestedScrollingEnabled(false);
    }

    public void setTexts(final TextView textView, final String value) {
        activityMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(value);
            }
        });
    }

    @Override
    public void onItemClickSummary(int position) {
        recyclerViewSummary.setVisibility(View.GONE);
        txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
        recyclerViewDetails.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.mmu_details);
        recyclerViewDetails.scrollToPosition(position);
    }

    @Override
    public void onItemClickDetails(int position) {
        recyclerViewDetails.setVisibility(View.GONE);
        txt_timeline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
        recyclerViewSummary.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.mmu_summary);
        recyclerViewSummary.scrollToPosition(position);
    }

}



