package com.kinitoapps.moneymanager;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kinitoapps.moneymanager.piechart.PieGraph;
import com.kinitoapps.moneymanager.piechart.PieSlice;

public class DailyStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_stats);
        final PieGraph pg = findViewById(R.id.graph);
        pg.setInnerCircleRatio(160);
        PieSlice slice;
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#F55454"));
        slice.setValue(1000);
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#43C443"));
        slice.setValue(5000);
        pg.addSlice(slice);
    }
}
