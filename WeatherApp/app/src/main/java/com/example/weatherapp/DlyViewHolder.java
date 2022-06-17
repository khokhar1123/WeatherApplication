package com.example.weatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DlyViewHolder extends RecyclerView.ViewHolder {

    TextView DayDate;
    TextView TempHL;
    TextView Clds;
    TextView Prec;
    TextView UV;
    TextView temp1;
    TextView temp8;
    TextView temp5;
    TextView temp11;
    ImageView Image;

    public DlyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.DayDate = itemView.findViewById(R.id.DayDate);
        this.TempHL = itemView.findViewById(R.id.TempHL);
        this.Clds = itemView.findViewById(R.id.Clds);
        this.Prec = itemView.findViewById(R.id.Prec);
        this.UV = itemView.findViewById(R.id.UV);
        this.temp8 = itemView.findViewById(R.id.temp8);
        this.temp1 = itemView.findViewById(R.id.temp1);
        this.temp5 = itemView.findViewById(R.id.temp5);
        this.temp11 = itemView.findViewById(R.id.temp11);
        this.Image = itemView.findViewById(R.id.Image);
    }
}
