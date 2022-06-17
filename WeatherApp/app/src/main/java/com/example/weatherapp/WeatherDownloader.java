package com.example.weatherapp;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class WeatherDownloader {

    private static MainActivity mainActivity;
    private static RequestQueue queue;

    private static final String weatherURL = "https://api.openweathermap.org/data/2.5/onecall";
    private static final String yourAPIKey = "71fb1fbd51892097684ebb349f04e96f";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void downloadWeather(MainActivity mainActivityIn, String location, double[] locData, boolean fahrenheit) {
        mainActivity = mainActivityIn;
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(weatherURL).buildUpon();

        buildURL.appendQueryParameter("lat", String.format(Locale.getDefault(), "%f", locData[0]));
        buildURL.appendQueryParameter("lon", String.format(Locale.getDefault(), "%f", locData[1]));
        buildURL.appendQueryParameter("appid", yourAPIKey);
        buildURL.appendQueryParameter("units", (fahrenheit ? "imperial" : "metric"));
        buildURL.appendQueryParameter("lang", "en");
        buildURL.appendQueryParameter("exclude", "minutely");
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
//              response -> mainActivity.updateData(parseJSON(response.toString()));
                response  ->mainActivity.runOnUiThread(() -> mainActivity.updateData(parseJSON(response.toString())));
        Response.ErrorListener error =
                error1 -> mainActivity.updateData(null);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        queue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static WeatherMeta parseJSON(String s) {
        try {
            JSONObject Jsonresponse = new JSONObject(s);

            String tz = Jsonresponse.getString("timezone");
            String tzoff = Jsonresponse.getString("timezone_offset");

            JSONObject currentResponse = Jsonresponse.getJSONObject("current");
            CurrentWeather curr = CurrParsing(currentResponse);

            JSONArray Jhrly = Jsonresponse.getJSONArray("hourly");
            ArrayList<HourlyWeather> Hrly = new ArrayList<>();
            for (int i = 0; i < Jhrly.length(); i++) {
                HourlyWeather h = HrlyParsing((JSONObject) Jhrly.get(i));
                Hrly.add(h);
            }
            JSONArray Jdly = Jsonresponse.getJSONArray("daily");
            ArrayList<DailyWeather> Dly = new ArrayList<>();
            for (int i = 0; i < Jdly.length(); i++) {
                DailyWeather d = DlyParing((JSONObject) Jdly.get(i));
                Dly.add(d);
            }
            WeatherMeta w = new WeatherMeta(tz, tzoff, curr,
                    Hrly, Dly);
            return new WeatherMeta(tz, tzoff, curr,
                    Hrly, Dly);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static CurrentWeather CurrParsing(JSONObject current) {

        try {

            String dtime = current.getString("dt");
            String sr = current.getString("sunrise");
            String ss = current.getString("sunset");
            String temp = current.getString("temp");
            String fl = current.getString("feels_like");
            String press = current.getString("pressure");
            String hum = current.getString("humidity");
            String uv = current.getString("uvi");
            String clds = current.getString("clouds");
            String vis = current.getString("visibility");
            String ws = current.getString("wind_speed");
            String wd = current.getString("wind_deg");
            String wg;
            try {
                wg = current.getString("wind_gust");
            } catch (Exception e) {
                wg = "";
            }
            JSONArray weatherArray = current.getJSONArray("weather");
            WeatherBasic weather = WthrParsing(weatherArray);
            return new CurrentWeather(dtime, sr, ss, temp, fl, press, hum, uv,
                    clds, vis, ws, wd, wg, weather);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static DailyWeather DlyParing(JSONObject daily) {
        try {
            String date = daily.getString("dt");
            JSONObject tempObj = daily.getJSONObject("temp");
            String day = tempObj.getString("day");
            String min = tempObj.getString("min");
            String max = tempObj.getString("max");
            String night = tempObj.getString("night");
            String eve = tempObj.getString("eve");
            String morn = tempObj.getString("morn");
            JSONArray weatherArray = daily.getJSONArray("weather");
            WeatherBasic weather = WthrParsing(weatherArray);
            String pop = daily.getString("pop");
            String uvi = daily.getString("uvi");
            return new DailyWeather(date, day, min, max, night, eve, morn, weather, pop, uvi);

        } catch (Exception e) {
            return null;
        }
    }
    private static WeatherBasic WthrParsing(JSONArray weatherArray) {
        try {
            JSONObject wthr = (JSONObject) weatherArray.get(0);
            String id = wthr.getString("id");
            String main = wthr.getString("main");
            String desc = wthr.getString("description");
            String ic = wthr.getString("icon");
            return new WeatherBasic(id, main, desc, ic);
        } catch (Exception e) {
            return null;
        }
    }

    private static HourlyWeather HrlyParsing(JSONObject hourly) {
        try {
            String dtime = hourly.getString("dt");
            String temp = hourly.getString("temp");
            String p = hourly.getString("pop");
            JSONArray weatherArray = hourly.getJSONArray("weather");
            WeatherBasic weather = WthrParsing(weatherArray);
            return new HourlyWeather(dtime, temp, weather, p);

        } catch (Exception e) {
            return null;
        }
    }




}