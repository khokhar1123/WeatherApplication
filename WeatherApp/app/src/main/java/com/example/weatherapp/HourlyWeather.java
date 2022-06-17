package com.example.weatherapp;

import java.io.Serializable;

public class HourlyWeather implements Serializable {

    public String date;
    public String temp;
    public WeatherBasic w;
    public String pop;

    HourlyWeather(String d, String t, WeatherBasic w, String p) {
        this.date = d;
        this.temp = t;
        this.w = w;
        this.pop = p;
    }

}