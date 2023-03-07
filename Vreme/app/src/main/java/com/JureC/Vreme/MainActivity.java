package com.JureC.Vreme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityTimeTV, cityNameTV, cityDateTV,temperatureTV, conditionTV, sunriseTV, sunsetTV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private ArrayList<DayRVModal> dayRVModalArrayList;
    private DayRVAdapter dayRVAdapter;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityTimeTV = findViewById(R.id.idTVCityTime);
        cityNameTV = findViewById(R.id.idTVCityName);
        cityDateTV = findViewById(R.id.idTVCityDate);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        sunriseTV = findViewById(R.id.idTVSunrise);
        sunsetTV = findViewById(R.id.idTVSunset);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        RecyclerView weatherRV = findViewById(R.id.idRVWeather);
        RecyclerView dayRV = findViewById(R.id.idRVDay);
        ImageView searchIV = findViewById(R.id.idIVSearch);

        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);

        weatherRV.setAdapter(weatherRVAdapter);

        dayRVModalArrayList = new ArrayList<>();
        dayRVAdapter = new DayRVAdapter(this, dayRVModalArrayList);

        dayRV.setAdapter(dayRVAdapter);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        getWeatherInfo(cityName);

        searchIV.setOnClickListener(v -> {

            String city = Objects.requireNonNull(cityEdt.getText()).toString();

            if (city.isEmpty()) {

                Toast.makeText(MainActivity.this, "Prosim vnesite ime mesta", Toast.LENGTH_SHORT).show();

            } else {

                cityNameTV.setText(cityName);

                getWeatherInfo(city);

            }

        } );

    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Dovoljenje odobreno ... ", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, "Prosimo za dovoljenje ", Toast.LENGTH_SHORT).show();

                finish();

            }

        }

    }

    private String getCityName(double longitude, double latitude) {

        String cityName = "Ne obstaja";

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {

            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adr : addresses) {

                if (adr != null) {

                    String city = adr.getLocality();

                    if (city != null && !city.equals("")) {

                        cityName = city;

                    } else {

                        Log.d("TAG", "MESTO NE OBSTAJA");

                        Toast.makeText(this, "Mesto ne obstaja", Toast.LENGTH_SHORT).show();

                    }

                }

            }


        } catch (IOException e) {

            e.printStackTrace();

        }

        return cityName;

    }

    private void getWeatherInfo(String cityName) {

        String url = "http://api.weatherapi.com/v1/forecast.json?key=ca3ee04fb9a14f278f9154644223101&q=" + cityName + "&days=7&aqi=no&alerts=no\n";

        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this::onResponse, error -> Toast.makeText(MainActivity.this, "Prosim vnesite ime mesta ...", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);

    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void onResponse(JSONObject response) {

        loadingPB.setVisibility(View.GONE);

        homeRL.setVisibility(View.VISIBLE);

        weatherRVModalArrayList.clear();

        dayRVModalArrayList.clear();

        try {

            String temperature = response.getJSONObject("current").getString("temp_c");

            temperatureTV.setText(temperature + "°C");

            int isDay = response.getJSONObject("current").getInt("is_day");

            String cityTime = response.getJSONObject("location").getString("localtime");

            cityTimeTV.setText(cityTime.substring(cityTime.length() - 5));

            // Pridobitev angleškega imena stanja

            String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");

            // Pridobitev ikone vremena

            String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

            // Pridobitev dneva v tednu, datuma ter zadnje ure posodobitve

            String[] weekDay = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

            String[] weekDan = {"Ponedeljek", "Torek", "Sreda", "Četrtek", "Petek", "Sobota", "Nedelja"};

            // Pridobivanje časa zadnje posodobitve

            String week = response.getJSONObject("current").getString("last_updated");

            // Priprava formatov datuma in časa

            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");

            SimpleDateFormat outputA = new SimpleDateFormat("EEEE");

            SimpleDateFormat outputB = new SimpleDateFormat("d.M");

            String week1 = "", week2 = "";

            // Pridobivanje angleškega dneva v tednu in datuma

            try {

                Date weekA = input.parse(week);

                week1 = outputA.format(weekA);

                Date weekB = input.parse(week);

                week2 = outputB.format(weekB);

            } catch (ParseException e) {

                e.printStackTrace();

            }

            // Pridobivanje slovenskega dneva v tednu

            for (int i = 0; i < weekDay.length; i++) {

                if (weekDay[i].equals(week1)) {

                    week1 = weekDan[i];

                }

            }

            cityDateTV.setText(week1 + ", " + week2 + ", " + week.substring(week.length() - 6));

            // Pridobitev slike

            Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);

            // Pridobitev slovenskega imena stanja

            String[] conditionDay = {"Sunny", "Partly cloudy", "Cloudy", "Overcast", "Mist", "Patchy rain possible", "Patchy snow possible", "Patchy sleet possible", "Patchy freezing drizzle possible", "Thundery outbreaks possible", "Blowing snow", "Blizzard", "Fog", "Freezing fog", "Patchy light drizzle", "Light drizzle", "Freezing drizzle", "Heavy freezing drizzle", "Patchy light rain", "Light rain", "Moderate rain at times", "Moderate rain", "Heavy rain at times", "Heavy rain", "Light freezing rain", "Moderate or heavy freezing rain", "Light sleet", "Moderate or heavy sleet", "Patchy light snow", "Light snow", "Patchy moderate snow", "Moderate snow", "Patchy heavy snow", "Heavy snow", "Ice pellets", "Light rain shower", "Moderate or heavy rain shower", "Torrential rain shower", "Light sleet showers", "Moderate or heavy sleet showers", "Light snow showers", "Moderate or heavy snow showers", "Moderate or heavy showers of ice pellets", "Patchy light rain with thunder", "Moderate or heavy rain with thunder", "Patchy light snow with thunder", "Moderate or heavy snow with thunder"};

            String[] conditionNight = {"Clear", "Partly cloudy", "Cloudy", "Overcast", "Mist", "Patchy rain possible", "Patchy snow possible", "Patchy sleet possible", "Patchy freezing drizzle possible", "Thundery outbreaks possible", "Blowing snow", "Blizzard", "Fog", "Freezing fog", "Patchy light drizzle", "Light drizzle", "Freezing drizzle", "Heavy freezing drizzle", "Patchy light rain", "Light rain", "Moderate rain at times", "Moderate rain", "Heavy rain at times", "Heavy rain", "Light freezing rain", "Moderate or heavy freezing rain", "Light sleet", "Moderate or heavy sleet", "Patchy light snow", "Light snow", "Patchy moderate snow", "Moderate snow", "Patchy heavy snow", "Heavy snow", "Ice pellets", "Light rain shower", "Moderate or heavy rain shower", "Torrential rain shower", "Light sleet showers", "Moderate or heavy sleet showers", "Light snow showers", "Moderate or heavy snow showers", "Moderate or heavy showers of ice pellets", "Patchy light rain with thunder", "Moderate or heavy rain with thunder", "Patchy light snow with thunder", "Moderate or heavy snow with thunder"};

            // Pridobivanje slike ozadja

            String[] conditionPhoto = {"https://images.unsplash.com/photo-1541119638723-c51cbe2262aa?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/Hnv4vp7/pexels-darius-krause-2470655.jpg", "https://images.unsplash.com/photo-1566228015668-4c45dbc4e2f5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/dBS7QtJ/despo-potamou-JJ0-Kuh9-Y6g-unsplash.jpg", "https://images.unsplash.com/photo-1640932288985-15f14d9fe490?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/pvGyX6J/jan-willem-Fobwh-DUgdrk-unsplash.jpg", "https://images.unsplash.com/photo-1482784160316-6eb046863ece?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1608562718716-1e4b1a703703?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/gb87JHg/pexels-matej-746527.jpg", "https://i.ibb.co/xhkf85Q/pexels-lachlan-ross-6510344.jpg", "https://i.ibb.co/fFZrvfV/pexels-anna-shvets-7258182.jpg", "https://images.unsplash.com/photo-1584614101036-e991679274b5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1485236715568-ddc5ee6ca227?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1606579215835-d5b457f3e5eb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1629724769029-7e086b821a6b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1630574232726-fc3ea90637b8?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1609252954132-89360ddb0db5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/B43Qm1F/pexels-guillaume-meurice-3363555.jpg", "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/ZMF5xr7/pexels-sourav-mishra-1251293.jpg", "https://images.unsplash.com/photo-1544365558-35aa4afcf11f?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1534274988757-a28bf1a57c17?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1498847559558-1e4b1a7f7a2f?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1438260483147-81148f799f25?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1622332581170-6f05fb760ae1?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/8sRLMfS/pexels-alex-conchillos-3888585.jpg", "https://images.unsplash.com/photo-1608562718716-1e4b1a703703?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1608562718716-1e4b1a703703?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://i.ibb.co/yNhhXMB/pexels-simon-berger-688660.jpg", "https://i.ibb.co/yNhhXMB/pexels-simon-berger-688660.jpg", "https://i.ibb.co/yNhhXMB/pexels-simon-berger-688660.jpg", "https://i.ibb.co/yNhhXMB/pexels-simon-berger-688660.jpg", "https://i.ibb.co/yNhhXMB/pexels-simon-berger-688660.jpg", "https://i.ibb.co/yNhhXMB/pexels-simon-berger-688660.jpg", "https://images.unsplash.com/photo-1478265409131-1f65c88f965c?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1604064111410-80340bc39926?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1974&q=80", "https://images.unsplash.com/photo-1604064111410-80340bc39926?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1974&q=80", "https://images.unsplash.com/photo-1532203512255-3c9c9d666c50?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1604064111410-80340bc39926?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1974&q=80", "https://images.unsplash.com/photo-1604064111410-80340bc39926?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1974&q=80", "https://i.ibb.co/Fm2Gnsc/pexels-roberto-shumski-6102920.jpg", "https://i.ibb.co/Fm2Gnsc/pexels-roberto-shumski-6102920.jpg", "https://i.ibb.co/Fm2Gnsc/pexels-roberto-shumski-6102920.jpg", "https://i.ibb.co/CMXjr1r/pexels-ekrulila-2838561.jpg", "https://i.ibb.co/CMXjr1r/pexels-ekrulila-2838561.jpg", "https://images.unsplash.com/photo-1492011221367-f47e3ccd77a0?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80", "https://images.unsplash.com/photo-1492011221367-f47e3ccd77a0?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3014&q=80"};

            String[] conditionDan = {"Sončno", "Zmerno oblačno", "Oblačno", "Oblačno", "Meglica", "Mogoč občasno deževanje", "Mogoče občasno sneženje", "Mogoče občasno žled", "Mogoče občasno zmrzovanje", "Izbruhi nevihte možno", "Pihanje snega", "Snežna megla", "Megla", "Zamrzovalna megla", "Rahlo rosenje", "Rahlo rosenje", "Zmrzovalno rosenje", "Močno zmrzovalno rosenje", "Kripasto rahlo dež", " Rahel dež", "Občasno zmerno dež", "Zmerno dež", "Občasno močan dež", "Močno dež", "Rahel dež z ledom", "Zmerno ali močno ledišče", "Rahel žled", "Zmerno oz. močan žled", "Kripasti rahli sneg", "Rahel sneg", "Kripasti zmeren sneg", "Zmerno sneženje", "Klapasto močan sneg", "Močno sneženje", "Ledeni peleti", "Rahla ploha", "Zmerno ali močna ploha", "Močna ploha", "Rahle plohe žled", "Zmerne ali močne plohe žled", "Rahle snežne plohe", "Zmerne ali močne snežne plohe", "Zmerne ali močne plohe žledu", "Nereden rahli dež z grmenjem", "Zmeren ali močan dež z grmenjem", "Nepravilen dež sneg z grmenjem", "Zmeren ali močan sneg z grmenjem"};

            String[] conditionNoc = {"Jasno", "Zmerno oblačno", "Oblačno", "Oblačno", "Meglica", "Mogoč občasno deževanje", "Mogoče občasno sneženje", "Mogoče občasno žled", "Mogoče občasno zmrzovanje", "Izbruhi nevihte možno", "Pihanje snega", "Snežna megla", "Megla", "Zamrzovalna megla", "Rahlo rosenje", "Rahlo rosenje", "Zmrzovalno rosenje", "Močno zmrzovalno rosenje", "Kripasto rahlo dež", " Rahel dež", "Občasno zmerno dež", "Zmerno dež", "Občasno močan dež", "Močno dež", "Rahel dež z ledom", "Zmerno ali močno ledišče", "Rahel žled", "Zmerno oz. močan žled", "Kripasti rahli sneg", "Rahel sneg", "Kripasti zmeren sneg", "Zmerno sneženje", "Klapasto močan sneg", "Močno sneženje", "Ledeni peleti", "Rahla ploha", "Zmerno ali močna ploha", "Močna ploha", "Rahle plohe žled", "Zmerne ali močne plohe žled", "Rahle snežne plohe", "Zmerne ali močne snežne plohe", "Zmerne ali močne plohe žledu", "Nereden rahli dež z grmenjem", "Zmeren ali močan dež z grmenjem", "Nepravilen dež sneg z grmenjem", "Zmeren ali močan sneg z grmenjem"};

            for (int i = 0; i < conditionDay.length; i++) {

                if (isDay == 1 && condition.equals(conditionDay[i])) {

                    condition = conditionDan[i];

                    Picasso.get().load(conditionPhoto[i]).into(backIV);

                } else if (condition.equals(conditionNight[i])) {

                        condition = conditionNoc[i];

                        Picasso.get().load("https://images.unsplash.com/photo-1531828051742-b644a99e319d?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1974&q=80").into(backIV);

                }

            }

            conditionTV.setText(condition);

            JSONObject forecastObj = response.getJSONObject("forecast");

            JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);

            if (isDay == 1) {

                searchIV.setImageResource(R.drawable.searchicon);

            } else {

                searchIV.setImageResource(R.drawable.search);

            }

            // Pridobitev časa vzhoda in zahoda sonca

            String sunrise = forecast0.getJSONObject("astro").getString("sunrise");

            String sunset = forecast0.getJSONObject("astro").getString("sunset");

            sunrise = sunrise.replace(':', ' ');

            sunset = sunset.replace(':', ' ');

            String[] sunriseC = sunrise.split(" ");

            String[] sunsetC = sunset.split(" ");

            int sunriseA = Integer.parseInt(sunriseC[0]);

            int sunriseB = Integer.parseInt(sunriseC[1]);

            int sunsetA = Integer.parseInt(sunsetC[0]);

            int sunsetB = Integer.parseInt(sunsetC[1]);

            String sunriseD = "", sunsetD = " ";

            if (sunriseC[sunriseC.length - 1].equals("AM")) {
                
                if (sunriseB < 10) {

                    sunriseD = String.valueOf(sunriseA + ":0" + sunriseB);
                    
                } else {

                    sunriseD = String.valueOf(sunriseA + ":" + sunriseB);

                }

            } else {

                if (sunriseB < 10) {

                    sunriseD = String.valueOf((sunriseA + 12) + ":0" + sunriseB);

                } else {

                    sunriseD = String.valueOf((sunriseA + 12) + ":" + sunriseB);

                }

            }

            if (sunsetC[sunsetC.length - 1].equals("AM")) {

                if (sunsetB < 10) {

                    sunsetD = String.valueOf(sunsetA + ":0" + sunsetB);

                } else {

                    sunsetD = String.valueOf(sunsetA + ":" + sunsetB);

                }

            } else {

                if (sunsetB < 10) {

                    sunsetD = String.valueOf((sunsetA + 12) + ":0" + sunsetB);

                } else {

                    sunsetD = String.valueOf((sunsetA + 12) + ":" + sunsetB);

                }

            }

            sunriseTV.setText(sunriseD);

            sunsetTV.setText(sunsetD);

            JSONArray hourArray = forecast0.getJSONArray("hour");

            for (int i = 0; i < hourArray.length(); i++) {

                JSONObject hourObj = hourArray.getJSONObject(i);

                String time = hourObj.getString("time");

                String temper = hourObj.getString("temp_c");

                String img = hourObj.getJSONObject("condition").getString("icon");

                double rain = hourObj.getDouble("precip_mm");

                weatherRVModalArrayList.add(new WeatherRVModal(time, temper, img, rain));

            }

            weatherRVAdapter.notifyDataSetChanged();

            JSONArray dayArray = forecastObj.getJSONArray("forecastday");

            for (int i = 0; i < dayArray.length(); i++) {

                JSONObject dayObj = dayArray.getJSONObject(i);

                String day = dayObj.getString("date");

                String temperatureA = dayObj.getJSONObject("day").getString("avgtemp_c");

                String icon = dayObj.getJSONObject("day").getJSONObject("condition").getString("icon");

                String rain = dayObj.getJSONObject("day").getString("daily_chance_of_rain");

                dayRVModalArrayList.add(new DayRVModal(day, temperatureA, icon, rain));

            }

            dayRVAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

}