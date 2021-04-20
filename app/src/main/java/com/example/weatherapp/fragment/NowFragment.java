package com.example.weatherapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.MyWidget;
import com.example.weatherapp.R;
import com.example.weatherapp.activity.MainActivity;
import com.example.weatherapp.adapter.HourlyWeatherAdapter;
import com.example.weatherapp.adapter.MultiViewAdapter;
import com.example.weatherapp.models.HourlyWeather;
import com.example.weatherapp.models.MultiView;
import com.example.weatherapp.until.ConvertsUntil;
import com.example.weatherapp.until.DialogUntil;
import com.github.mikephil.charting.data.BarEntry;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edmt.dev.advancednestedscrollview.AdvancedNestedScrollView;
import edmt.dev.advancednestedscrollview.MaxHeightRecyclerView;
import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.WindowManager.*;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_DETAIL;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_RAIN_CHART;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_SUN;
import static com.example.weatherapp.models.MultiView.NOW_FRAGMENT_WARS;


public class NowFragment extends Fragment {

    private View rootView;
    private TextView txt_day, txt_temp, txt_clouds, txt_status, lable_temp;
    private ImageView img_icon, img_arrowHourly, img_arrowRightHourly, img_arrowLeftHourly;
    private LinearLayout linearHourly;

    //until
    private ConvertsUntil convertsUntil;

    private String longitude = "", latitude = "";
    private String defaultCity = "";
    private MainActivity activityMain;

    //hourly
    private ArrayList<HourlyWeather> hourlyWeatherList; // data
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private MaxHeightRecyclerView recyclerViewHourly;
    private LinearLayoutManager layoutManagerHourly;
    private String tempHourly;

    //rain chart
    private ArrayList<String> xAxisData;
    private ArrayList<BarEntry> valueSet;

    //include layout in recyclerView
    private boolean isShowingCardHeaderShadow;
    private List<MultiView> multiViewList;
    private MultiViewAdapter multiViewAdapter;

    //multiView
    private MaxHeightRecyclerView recyclerViewMulti;
    private LinearLayoutManager layoutManagerMultiView;
    private AdvancedNestedScrollView advancedNestedScrollView;
    private DividerItemDecoration dividerItemDecorationMulti;

    //nowFragment
    private String temp, feels_like, dew_point;
    private String tempWidget;

    public Dialog dialogLoadMoreData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_now, container, false);

        initViewRoot();
        events();
        // lấy city dựa theo GPS trên hệ thống
        defaultCity = activityMain.getCityCurrentLocation();

        if (defaultCity.isEmpty()) {
            defaultCity = "California";
            activityMain.toolbarRoot.setTitle(defaultCity);
            getDataCityLonLat(defaultCity);
        } else {
            getDataCityLonLat(defaultCity);
        }

        return rootView;
    }

    private void initViewRoot() {
        txt_day = rootView.findViewById(R.id.txt_day);
        txt_temp = rootView.findViewById(R.id.txt_temp);
        txt_clouds = rootView.findViewById(R.id.txt_clouds);
        txt_status = rootView.findViewById(R.id.txt_status);
        img_icon = rootView.findViewById(R.id.img_icon);
        linearHourly = rootView.findViewById(R.id.linearHourly);
        recyclerViewHourly = rootView.findViewById(R.id.recyclerViewHourly);
        recyclerViewMulti = rootView.findViewById(R.id.card_recycler_view);
        advancedNestedScrollView = rootView.findViewById(R.id.nested_scroll_view);
        lable_temp = rootView.findViewById(R.id.lable_temp);
        img_arrowHourly = rootView.findViewById(R.id.img_arrowHourly);
        img_arrowLeftHourly = rootView.findViewById(R.id.img_arrowLeftHourly);
        img_arrowRightHourly = rootView.findViewById(R.id.img_arrowRightHourly);

        activityMain = (MainActivity) getActivity();

        convertsUntil = new ConvertsUntil();
        hourlyWeatherList = new ArrayList<HourlyWeather>(); // data
        layoutManagerHourly = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        xAxisData = new ArrayList<>();
        valueSet = new ArrayList<>();

        multiViewList = new ArrayList<>();
        layoutManagerMultiView = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        multiViewAdapter = new MultiViewAdapter(multiViewList);

        dividerItemDecorationMulti = new DividerItemDecoration(getContext(), layoutManagerMultiView.getOrientation());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecorationMulti.setDrawable(drawable);

        createDialogLoadMore();
    }

    private void createDialogLoadMore(){
        dialogLoadMoreData = new Dialog(getContext());
        dialogLoadMoreData.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoadMoreData.setContentView(R.layout.dialog_load_more_data);
        dialogLoadMoreData.setCanceledOnTouchOutside(false);
        dialogLoadMoreData.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void events() {
        //envent scroll reyclerVier show/hide image arrowRingt and arrowLeft
        recyclerViewHourly.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int secondPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (firstPosition == 0) {
                    img_arrowLeftHourly.setVisibility(View.GONE);
                    img_arrowRightHourly.setVisibility(View.VISIBLE);
                } else if (secondPosition == 47) {
                    img_arrowLeftHourly.setVisibility(View.VISIBLE);
                    img_arrowRightHourly.setVisibility(View.GONE);
                } else {
                    img_arrowLeftHourly.setVisibility(View.VISIBLE);
                    img_arrowRightHourly.setVisibility(View.VISIBLE);
                }

            }
        });

        //event click image arrowRight/arrowLeft scroll recyclerView
        img_arrowRightHourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewHourly.getLayoutManager();
                int secondPosition = linearLayoutManager.findLastVisibleItemPosition();
                int increasePosition = secondPosition;

                if (increasePosition < 47) {
                    if (secondPosition >= 12)
                        recyclerViewHourly.scrollToPosition(increasePosition + 5);
                    else if (secondPosition < 12)
                        recyclerViewHourly.scrollToPosition(increasePosition + 6);
                }
            }
        });

        img_arrowLeftHourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewHourly.getLayoutManager();
                int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int increasePosition = firstPosition;

                if (increasePosition > 0) {
                    if (firstPosition >= 36)
                        recyclerViewHourly.scrollToPosition(increasePosition - 6);
                    else if (firstPosition < 36)
                        recyclerViewHourly.scrollToPosition(increasePosition - 5);
                }
            }
        });


        //envent scroll reyclerVier show/hide cardHeartShadow
        recyclerViewMulti.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                boolean isRecyclerViewScrollToTop = layoutManagerMultiView.findFirstVisibleItemPosition() == 0 && layoutManagerMultiView.findViewByPosition(0).getTop() == 0;

                if (!isRecyclerViewScrollToTop && !isShowingCardHeaderShadow) {
                    isShowingCardHeaderShadow = true;
                } else {
                    isShowingCardHeaderShadow = false;
                }

            }
        });

        advancedNestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        advancedNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0 && oldScrollY > 0) {
                    //reset the recyckerView's scroll position each time the card return to is starting position
                    recyclerViewMulti.scrollToPosition(0);
                    isShowingCardHeaderShadow = false;
                }
                //event show/hide image arrowUp and arrowDown
                if (scrollY > 0) {
                    img_arrowHourly.setImageResource(R.drawable.ic_arrow_down);
                    activityMain.checkAdvancedNestedScrollView = true;
                } else {
                    img_arrowHourly.setImageResource(R.drawable.ic_arrow_up);
                    activityMain.checkAdvancedNestedScrollView = false;
                }
            }
        });

        img_arrowHourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activityMain.checkAdvancedNestedScrollView == false) {
                    advancedNestedScrollView.fullScroll(View.FOCUS_DOWN);
                    activityMain.checkAdvancedNestedScrollView = true;
                } else {
                    advancedNestedScrollView.fullScroll(View.FOCUS_UP);
                    activityMain.checkAdvancedNestedScrollView = false;
                }
            }
        });

    }

    public void showDialogNotFoundCity(final Context context) {
        activityMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dialogLoadMoreData.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_city_not_found, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();
                        dialogLoadMoreData.dismiss();
                        activityMain.searchView.showSearch();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getWindow().setWindowAnimations(R.style.ConnectionDialogAnimation);
                alertDialog.show();

            }
        });
    }

    public void getDataCityLonLat(final String data) {

        if (getContext() != null) {
            dialogLoadMoreData.show();

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

                            //sys
                            JSONObject jsonObjectClouds = jsonObjectRoot.getJSONObject("clouds");
                            String clouds = jsonObjectClouds.getString("all");

                            setTexts(txt_clouds, "Cloudiness: " + clouds + "%");

                            hourlyWeatherList.removeAll(hourlyWeatherList);
                            getDataCurrentWeather(latitude, longitude);

                            activityMain.saveFileHSearchistory(getContext(), data); // city from GPS and city while search

                        } catch (JSONException e) {
                            showDialogNotFoundCity(getContext());
                        }
                    }
                });
            } catch (IOException e) {

            }
        }
    }

    public void getDataCurrentWeather(String lat, String lon) {
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
                        //khai báo json root
                        JSONObject jsonObjectRoot = new JSONObject(response.body().string());

                        // hourly
                        JSONArray jsonArrayHourly = jsonObjectRoot.getJSONArray("hourly");
                        for (int i = 0; i < jsonArrayHourly.length(); i++) {
                            JSONObject jsonObjectHourly = jsonArrayHourly.getJSONObject(i);
                            String dtHourly = convertsUntil.convertTime(jsonObjectHourly.getString("dt"), "HH a");

                            if (activityMain.loadFileTemperature().equals("°C"))
                                tempHourly = convertsUntil.convertKelvinToCelsius(jsonObjectHourly.getString("temp")) + "°";
                            else
                                tempHourly = convertsUntil.convertKelvinToFahrenheit(jsonObjectHourly.getString("temp")) + "°";

                            JSONArray jsonArrayWeatherHourly = jsonObjectHourly.getJSONArray("weather");
                            JSONObject jsonObjectWeatherHourly = jsonArrayWeatherHourly.getJSONObject(0);
                            String codeIconHourly = jsonObjectWeatherHourly.getString("icon");
                            hourlyWeatherList.add(new HourlyWeather(dtHourly, codeIconHourly, tempHourly));
                        }

                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hourlyWeatherAdapter = new HourlyWeatherAdapter(getContext(), hourlyWeatherList);

                                recyclerViewHourly.setLayoutManager(layoutManagerHourly);
                                recyclerViewHourly.setAdapter(hourlyWeatherAdapter);
                                hourlyWeatherAdapter.notifyDataSetChanged();
                            }
                        });

                        // current
                        JSONObject jsonObjectCurrent = jsonObjectRoot.getJSONObject("current");

                        String dt = convertsUntil.convertTime(jsonObjectCurrent.getString("dt"), "E, MMM d yyyy");   //time

                        if (activityMain.loadFileTemperature().equals("°C")) {
                            temp = convertsUntil.convertKelvinToCelsius(jsonObjectCurrent.getString("temp"));          // nhiệt độ hiện tại
                            feels_like = convertsUntil.convertKelvinToCelsius(jsonObjectCurrent.getString("feels_like")) + "°";  // nhiệt độ cảm thấy như
                            dew_point = convertsUntil.convertKelvinToCelsius(jsonObjectCurrent.getString("dew_point")) + "°";    // Nhiệt độ khí quyển
                            setTexts(lable_temp, "°C");
                            tempWidget = temp + "°C";

                        } else {
                            temp = convertsUntil.convertKelvinToFahrenheit(jsonObjectCurrent.getString("temp"));          // nhiệt độ hiện tại
                            feels_like = convertsUntil.convertKelvinToFahrenheit(jsonObjectCurrent.getString("feels_like")) + "°";  // nhiệt độ cảm thấy như
                            dew_point = convertsUntil.convertKelvinToFahrenheit(jsonObjectCurrent.getString("dew_point")) + "°";    // Nhiệt độ khí quyển
                            setTexts(lable_temp, "°F");
                            tempWidget = temp + "°F";
                        }

                        String pressure = jsonObjectCurrent.getString("pressure") + " hPa";      // áp suất
                        String humidity = jsonObjectCurrent.getString("humidity") + "%";      // độ ẩm
                        String visibility = convertsUntil.convertMeterToKilometer(jsonObjectCurrent.getString("visibility")) + " km";  // tầm nhìn
                        String wind_speed = convertsUntil.convertMetersPerSecondToKilometersPerHour(jsonObjectCurrent.getString("wind_speed")) + " km/h";  // tốc độ gió
                        String wind_deg = jsonObjectCurrent.getString("wind_deg") + "°";      //  Hướng gió, độ

                        String wind_gust = ""; // cơn gió mạnh(mm) nếu có
                        String uvi = "";
                        try {
                            wind_gust = jsonObjectCurrent.getString("wind_deg") + " m/s"; // con gió mạnh
                            uvi = jsonObjectCurrent.getString("uvi");  // uv
                        } catch (Exception e) {
                            wind_gust = getResources().getString(R.string.text_default);
                            uvi = getResources().getString(R.string.text_default);
                        }

                        //weather
                        JSONArray jsonArrayWeather = jsonObjectCurrent.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String main = jsonObjectWeather.getString("main");                  // Nhóm thông số thời tiết
                        String description = jsonObjectWeather.getString("description");   // Tình trạng thời tiết trong nhóm
                        String codeIcon = jsonObjectWeather.getString("icon");                 // biểu tượng thời tiết

                        setImages(main, description, codeIcon, activityMain.img_backgroundApp, img_icon, linearHourly);

                        String rain_volume = ""; // rain(nếu có)
                        String snow_volume = "";  // snow(nếu có)
                        try {
                            JSONObject jsonObjectRain = jsonObjectCurrent.getJSONObject("rain");
                            rain_volume = jsonObjectRain.getString("1h") + " mm";

                            JSONObject jsonObjectSnow = jsonObjectCurrent.getJSONObject("snow");
                            snow_volume = jsonObjectSnow.getString("1h") + " mm";

                        } catch (Exception e) {

                            rain_volume = getResources().getString(R.string.text_default);
                            snow_volume = getResources().getString(R.string.text_default);

                        }

                        //set giá trị
                        setTexts(txt_day, dt);
                        setTexts(txt_temp, temp);
                        setTexts(txt_status, description);

                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerViewMulti.setLayoutManager(layoutManagerMultiView);
                                recyclerViewMulti.addItemDecoration(dividerItemDecorationMulti);
                                recyclerViewMulti.setAdapter(multiViewAdapter);
                                multiViewAdapter.notifyDataSetChanged();
                            }
                        });

                        multiViewList.removeAll(multiViewList);
                        multiViewList.add(new MultiView(NOW_FRAGMENT_DETAIL, feels_like, humidity, visibility, uvi));
                        multiViewList.add(new MultiView(NOW_FRAGMENT_WARS, wind_speed, wind_deg, wind_gust, pressure, dew_point, rain_volume, snow_volume));

                        //ArcSeekBar sun
                        String timeSunrise = convertsUntil.convertTime(jsonObjectCurrent.getString("sunrise"), "d MMMM YYYY, HH:mm a");    // thời gian mặt trời mọc
                        String timeSunset = convertsUntil.convertTime(jsonObjectCurrent.getString("sunset"), "d MMMM YYYY, HH:mm a");    // thời gian mặt trời lặn

                        String minTimeSun, maxTimeSun; // biến kiểm tra sunrise, sunset để lấy lớn nhỏ (min trước, max đứng sau)

                        int sunMaxSeekBar = 0, sunProgressSun = 0;
                        int int_sunrise = jsonObjectCurrent.getInt("sunrise");
                        int int_sunset = jsonObjectCurrent.getInt("sunset");

                        sunMaxSeekBar = Math.abs(int_sunset - int_sunrise);
                        sunProgressSun = (int) Math.abs((System.currentTimeMillis() / 1000) - Math.min(int_sunrise, int_sunset));
                        minTimeSun = convertsUntil.convertTime(String.valueOf(Math.min(int_sunrise, int_sunset)), "MMM d YYYY HH:mm a");
                        maxTimeSun = convertsUntil.convertTime(String.valueOf(Math.max(int_sunrise, int_sunset)), "MMM d YYYY HH:mm a");

                        if (timeSunrise.substring(0, 2).equals(timeSunset.substring(0, 2))) { // cùng ngày

                            if (int_sunrise < int_sunset) {  //bình thường (mọc sáng 6h30 lặn 18h30 cùng ngày)
                                multiViewList.add(new MultiView(NOW_FRAGMENT_SUN, minTimeSun, maxTimeSun, sunMaxSeekBar, sunProgressSun, R.drawable.sunrise, R.drawable.sunset));
                            } else { // sunrise > sunset (lặn 4h chiều, mọc 9h đêm cùng ngày)
                                multiViewList.add(new MultiView(NOW_FRAGMENT_SUN, minTimeSun, maxTimeSun, sunMaxSeekBar, sunProgressSun, R.drawable.sunset, R.drawable.sunrise));
                            }
                        } else {

                            if (int_sunrise > int_sunset) {   // (đêm dài ngày ngắn: lặn hôm nay, mọc hôm sau)
                                multiViewList.add(new MultiView(NOW_FRAGMENT_SUN, minTimeSun, maxTimeSun, sunMaxSeekBar, sunProgressSun, R.drawable.sunrise, R.drawable.sunset));
                            } else {  // sunrise < sunset bth (mọc hôm nay, lặn  hôm sau)
                                multiViewList.add(new MultiView(NOW_FRAGMENT_SUN, minTimeSun, maxTimeSun, sunMaxSeekBar, sunProgressSun, R.drawable.sunset, R.drawable.sunrise));
                            }
                        }

                        // JSON minutely: Precipitation (CHART)
                        JSONArray jsonArrayMinutely = null;
                        try {
                            jsonArrayMinutely = jsonObjectRoot.getJSONArray("minutely");
                            for (int i = 0; i < jsonArrayMinutely.length(); i++) {
                                JSONObject jsonObjectMinutely = jsonArrayMinutely.getJSONObject(i);
                                String dtRain = convertsUntil.convertTime(jsonObjectMinutely.getString("dt"), "HH:mm");
                                String rain = jsonObjectMinutely.getString("precipitation");

                                float precipitation = Float.parseFloat(rain);
                                if (xAxisData.size() <= 60 && valueSet.size() <= 60) {
                                    xAxisData.add(dtRain);
                                    valueSet.add(new BarEntry(precipitation, i));
                                } else {
                                    break;
                                }

                            }
                            multiViewList.add(new MultiView(NOW_FRAGMENT_RAIN_CHART, xAxisData, valueSet));
                        } catch (Exception e) {
                        }

                        //getDataWidget
                        int image = convertsUntil.setImageWidget(main, description, codeIcon);
                        Paper.book().write("temp", tempWidget);
                        Paper.book().write("image", image);

                        Intent updateWidget = new Intent(getContext(), MyWidget.class); // Widget.class is your widget class
                        updateWidget.setAction("update_widget");
                        PendingIntent pending = PendingIntent.getBroadcast(getContext(), 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
                        pending.send();

                        dialogLoadMoreData.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setTexts(final TextView textView, final String value) {
        activityMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(value);
            }
        });
    }

    public void setImages(final String groupWeather, final String descWeather, final String codeIcon, final ImageView imgBR,
                          final ImageView imgICON, final LinearLayout linearLayoutHourly) {
        activityMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Clouds
                if (groupWeather.equals("Clouds")) {
                    if (descWeather.equals("broken clouds")) {
                        if (codeIcon.contains("d")) {
                            imgICON.setImageResource(R.drawable.broken_clouds_mor);
                            imgBR.setImageResource(R.drawable.br_broken_clouds_mor);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_broken_clouds_mor);

                        } else {
                            imgICON.setImageResource(R.drawable.broken_clouds_night);
                            imgBR.setImageResource(R.drawable.br_broken_clouds_night);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_broken_clouds_night);
                        }

                    } else if (descWeather.equals("overcast clouds")) {
                        imgICON.setImageResource(R.drawable.overcast_clouds);
                        imgBR.setImageResource(R.drawable.br_overcast_clouds);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_overcast_clouds);

                    } else if (descWeather.equals("few clouds")) {
                        if (codeIcon.contains("d")) {
                            imgICON.setImageResource(R.drawable.few_clouds_mor);
                            imgBR.setImageResource(R.drawable.br_fewclouds_mor);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_fewclouds_mor);
                        } else {
                            imgICON.setImageResource(R.drawable.few_clouds_night);
                            imgBR.setImageResource(R.drawable.br_fewclouds_night);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_fewclouds_night);
                        }

                    } else if (descWeather.equals("scattered clouds")) {
                        if (codeIcon.contains("d")) {
                            imgICON.setImageResource(R.drawable.scattered_clouds_mor);
                            imgBR.setImageResource(R.drawable.br_scattered_clouds_mor);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_scattered_clouds_mor);
                        } else {
                            imgICON.setImageResource(R.drawable.scattered_clouds_night);
                            imgBR.setImageResource(R.drawable.br_scattered_clouds_night);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_scattered_clouds_night);
                        }
                    }

                    // Clear
                } else if (groupWeather.equals("Clear")) {
                    if (codeIcon.contains("d")) {
                        imgICON.setImageResource(R.drawable.clear_sky_mor);
                        imgBR.setImageResource(R.drawable.br_clear_mor);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_clear_mor);
                    } else {
                        imgICON.setImageResource(R.drawable.clear_sky_night);
                        imgBR.setImageResource(R.drawable.br_clear_night);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_clear_night);
                    }

                    // Rain
                } else if (groupWeather.equals("Rain")) {
                    if (descWeather.equals("freezing rain")) {
                        imgICON.setImageResource(R.drawable.freezing_rain);
                        imgBR.setImageResource(R.drawable.br_rain_and_snow);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_rain_and_snow);
                    } else if (descWeather.contains("shower rain")) {
                        imgICON.setImageResource(R.drawable.shower_rain);
                        imgBR.setImageResource(R.drawable.br_rain);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_rain);
                    } else {
                        if (codeIcon.contains("d")) {
                            imgICON.setImageResource(R.drawable.rain_mor);
                            imgBR.setImageResource(R.drawable.br_rain);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_rain);
                        } else {
                            imgICON.setImageResource(R.drawable.rain_night);
                            imgBR.setImageResource(R.drawable.br_rain);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_rain);
                        }
                    }

                    // Drizzle
                } else if (groupWeather.equals("Drizzle")) {
                    imgICON.setImageResource(R.drawable.shower_rain);
                    imgBR.setImageResource(R.drawable.br_drizzle);
                    linearLayoutHourly.setBackgroundResource(R.color.scroll_drizzle);

                    // Thunderstorm
                } else if (groupWeather.equals("Thunderstorm")) {

                    imgBR.setImageResource(R.drawable.br_thunderstorm);
                    linearLayoutHourly.setBackgroundResource(R.color.scroll_thunderstorm);

                    if (descWeather.equals("thunderstorm with light rain") || descWeather.equals("thunderstorm with light drizzle")) {
                        if (codeIcon.contains("d"))
                            imgICON.setImageResource(R.drawable.thunderstorm_with_light_rain_mor);
                        else
                            imgICON.setImageResource(R.drawable.thunderstorm_with_light_rain_night);
                    } else if (descWeather.equals("thunderstorm") || descWeather.equals("heavy thunderstorm") || descWeather.equals("ragged thunderstorm")) {
                        imgICON.setImageResource(R.drawable.thunderstorm);
                    } else {
                        imgICON.setImageResource(R.drawable.thunderstorm_with_rain);
                    }

                    // Atmosphere
                } else if (groupWeather.equals("Mist") || groupWeather.equals("Smoke") || groupWeather.equals("Haze") || groupWeather.equals("Fog")) {
                    imgICON.setImageResource(R.drawable.atmosphere_icon);
                    imgBR.setImageResource(R.drawable.br_atmosphere_mist);
                    linearLayoutHourly.setBackgroundResource(R.color.scroll_atmosphere_mist);

                } else if (groupWeather.equals("Dust") || groupWeather.equals("Sand") || groupWeather.equals("Ash") || groupWeather.equals("Tornado")) {
                    imgICON.setImageResource(R.drawable.wind_icon);
                    imgBR.setImageResource(R.drawable.br_atmosphere_tornado);
                    linearLayoutHourly.setBackgroundResource(R.color.scroll_atmosphere_tornado);

                } else if (groupWeather.equals("Squall")) {
                    imgICON.setImageResource(R.drawable.freezing_rain);
                    imgBR.setImageResource(R.drawable.br_atmosphere_squalls);
                    linearLayoutHourly.setBackgroundResource(R.color.scroll_atmosphere_squalls);

                }// Snow
                else if (groupWeather.equals("Snow")) {
                    if (descWeather.equals("light snow") || descWeather.equals("Snow") || descWeather.equals("Heavy snow")) {
                        imgICON.setImageResource(R.drawable.light_snow);
                        if (descWeather.equals("light snow")) {
                            imgBR.setImageResource(R.drawable.br_snow_light_snow);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_snow_light_snow);
                        } else if (descWeather.equals("Snow")) {
                            imgBR.setImageResource(R.drawable.br_snow_snow);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_snow_snow);
                        } else if (descWeather.equals("Heavy snow")) {
                            imgBR.setImageResource(R.drawable.br_snow_heavy_snow);
                            linearLayoutHourly.setBackgroundResource(R.color.scroll_snow_heavy_snow);
                        }

                    } else if (descWeather.equals("Sleet")) {
                        imgICON.setImageResource(R.drawable.freezing_rain);
                        imgBR.setImageResource(R.drawable.br_snow_sleet);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_snow_sleet);

                    } else {
                        imgICON.setImageResource(R.drawable.rain_and_snow);
                        imgBR.setImageResource(R.drawable.br_rain_and_snow);
                        linearLayoutHourly.setBackgroundResource(R.color.scroll_rain_and_snow);
                    }
                }
            }
        });
    }

}
