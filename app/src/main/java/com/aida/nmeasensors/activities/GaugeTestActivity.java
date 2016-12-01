package com.aida.nmeasensors.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aida.nmeasensors.R;
import com.aida.nmeasensors.utils.Gauge;

public class GaugeTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge_test);

        Gauge  gaugeTest= (Gauge)findViewById(R.id.gaugeTest);
        gaugeTest.setStartAngle(135);
        gaugeTest.setSweepAngle(275);
        gaugeTest.setSpeed(25);
        gaugeTest.setMaxSpeed(100);



    }


}
