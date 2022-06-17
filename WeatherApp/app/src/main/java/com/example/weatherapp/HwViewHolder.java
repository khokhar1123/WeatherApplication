package com.example.weatherapp;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HwViewHolder extends RecyclerView.ViewHolder {
    TextView DAY;
    TextView TIME;
    TextView TEMP;
    TextView DESC;
    ImageView IMAGE;

    public HwViewHolder(@NonNull View v) {
        super(v);
        this.DAY= itemView.findViewById(R.id.dData);
        this.TIME = itemView.findViewById(R.id.TData);
        this.TEMP= itemView.findViewById(R.id.TeData);
        this.DESC= itemView.findViewById(R.id.DescData);
        this.IMAGE= itemView.findViewById(R.id.imageIcon);



    }
}
