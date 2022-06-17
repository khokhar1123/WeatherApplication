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
import java.util.List;
import java.util.Locale;

public class HwAdapter extends RecyclerView.Adapter<HwViewHolder> {

    private final List<HourlyWeather> hList;

    private final MainActivity mainacitvityIn;

    public HwAdapter(List<HourlyWeather> hList, MainActivity mainacitvityIn) {
        this.hList = hList;
        this.mainacitvityIn = mainacitvityIn;
    }

    @NonNull
    @Override
    public HwViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View hourlyView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hholder, parent, false);

        hourlyView.setOnClickListener(mainacitvityIn);
// FOR CALENDAR EXTRA CREDIT
        return new HwViewHolder(hourlyView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HwViewHolder holder, int position) {
        HourlyWeather hourly = hList.get(position);
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(Long.parseLong(hourly.date) +
                Long.parseLong(mainacitvityIn.timezone_offset), 0, ZoneOffset.UTC);
        LocalDateTime controlLdt = LocalDateTime.ofEpochSecond(Long.parseLong(hList.get(0).date) +
                Long.parseLong(mainacitvityIn.timezone_offset), 0, ZoneOffset.UTC);
        DateTimeFormatter df =
                DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());
        DateTimeFormatter tf =
                DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
        String currDay = controlLdt.format(df);
        String tempDay = ldt.format(df);
        String Time = ldt.format(tf);
        if (tempDay.equals(currDay)) {
            tempDay = "Today";
        }
        holder.DAY.setText(tempDay);
        holder.TIME.setText(Time);
        holder.TEMP.setText(String.format(Locale.getDefault(), "%.0f%s",
                Double.parseDouble(hourly.temp), mainacitvityIn.suffix));
        String firstletter = hourly.w.desc.substring(0,1).toUpperCase();
        String[] descData = hourly.w.desc.split(" ");
        String firstword = descData[0].substring(1);
        String secondletter="";
        String secondword="";
        String tword="";
        String tletter="";
        if(descData.length > 1){
        secondletter = descData[1].substring(0,1).toUpperCase();
        secondword = descData[1].substring(1);}
        if(descData.length > 2) {
            tletter = descData[2].substring(0, 1).toUpperCase();
            for (int i = 2; i < descData.length; i++) {
                tword += descData[i].substring(1);
            }
        }
        holder.DESC.setText(String.format(Locale.getDefault(), "%S%s %S%s %S%s", firstletter,firstword,secondletter,secondword,tletter,tword));
        int iconResId = mainacitvityIn.getResources().getIdentifier("_" + hourly.w.ic,
                "drawable", mainacitvityIn.getPackageName());
        holder.IMAGE.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return hList.size();
    }
}
