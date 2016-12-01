package com.aida.nmeasensors.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import com.aida.nmeasensors.R;
import com.aida.nmeasensors.utils.Constants;

/**
 * Created by AIssayeva on 8/9/16.
 */
public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private RadioButton celsius,farenheit,kelvin;
    private RadioButton psi,kpa,inHg;
    private RadioButton knots,mph,mps;
    private RadioButton radian,degree,inVis;
    private Button toTheNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        configureActionBar();
        toTheNext=(Button)findViewById(R.id.gotoSensors);
        RelativeLayout tempLayout=(RelativeLayout) findViewById(R.id.radioTemp);
        RadioGroup temperature=(RadioGroup)tempLayout.findViewById(R.id.radioGroup);
         celsius=(RadioButton)tempLayout.findViewById(R.id.radio1);
        farenheit=(RadioButton)tempLayout.findViewById(R.id.radio2);
         kelvin=(RadioButton)tempLayout.findViewById(R.id.radio3);
        sharedPref =getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        if(sharedPref !=null){
            int intTemp=sharedPref.getInt(Constants.SHARED_PREF_TEMP,-1);
            if(intTemp==0){
                celsius.setChecked(true);
                farenheit.setChecked(false);
                kelvin.setChecked(false);
            }else if(intTemp==1){
                celsius.setChecked(false);
                farenheit.setChecked(true);
                kelvin.setChecked(false);
            }else if(intTemp==2){
                celsius.setChecked(false);
                farenheit.setChecked(false);
                kelvin.setChecked(true);
            }
        }
        temperature.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (celsius.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_TEMP, 0);
                    editor.commit();


                } else if (farenheit.isChecked()) {

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_TEMP, 1);
                    editor.commit();


                } else if (kelvin.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_TEMP, 2);
                    editor.commit();

                }
            }
        });
        RelativeLayout angleLayout=(RelativeLayout)findViewById(R.id.radioAngle);
        RadioGroup angle=(RadioGroup)angleLayout.findViewById(R.id.radioGroup);
        radian=(RadioButton)angleLayout.findViewById(R.id.radio1);
        degree=(RadioButton) angleLayout.findViewById(R.id.radio2);
        inVis=(RadioButton) angleLayout.findViewById(R.id.radio3);
        radian.setText("Radian");
        degree.setText("Degree");
        inVis.setVisibility(View.INVISIBLE);
        if(sharedPref !=null){
            int intAngle=sharedPref.getInt(Constants.SHARED_PREF_ANGLE,-1);
            if(intAngle==0){
                radian.setChecked(true);
                degree.setChecked(false);

            }else if(intAngle==1) {
                radian.setChecked(false);
                degree.setChecked(true);

            }
        }
        angle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radian.isChecked()){

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_ANGLE, 0);
                    editor.commit();


                } else if (degree.isChecked()) {

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_ANGLE, 1);
                    editor.commit();


                }
            }
        });

        RelativeLayout pressLayout=(RelativeLayout)findViewById(R.id.radioPress);
        RadioGroup pressure=(RadioGroup) pressLayout.findViewById(R.id.radioGroup);
        psi=(RadioButton) pressLayout.findViewById(R.id.radio1);
        kpa=(RadioButton) pressLayout.findViewById(R.id.radio2);
        inHg=(RadioButton) pressLayout.findViewById(R.id.radio3);
        psi.setText("PSI");
        kpa.setText("kPascal");
        inHg.setText("inHg");
        if(sharedPref !=null){
            int intPres=sharedPref.getInt(Constants.SHARED_PREF_PRES,-1);
            if(intPres==0){
                psi.setChecked(true);
                kpa.setChecked(false);
                inHg.setChecked(false);
            }else if(intPres==1){
                psi.setChecked(false);
                kpa.setChecked(true);
                inHg.setChecked(false);
            }else if(intPres==2){
                psi.setChecked(false);
                kpa.setChecked(false);
                inHg.setChecked(true);
            }
        }
        pressure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (psi.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_PRES, 0);
                    editor.commit();


                } else if (kpa.isChecked()) {

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_PRES, 1);
                    editor.commit();


                } else if (inHg.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_PRES, 2);
                    editor.commit();

                }
            }
        });
        RelativeLayout windLayout=(RelativeLayout)findViewById(R.id.radioWind);
        RadioGroup wind=(RadioGroup) windLayout.findViewById(R.id.radioGroup);
        knots=(RadioButton) windLayout.findViewById(R.id.radio1);
        mph=(RadioButton) windLayout.findViewById(R.id.radio2);
        mps=(RadioButton) windLayout.findViewById(R.id.radio3);
        knots.setText("knots");
        mph.setText("MPH");
        mps.setText("MPS");
        if(sharedPref !=null){
            int intPres=sharedPref.getInt(Constants.SHARED_PREF_WIND,-1);
            if(intPres==0){
                knots.setChecked(true);
                mph.setChecked(false);
                mps.setChecked(false);
            }else if(intPres==1){
                knots.setChecked(false);
                mph.setChecked(true);
                mps.setChecked(false);
            }else if(intPres==2){
                knots.setChecked(false);
                mph.setChecked(false);
                mps.setChecked(true);
            }
        }
        wind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (knots.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_WIND, 0);
                    editor.commit();


                } else if (mph.isChecked()) {

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_WIND, 1);
                    editor.commit();


                } else if (mps.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.SHARED_PREF_WIND, 2);
                    editor.commit();

                }
            }
        });
        if(wind.getCheckedRadioButtonId() !=-1 && temperature.getCheckedRadioButtonId()!=-1 && pressure.getCheckedRadioButtonId()!=-1){
            toTheNext.setVisibility(View.VISIBLE);
            toTheNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this,ViewPagerActivity.class));
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void configureActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }
}
