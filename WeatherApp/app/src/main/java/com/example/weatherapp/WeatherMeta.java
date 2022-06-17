package com.example.weatherapp;

import java.io.Serializable;
import java.util.ArrayList;


public class WeatherMeta implements Serializable {

    public String tzone;
    public String offset;
    public CurrentWeather curr;
    public ArrayList<HourlyWeather> hrly;
    public ArrayList<DailyWeather> dly;

    WeatherMeta(String tzone, String offset, CurrentWeather curr,
                ArrayList<HourlyWeather> hrly, ArrayList<DailyWeather> dly) {
        this.tzone = tzone;
        this.offset = offset;
        this.curr = curr;
        this.hrly = hrly;
        this.dly = dly;
    }

}
