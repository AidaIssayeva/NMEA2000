package com.aida.nmeasensors.JsonObjects;

import android.content.SharedPreferences;

import com.aida.nmeasensors.utils.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class SpeedApparent extends NmeaSensor {
    @SerializedName("value")
    private double value;
    private String metrics;
    private double datas;
    private int fromSharedPrefUnit;

    public SpeedApparent(int sharedPrefUnit) {
        this.fromSharedPrefUnit = sharedPrefUnit;
    }

    @Override
    public String getName() {
        return "Apparent wind speed";
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String getMetric() {
        if (fromSharedPrefUnit != -1) {
            switch (fromSharedPrefUnit) {
                case 0:
                    metrics = Constants.KNOTS;
                    break;
                case 1:
                    metrics = Constants.MPH;
                    break;
                case 2:
                    metrics = Constants.MPS;
                    break;
                default:
                    break;

            }
        } else {
            metrics = Constants.MPS;
        }
        return metrics;
    }

    public double getValue() {
        return value;
    }

    @Override
    public void setData(double data) {
        if(fromSharedPrefUnit!=-1) {
            switch (fromSharedPrefUnit) {
                case 0:
                    datas = (data * 1.94384449);
                    break;
                case 1:
                    datas = (data *2.23694);
                    break;
                case 2:
                    datas = data;
                    break;
                default:
                    break;
            }
        }else{
            datas=data;
        }

    }
    @Override
    public double getData() {

        return datas;
    }
}
