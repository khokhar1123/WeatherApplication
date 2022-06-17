package com.example.weatherapp;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class DlyAdapter extends RecyclerView.Adapter<DlyViewHolder> {

    private final DlyAct dlyAct;
    private final ArrayList<DailyWeather> dList;

    public DlyAdapter(DlyAct dlyAct, ArrayList<DailyWeather> dList) {
        this.dlyAct = dlyAct;
        this.dList = dList;
    }


    @NonNull
    @Override
    public DlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dlyholder, parent, false);

        return new DlyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DlyViewHolder holder, int position) {
        DailyWeather dlywthr = dList.get(position);
        LocalDateTime localdata = LocalDateTime.ofEpochSecond(
                Long.parseLong(dlywthr.date) + Long.parseLong(dlyAct.tzoff),
                0, ZoneOffset.UTC);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, M/dd", Locale.getDefault());
        String formdate = localdata.format(dtf);
        holder.DayDate.setText(formdate);
        holder.TempHL.setText(String.format(Locale.getDefault(),
                "%.0f%s/%.0f%s", Double.parseDouble(dlywthr.max), dlyAct.suffix,
                Double.parseDouble(dlywthr.min), dlyAct.suffix));
        String firstletter = dlywthr.weather.desc.substring(0,1).toUpperCase();
        String[] weatherdata = dlywthr.weather.desc.split(" ");
        String firstword = weatherdata[0].substring(1);
        String secondword="";
        String secondletter="";
        String tword="";
        String tletter="";
        if(weatherdata.length > 1){
            secondletter = weatherdata[1].substring(0,1).toUpperCase();
            secondword = weatherdata[1].substring(1);}
        if(weatherdata.length > 2){
            tletter = weatherdata[2].substring(0,1).toUpperCase();
            for(int i =2; i< weatherdata.length;i++){
            tword += weatherdata[i].substring(1);
            }
        }
        holder.Clds.setText(String.format(Locale.getDefault(),
                "%S%s %S%s %S%s", firstletter,firstword,secondletter,secondword,tletter,tword));
        holder.Prec.setText(String.format(Locale.getDefault(),
                "(%.0f%% precip.)", Double.parseDouble(dlywthr.pop) * 100));
        holder.UV.setText(String.format(Locale.getDefault(),
                "UV Index: %.0f", Double.parseDouble(dlywthr.uv)));
        holder.temp8.setText(String.format(Locale.getDefault(),
                "%.0f%s", Double.parseDouble(dlywthr.morn), dlyAct.suffix));
        holder.temp1.setText(String.format(Locale.getDefault(),
                "%.0f%s", Double.parseDouble(dlywthr.day), dlyAct.suffix));
        holder.temp5.setText(String.format(Locale.getDefault(),
                "%.0f%s", Double.parseDouble(dlywthr.evening), dlyAct.suffix));
        holder.temp11.setText(String.format(Locale.getDefault(),
                "%.0f%s", Double.parseDouble(dlywthr.night), dlyAct.suffix));
        String iconCode = "_" + dlywthr.weather.ic;
        int iconResId = dlyAct.getResources().getIdentifier(iconCode,
                "drawable",  dlyAct.getPackageName());
        holder.Image.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return  dList.size();
    }
}
