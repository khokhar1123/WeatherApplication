package com.example.weatherapp;

import java.io.Serializable;

public class CurrentWeather implements Serializable {

    public String date;
    public String sr;
    public String ss;
    public String temp;
    public String fl;
    public String press;
    public String hum;
    public String uv;
    public String clds;
    public String vis;
    public String Wspeed;
    public String Wdeg;
    public String gust;

    public WeatherBasic weather;

    public CurrentWeather(String dt, String sr, String ss, String temp, String fl,
                   String press, String hum, String uv, String clds, String vis,
                   String Wspeed, String Wdeg, String gust, WeatherBasic weather) {

        this.date = dt;
        this.sr = sr;
        this.ss = ss;
        this.temp = temp;
        this.fl = fl;
        this.press = press;
        this.hum = hum;
        this.uv = uv;
        this.clds = clds;
        this.vis = vis;
        this.Wspeed = Wspeed;
        this.Wdeg = Wdeg;
        this.gust = gust;
        this.weather = weather;
    }
}