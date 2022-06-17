package com.example.weatherapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView locData;
    private TextView dtData;
    private TextView TempData;
    private TextView flData;
    private TextView weatherData;
    private TextView windData;
    private TextView humData;
    private TextView uvData;
    private TextView visData;
    private TextView morTempData;
    private TextView morTime;
    private TextView dTempData;
    private TextView dTime;
    private TextView eTempData;
    private TextView eTime;
    private TextView nTempData;
    private TextView nTime;
    private TextView sunrData;
    private TextView sunsData;
    private String  DefaultLocation= "Chicago, IL";
    private ImageView icon;

    private HwAdapter hwAdapter;
    private String  rtimeLoc;
    public String timezone;
    public String timezone_offset;
    public String suffix;
    private String perHour;
    private boolean fahrenheit;
    private double[] latlon;
    private Menu optmenu;
    private RecyclerView hrecycler;

    private final ArrayList<DailyWeather> dList = new ArrayList<>();

    private final ArrayList<HourlyWeather> hList = new ArrayList<>();
    private SwipeRefreshLayout swiper;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locData = findViewById(R.id.city_country);
        dtData = findViewById(R.id.currentTime);
        TempData = findViewById(R.id.currentTemp);
        flData = findViewById(R.id.feelsLike);
        icon = findViewById(R.id.weatherIcon);
        weatherData = findViewById(R.id.weatherDesc);
        windData = findViewById(R.id.wind);
        humData = findViewById(R.id.hum);
        uvData = findViewById(R.id.uv);
        visData = findViewById(R.id.vis);
        morTempData = findViewById(R.id.mTemp);
        morTime = findViewById(R.id.mTempTime);
        dTempData = findViewById(R.id.dTemp);
        dTime = findViewById(R.id.dTempTime);
        eTempData = findViewById(R.id.eTemp);
        eTime = findViewById(R.id.eTempTime);
        nTempData = findViewById(R.id.nTemp);
        nTime = findViewById(R.id.nTempTime);
        sunrData = findViewById(R.id.sunrise);
        sunsData = findViewById(R.id.sunset);
        hrecycler = findViewById(R.id.hourlyRecycler);
        swiper = findViewById(R.id.swiper);

        hwAdapter = new HwAdapter(hList, this);
        hrecycler.setAdapter(hwAdapter);
        hrecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        swiper.setOnRefreshListener(this::OnRefresh);//EC Stuff
//        swiper.setOnRefreshListener((this) -> {
//            if (hasNetworkConnection()) {
//                Toast.makeText("No network connection found.", Toast.LENGTH_LONG).show();
//            } else {
//                doDownload();
//            }
//            swiper.setRefreshing(false);
//        });
        if (!this.hasNetworkConnection()) {
            dtData.setText("No Internet Connection");
            makeVisible("Invisible");
            return;
        }

        makeVisible("Invisible");
        hList.clear();
        dList.clear();
//Preferences implementing the part for saving data -> Extra Credit
        myPrefs = getPreferences(Context.MODE_PRIVATE);
        prefsEditor = myPrefs.edit();
        if (!myPrefs.contains("funit")) {
            prefsEditor.putBoolean("funit", true);
            prefsEditor.apply();
        }
        latlon = new double[2];

        if (myPrefs.contains("lat")) {
            this.DefaultLocation = myPrefs.getString("Loc", "Chicago, IL");
            this.latlon[0] = Double.parseDouble(myPrefs.getString("lat", "41.8675766"));
            this.latlon[1] = Double.parseDouble(myPrefs.getString("lon", "-87.616232"));
        }
        fahrenheit = myPrefs.getBoolean("funit", true);
//Extra Credit End
        suffix = fahrenheit ? "°F" : "°C";
        perHour = fahrenheit ? "mph" : "kph";

        this.doDownload();
        this.setTitle("OpenWeather App");
    }


    public void makeVisible(String visibility){
        if (visibility.equals("Visible")) {
            morTime.setVisibility(View.VISIBLE);
            dTime.setVisibility(View.VISIBLE);
            eTime.setVisibility(View.VISIBLE);
            nTime.setVisibility(View.VISIBLE);
        }
        else {
            morTime.setVisibility(View.INVISIBLE);
            dTime.setVisibility(View.INVISIBLE);
            eTime.setVisibility(View.INVISIBLE);
            nTime.setVisibility(View.INVISIBLE);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.opt_menu, menu);

        this.optmenu = menu;
        MenuItem units = this.optmenu.getItem(0);
        units.setIcon(fahrenheit ? R.drawable.units_f : R.drawable.units_c);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doDownload() {
        rtimeLoc = this.doLocationName(DefaultLocation);
        latlon = this.doLatLon(DefaultLocation);
        if ((rtimeLoc==null) || (latlon==null)){   //Handling geocoder null return by deafulting chicago,Il
            rtimeLoc="Chicago, Illinois";
            latlon= new double[]{41.8675766,-87.616232};
        }
        WeatherDownloader.downloadWeather(this, rtimeLoc, latlon, fahrenheit);
//        makeVisible("Visible");
    }

    private String doLocationName(String defaultLocation) {
        Geocoder gc = new Geocoder(this);
        try {
            List<Address> address =
                    gc.getFromLocationName(defaultLocation, 1);

            if (address == null || address.isEmpty()) {
                return null;
            }
            String cnty = address.get(0).getCountryCode();
            if (cnty == null) {
                return null;
            }
            Address addressItem = address.get(0);
            String fname;
            String aarea;
            if (cnty.equals("US")) {
                fname = addressItem.getFeatureName();
                aarea = addressItem.getAdminArea();
            } else {
                fname = addressItem.getLocality();
                if (fname == null)
                    fname = addressItem.getFeatureName();
                aarea = addressItem.getCountryName();
            }
            if (fname == null || fname.isEmpty()) return null;
            if (aarea == null || aarea.isEmpty()) return null;
            rtimeLoc = fname + ", " + aarea;
            return rtimeLoc;
        } catch (IOException e) {
            return null;
        }
    }

    private double[] doLatLon(String defaultLocation) {
        Geocoder gc = new Geocoder(this);
        try {
            List<Address> address =
                    gc.getFromLocationName(defaultLocation, 1);

            if (address == null || address.isEmpty()) {
                return null;
            }
            double lat = address.get(0).getLatitude();
            double lon = address.get(0).getLongitude();

            return new double[] {lat, lon};

        } catch (IOException e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateData(WeatherMeta weather) {

        this.timezone = weather.tzone;
        this.timezone_offset = weather.offset;

        LocalDateTime ldt =
                LocalDateTime.ofEpochSecond(
                        Long.parseLong(weather.curr.date) + Long.parseLong(timezone_offset),
                        0, ZoneOffset.UTC);

        DateTimeFormatter dtf =
                DateTimeFormatter.ofPattern("EEE MMM dd h:mm a, yyyy", Locale.getDefault());

        String formattedTimeString = ldt.format(dtf);

        this.locData.setText(this.rtimeLoc);

        this.dtData.setText(formattedTimeString);

        this.TempData.setText(String.format(Locale.getDefault(), "%.0f%s",
                Double.parseDouble(weather.curr.temp), suffix));

        this.flData.setText(String.format(Locale.getDefault(), "Feels Like %.0f%s",
                Double.parseDouble(weather.curr.fl), suffix));

        String iconCode = "_" + weather.curr.weather.ic;
        int iconResId = this.getResources().getIdentifier(iconCode, "drawable", this.getPackageName());
        this.icon.setImageResource(iconResId);
        String firstletter = weather.curr.weather.desc.substring(0,1).toUpperCase();
        String[] weatherdata = weather.curr.weather.desc.split(" ");
        String firstword = weatherdata[0].substring(1);
        String secondword="";
        String secondletter="";
        String tword="";
        String tletter="";
        if (weatherdata.length == 1){
            this.weatherData.setText(String.format(Locale.getDefault(), "%S%s (%d%% clouds)",
                    firstletter,firstword,Integer.parseInt(weather.curr.clds)));
        }else if((weatherdata.length > 1) && (weatherdata.length <= 2)){
            secondletter = weatherdata[1].substring(0,1).toUpperCase();
            secondword = weatherdata[1].substring(1);
            this.weatherData.setText(String.format(Locale.getDefault(), "%S%s %S%s (%d%% clouds)",
                    firstletter,firstword,secondletter,secondword,Integer.parseInt(weather.curr.clds)));
        }else {
            if (weatherdata.length > 2) {
                tletter = weatherdata[2].substring(0, 1).toUpperCase();
                for (int i = 2; i < weatherdata.length; i++) {
                    tword += weatherdata[i].substring(1);
                }
                this.weatherData.setText(String.format(Locale.getDefault(), "%S%s %S%s %S%s (%d%% clouds)",
                        firstletter, firstword, secondletter, secondword, tletter, tword, Integer.parseInt(weather.curr.clds)));
            }
        }

        String direc = this.getDirection(Double.parseDouble(weather.curr.Wdeg));
        this.windData.setText(String.format(Locale.getDefault(), "Winds: %s at %.0f %s", direc,
                Double.parseDouble(weather.curr.Wspeed), perHour));

        this.humData.setText(String.format(Locale.getDefault(), "Humidity: %.0f%%",
                Double.parseDouble(weather.curr.hum)));

        this.uvData.setText(String.format(Locale.getDefault(), "UV Index: %.0f",
                Double.parseDouble(weather.curr.uv)));

        if (fahrenheit) {
            this.visData.setText(String.format(Locale.getDefault(), "Visibility: %.1f mi",
                    Double.parseDouble(weather.curr.vis) * 0.000621371192));
        } else {
            this.visData.setText(String.format(Locale.getDefault(), "Visibility: %.1f km",
                    Double.parseDouble(weather.curr.vis) / 1000));
        }
        makeVisible("Visible");
        this.morTempData.setText(String.format(Locale.getDefault(), "%.0f%s",
                Double.parseDouble(weather.dly.get(0).morn), suffix));

        this.dTempData.setText(String.format(Locale.getDefault(), "%.0f%s",
                Double.parseDouble(weather.dly.get(0).day), suffix));

        this.eTempData.setText(String.format(Locale.getDefault(), "%.0f%s",
                Double.parseDouble(weather.dly.get(0).evening), suffix));

        this.nTempData.setText(String.format(Locale.getDefault(), "%.0f%s",
                Double.parseDouble(weather.dly.get(0).night), suffix));
//
        LocalDateTime srf =
                LocalDateTime.ofEpochSecond(
                        Long.parseLong(weather.curr.sr) + Long.parseLong(weather.offset),
                        0, ZoneOffset.UTC);

        LocalDateTime ssf =
                LocalDateTime.ofEpochSecond(
                        Long.parseLong(weather.curr.ss) + Long.parseLong(weather.offset),
                        0, ZoneOffset.UTC);
        this.sunrData.setText(String.format(Locale.getDefault(), "Sunrise: %s",
                srf.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))));

        this.sunsData.setText(String.format(Locale.getDefault(), "Sunset: %s",
                ssf.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))));

        hList.clear();
        hList.addAll(weather.hrly);
        hwAdapter.notifyDataSetChanged();

        dList.clear();
        dList.addAll(weather.dly);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!this.hasNetworkConnection()) {
            Toast.makeText(this, "Functionality cannot be used without a connection.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.changeLoc) {
            this.LocSet();
        }
        if (item.getItemId() == R.id.ToggleUn){
            if (fahrenheit) {
                item.setIcon(R.drawable.units_c);
                fahrenheit = false;
                prefsEditor.putBoolean("funit", false);
            } else {
                item.setIcon(R.drawable.units_f);
                fahrenheit = true;
                prefsEditor.putBoolean("funit", true);
            }
            hList.clear();
            this.doDownload();
            suffix = fahrenheit ? "°F" : "°C";
            perHour = fahrenheit ? "mph" : "kph";
            prefsEditor.apply();
        }
        if (item.getItemId() == R.id.ShowDailyForecast){
            Intent intent = new Intent(this, DlyAct.class);
            intent.putExtra("suf", this.suffix);
            intent.putExtra("dly", this.dList);
            intent.putExtra("loc", this.rtimeLoc);
            intent.putExtra("tzoff", this.timezone_offset);
            startActivity(intent);
        }
    return true;

    }
    public void LocSet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText loc = new EditText(this);
        loc.setInputType(InputType.TYPE_CLASS_TEXT);
        loc.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(loc);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                if (doLocationName(loc.getText().toString()) == null) {
                    Toast.makeText(getApplicationContext(), "Entered Location is Not Valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                DefaultLocation = loc.getText().toString();
                prefsEditor.putString("Loc", doLocationName(DefaultLocation));
                prefsEditor.putString("lat", String.valueOf(doLatLon(DefaultLocation)[0]));
                prefsEditor.putString("lon", String.valueOf(doLatLon(DefaultLocation)[1]));
                prefsEditor.apply();
                doDownload();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setTitle("Enter a Location");
        builder.setMessage("For US locations, enter as 'City', or 'City, State'\n\nFor International locations, enter as 'City, Country'");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X";
    }
//Below is the extra credit stuff
 @RequiresApi(api = Build.VERSION_CODES.O)
    private void OnRefresh() {
        if (!this.hasNetworkConnection()) {
                Toast.makeText(this,"You're Not Connected To The Network. Try Later.", Toast.LENGTH_LONG).show();
            } else {
               this.doDownload();
                suffix = fahrenheit ? "°F" : "°C";
                perHour = fahrenheit ? "mph" : "kph";
            }
            swiper.setRefreshing(false);
    }

//Extra Credit to open calendar
    public void onClick(View view) {
        int pos = hrecycler.getChildLayoutPosition(view);
        HourlyWeather hrlyDate = hList.get(pos);
        long CalTime = Long.parseLong(hrlyDate.date) *1000 ;
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, CalTime);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }
}