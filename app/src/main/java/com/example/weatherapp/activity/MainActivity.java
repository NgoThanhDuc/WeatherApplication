package com.example.weatherapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.MyFragmentAdapter;
import com.example.weatherapp.adapter.SearchHistoryAdapter;
import com.example.weatherapp.fragment.DailyFragment;
import com.example.weatherapp.fragment.NowFragment;
import com.example.weatherapp.fragment.RadarFragment;
import com.example.weatherapp.models.SearchHistory;
import com.example.weatherapp.until.CheckConnection;
import com.example.weatherapp.until.DialogUntil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPagerRoot;
    private TabLayout tabLayoutRoot;
    public MaterialSearchView searchView;
    public Toolbar toolbarRoot;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView txt_celsius, txt_fahrenheit;
    public ImageView img_backgroundApp;

    private ListView listViewSearchHistory;
    private SearchHistoryAdapter searchHistoryAdapter;
    private ArrayList<SearchHistory> searchHistoryArrayList;

    public String checkCelsiusFahrenheit = "°C";

    private SmartMaterialSpinner continentSpinner, statesSpinner;
    private ListView listViewNavigation;

    public DialogUntil dialogUntil;

    private double longitude = 0.0, latitude = 0.0;
    private String defaultCity = "";

    private Location gps_loc = null, network_loc = null, final_loc = null;

    private MyFragmentAdapter myFragmentAdapter;

    //continent
    private List<String> continentList;

    // states
    private List<String> statesAmerica, statesAsiaPacific, statesEurope, statesMiddleEastAfrica;
    //  cities America By states
    private List<String> citiesNorthAmerica, citiesSouthAmerica, citiesCentralAmerica, citiesCaribbean;
    //  cities Asia-Pacific By states
    private List<String> citiesCentralAsia, citiesSouthAsia, citiesNortheastAsia, citiesSoutheastAsia, citiesAustraliaAndOceania;
    //  cities Europe By states
    private List<String> citiesNorthernEurope, citiesSouthernEurope, citiesEasternEurope, citiesWesternEurope;
    //  cities Europe By states
    private List<String> citiesMiddleEast, citiesNorthAfrica, citiesSouthernAfrica;
    private int idContinent; // check current position of states

    //nowFragment
    public boolean checkAdvancedNestedScrollView = false;
    private boolean oneRun = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        actionBar();
        newStatesSpinner();
        newCitieByStatesSpinner();
        events();
    }

    private void initView() {
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewPagerRoot = findViewById(R.id.viewPager_root);
        viewPagerRoot.setOffscreenPageLimit(3); // tránh reload lại fragment (mặc định là 1) (3): số trang được giữ lại k cần reload
        viewPagerRoot.setAdapter(myFragmentAdapter);

        tabLayoutRoot = findViewById(R.id.tabbLayout_root);
        tabLayoutRoot.setupWithViewPager(viewPagerRoot);

        searchView = findViewById(R.id.searchView);
        toolbarRoot = findViewById(R.id.toolbar_root);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        txt_celsius = findViewById(R.id.txt_celsius);
        txt_fahrenheit = findViewById(R.id.txt_fahrenheit);

        img_backgroundApp = findViewById(R.id.img_backgroundApp);

        // default is celsius
        txt_celsius.setBackgroundColor(Color.LTGRAY);
        txt_celsius.setTextColor(Color.WHITE);

        continentSpinner = findViewById(R.id.continent_spinner);
        continentList = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.continent)));
        continentSpinner.setItem(continentList);

        statesSpinner = findViewById(R.id.states_spinner);
        listViewNavigation = findViewById(R.id.listViewCities);

        listViewSearchHistory = findViewById(R.id.listViewSearchHistory);
        searchHistoryArrayList = new ArrayList<>();

        // init default data search by file
        loadFileSearchHistory();

        // init data temperature and check
        initDataTempCheckColorBackgroundTextNavigation();

        //init Paper
        Paper.init(this);

        dialogUntil = new DialogUntil();

    }

    private void actionBar() {
        setSupportActionBar(toolbarRoot);
        toolbarRoot.setNavigationIcon(R.drawable.ic_menu_sort_by);
        toolbarRoot.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initDataTempCheckColorBackgroundTextNavigation() {
        File file = new File(getFilesDir(), "temperature.txt");
        if (file.exists()) {
            if (loadFileTemperature().equals("")) {
                saveFileTemperature(MainActivity.this, checkCelsiusFahrenheit);
            } else {
                if (loadFileTemperature().equals("°C")) {
                    txt_celsius.setBackgroundColor(Color.LTGRAY);
                    txt_celsius.setTextColor(Color.WHITE);

                    txt_fahrenheit.setBackgroundColor(Color.WHITE);
                    txt_fahrenheit.setTextColor(Color.BLACK);
                } else {
                    txt_fahrenheit.setBackgroundColor(Color.LTGRAY);
                    txt_fahrenheit.setTextColor(Color.WHITE);

                    txt_celsius.setBackgroundColor(Color.WHITE);
                    txt_celsius.setTextColor(Color.BLACK);
                }
            }

        } else {
            saveFileTemperature(MainActivity.this, checkCelsiusFahrenheit);
        }
    }

    private void events() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
//                    alertDialogLoadMoreData.show();

                    String boKyTuDacBiet = query.replace(" ", "").trim().replaceAll("[^\\p{L}\\p{Z}]", "");
                    String citySearch = boKyTuDacBiet.substring(0, 1).toUpperCase() + boKyTuDacBiet.substring(1).toLowerCase();

                    getAllDataByFragments(citySearch);

                    searchView.closeSearch();
                    toolbarRoot.setTitle(citySearch);
                    return true;

                } else {
                    dialogUntil.showDialogNoUpdateData(MainActivity.this);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (oneRun == true) {
                    loadFileSearchHistory();
                    oneRun = false;
                }
                searchHistoryAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                loadFileSearchHistory();
                listViewSearchHistory.setVisibility(View.VISIBLE);
                tabLayoutRoot.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                listViewSearchHistory.setVisibility(View.GONE);
                tabLayoutRoot.setVisibility(View.VISIBLE);
            }
        });

        toolbarRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.showSearch();
            }
        });

        continentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idContinent = position;
                switch (idContinent) {
                    case 0:
                        statesSpinner.setItem(statesAmerica);
                        statesSpinner.setSelection(0, true);
                        listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesNorthAmerica));
                        break;
                    case 1:
                        statesSpinner.setItem(statesAsiaPacific);
                        statesSpinner.setSelection(0, true);
                        listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesCentralAsia));
                        break;
                    case 2:
                        statesSpinner.setItem(statesEurope);
                        statesSpinner.setSelection(0, true);
                        listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesNorthernEurope));
                        break;
                    case 3:
                        statesSpinner.setItem(statesMiddleEastAfrica);
                        statesSpinner.setSelection(0, true);
                        listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesMiddleEast));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        statesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (idContinent == 0) {  //  America
                    switch (position) {
                        case 0:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesNorthAmerica));
                            break;
                        case 1:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesSouthAmerica));
                            break;
                        case 2:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesCentralAmerica));
                            break;
                        case 3:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesCaribbean));
                            break;
                        default:
                            break;
                    }

                } else if (idContinent == 1) {  //  Asia-Pacific
                    switch (position) {
                        case 0:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesCentralAsia));
                            break;
                        case 1:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesSouthAsia));
                            break;
                        case 2:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesNortheastAsia));
                            break;
                        case 3:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesSoutheastAsia));
                            break;
                        case 4:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesAustraliaAndOceania));
                            break;
                        default:
                            break;
                    }

                } else if (idContinent == 2) {  //  Eupore
                    switch (position) {
                        case 0:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesNorthernEurope));
                            break;
                        case 1:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesSouthernEurope));
                            break;
                        case 2:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesEasternEurope));
                            break;
                        case 3:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesWesternEurope));
                            break;
                        default:
                            break;
                    }

                } else if (idContinent == 3) {  //  Middle East / Africa
                    switch (position) {
                        case 0:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesMiddleEast));
                            break;
                        case 1:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesNorthAfrica));
                            break;
                        case 2:
                            listViewNavigation.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesSouthernAfrica));
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listViewNavigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {

                    String itemSelectListView = listViewNavigation.getAdapter().getItem(position).toString();

                    getAllDataByFragments(itemSelectListView);

                    searchView.closeSearch();
                    drawerLayout.closeDrawer(navigationView);
                    toolbarRoot.setTitle(itemSelectListView);
                } else {
                    dialogUntil.showDialogNoUpdateData(MainActivity.this);
                }
            }
        });

        txt_celsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCelsiusFahrenheit = "°C";
                drawerLayout.closeDrawer(navigationView);

                getAllDataByFragments((String) toolbarRoot.getTitle());
                saveFileTemperature(MainActivity.this, checkCelsiusFahrenheit);

                txt_celsius.setBackgroundColor(Color.LTGRAY);
                txt_celsius.setTextColor(Color.WHITE);

                txt_fahrenheit.setBackgroundColor(Color.WHITE);
                txt_fahrenheit.setTextColor(Color.BLACK);
            }
        });

        txt_fahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCelsiusFahrenheit = "°F";
                drawerLayout.closeDrawer(navigationView);

                getAllDataByFragments((String) toolbarRoot.getTitle());
                saveFileTemperature(MainActivity.this, checkCelsiusFahrenheit);

                txt_fahrenheit.setBackgroundColor(Color.LTGRAY);
                txt_fahrenheit.setTextColor(Color.WHITE);

                txt_celsius.setBackgroundColor(Color.WHITE);
                txt_celsius.setTextColor(Color.BLACK);
            }
        });
    }

    public String getCityCurrentLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 5678);
                } else {
                    gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            } else {
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();

        } else if (network_loc != null) {
            final_loc = network_loc;
            latitude = network_loc.getLatitude();
            longitude = network_loc.getLongitude();
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                defaultCity = addresses.get(0).getAdminArea();
                toolbarRoot.setTitle(defaultCity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultCity;
    }

    public void getAllDataByFragments(String city) {
        Fragment fragmentNow = myFragmentAdapter.getItem(0);
        ((NowFragment) fragmentNow).getDataCityLonLat(city);

        Fragment fragmentDaily = myFragmentAdapter.getItem(1);
        ((DailyFragment) fragmentDaily).getDataCityLonLat(city);

        Fragment fragmentRadar = myFragmentAdapter.getItem(2);
        ((RadarFragment) fragmentRadar).getDataCityLonLat(city);

        if (checkAdvancedNestedScrollView == true)
            checkAdvancedNestedScrollView = false;
    }

    private void newStatesSpinner() {
        statesAmerica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.state_america)));
        statesAsiaPacific = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.state_asia_pacific)));
        statesEurope = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.state_europe)));
        statesMiddleEastAfrica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.state_middleEast_africa)));
    }

    private void newCitieByStatesSpinner() {

        //  newCitiesAmericaByStates
        citiesNorthAmerica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_north_america)));
        citiesSouthAmerica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_south_america)));
        citiesCentralAmerica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_central_america)));
        citiesCaribbean = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_caribbean)));

        //  newCitiesPacificByStates
        citiesCentralAsia = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_central_asisa)));
        citiesSouthAsia = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_south_asisa)));
        citiesNortheastAsia = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_northeast_asisa)));
        citiesSoutheastAsia = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_southeast_asisa)));
        citiesAustraliaAndOceania = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_australia_and_oceania)));

        //  newCitiesEuporeByStates
        citiesNorthernEurope = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_northern_europe)));
        citiesSouthernEurope = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_southern_europe)));
        citiesEasternEurope = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_eastern_europe)));
        citiesWesternEurope = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cities_western_europe)));

        //  newCitiesMiddleEastAfricaByStates
        citiesMiddleEast = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.citise_middle_east)));
        citiesNorthAfrica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.citise_north_africa)));
        citiesSouthernAfrica = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.citise_southern_africa)));
    }

    private void loadFileSearchHistory() {
        ArrayList<String> items = new ArrayList<String>();
        try {
            FileInputStream fs_in = openFileInput("historySearch.txt");
            InputStreamReader is = new InputStreamReader(fs_in);
            BufferedReader br = new BufferedReader(is);
            String line = br.readLine();
            String temp = "";
            while (line != null) {
                if (!line.equals("#")) {
                    temp += line;
                } else {
                    items.add(temp);
                    temp = "";
                }
                line = br.readLine();
            }
            br.close();

            searchHistoryArrayList = new ArrayList<>();
            for (int i = items.size() - 1; i >= 0; i--) {
                searchHistoryArrayList.add(new SearchHistory(items.get(i).toString()));
            }

            searchHistoryAdapter = new SearchHistoryAdapter(this, R.layout.item_listview_search_history, searchHistoryArrayList);
            listViewSearchHistory.setAdapter(searchHistoryAdapter);
            searchHistoryAdapter.notifyDataSetChanged();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileHSearchistory(Context context, String citySave) {

        boolean checkContain = false; // biến kiểm tra city có trong file hay không

        try {
            File file = new File(context.getFilesDir(), "historySearch.txt");
            if (file.exists()) {
                // Đọc file để lấy data trong file
                ArrayList<String> items = new ArrayList<String>();
                FileInputStream fileInputStream = context.openFileInput("historySearch.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String lineRead = bufferedReader.readLine();
                String temp = "";
                while (lineRead != null) {
                    if (!lineRead.equals("#")) {
                        temp += lineRead;
                    } else {
                        items.add(temp);
                        temp = "";
                    }
                    lineRead = bufferedReader.readLine();
                }
                bufferedReader.close();

                // Lấy data trong file đã đọc lưu trong mảng rồi đem so trùng vs city nhập vào
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).contains(citySave)) { // trùng thì thoát vòng lặp
                        checkContain = true; //trùng
                        break;

                    } else {
                        checkContain = false;
                    }
                }

                // checkContain == false tức là city chưa có trong file nên ghi vào
                if (checkContain == false) {

                    if (items.size() >= 5) { // kiểm tra mảng items đọc từ file đã đủ 5 item hay chưa
                        items.remove(0); // xóa item đầu
                        items.add(citySave); // lưu city vào mảng items

                        file.delete(); // xóa dữ liệu trong file

                        FileOutputStream fs_out = context.openFileOutput("historySearch.txt", Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);

                        for (int i = 0; i < items.size(); i++) { // ghi dữ liệu mới gồm 5 item vào file
                            os.write(items.get(i) + "\n" + "#\n");
                        }
                        os.close();
                    } else {
                        FileOutputStream fs_out = context.openFileOutput("historySearch.txt", Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);
                        String lineSave = citySave + "\n" + "#\n";
                        os.write(lineSave);
                        os.close();
                    }
                }

            } else { // lần đầu cài app thì sẽ chưa có file nên tạo và lưu dữ liệu city lấy từ GPS vào file
                FileOutputStream fs_out = context.openFileOutput("historySearch.txt", Context.MODE_APPEND);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);
                String lineOneRun = citySave + "\n" + "#\n"; // GPS
                os.write(lineOneRun);
                os.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadFileTemperature() {

        String temperatureMeasurementUnit = "";

        try {
            FileInputStream fileInputStream = openFileInput("temperature.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            bufferedReader.close();
            temperatureMeasurementUnit = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return temperatureMeasurementUnit;
    }

    public void saveFileTemperature(Context context, String dvtTemp) {
        try {
            File checkFileIsExists = new File(context.getFilesDir(), "temperature.txt");
            FileOutputStream fileOutputStream = null;

            if (!checkFileIsExists.exists() || fileOutputStream == null) {
                fileOutputStream = context.openFileOutput("temperature.txt", Context.MODE_PRIVATE);
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(dvtTemp);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                searchView.showSearch();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 5678 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                getCityCurrentLocation();

            } else {
                dialogUntil.showDialogWarningPermission(MainActivity.this);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            dialogUntil.showDialogExit(MainActivity.this);
        }
    }

}