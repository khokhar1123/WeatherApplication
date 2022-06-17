package com.example.weatherapp;

import java.io.Serializable;

public class WeatherBasic implements Serializable {

    public String id;
    public String main;
    public String desc;
    public String ic;

    WeatherBasic(String id, String main, String description, String icon) {
        this.id = id;
        this.main = main;
        this.desc = description;
        this.ic = icon;
    }
}
