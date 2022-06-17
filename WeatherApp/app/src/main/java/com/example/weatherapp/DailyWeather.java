package com.example.weatherapp;

import java.io.Serializable;

public class DailyWeather implements Serializable {

    public String date;

    public String day, min, max, night, evening, morn;
    public WeatherBasic weather;
    public String pop;
    public String uv;

    public DailyWeather(String date, String day, String min, String max, String night, String evening, String morn, WeatherBasic weather, String pop, String uv) {
        this.date = date;

        this.day=day;
        this.min=min;
        this.max=max;
        this.night=night;
        this.evening=evening;
        this.morn=morn;
        this.weather = weather;
        this.pop = pop;
        this.uv = uv;
    }
}