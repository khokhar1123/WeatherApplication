package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DlyAct extends AppCompatActivity {
    private RecyclerView drview;
    private DlyAdapter dAdapter;
    private ArrayList<DailyWeather> dList;

    private String loc;

    public String suffix;
    public String tzoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_dly);

        Intent intent = getIntent();
        try {
            this.suffix = intent.getStringExtra("suf");
            this.dList = (ArrayList<DailyWeather>) intent.getSerializableExtra("dly");
            this.loc = intent.getStringExtra("loc");
            this.tzoff = intent.getStringExtra("tzoff");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setTitle(loc);
        dAdapter = new DlyAdapter(this, dList);
        drview = findViewById(R.id.dlyRecycler);
        drview.setAdapter(dAdapter);
        drview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


    }
}
