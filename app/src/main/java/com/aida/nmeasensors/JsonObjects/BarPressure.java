package com.aida.nmeasensors.JsonObjects;

import com.aida.nmeasensors.utils.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class BarPressure extends NmeaSensor{

    @SerializedName("value")
    private long value;
    private String metrics;
    private double datas;
    private int fromSharedPrefUnit;
    public BarPressure(int shared){
        this.fromSharedPrefUnit=shared;
    }
    @Override
    public String getName() {
        return  "Barometric Pressure";
    }
    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
    @Override
    public void setData(double data) {
        if(fromSharedPrefUnit!=-1) {
            switch (fromSharedPrefUnit) {
                case 0:
                    datas = (data*0.000145038);
                    break;
                case 1:
                    datas = (data/1000);
                    break;
                case 2:
                    datas = (data*0.0002953);
                    break;
                default:
                    break;
            }
        }else{
            datas=data/1000;
        }


    }
    @Override
    public double getData() {

        return datas;
    }
    @Override
    public String getMetric() {
        if (fromSharedPrefUnit != -1) {
            switch (fromSharedPrefUnit) {
                case 0:
                    metrics = Constants.PSI;
                    break;
                case 1:
                    metrics = Constants.KPA;
                    break;
                case 2:
                    metrics = Constants.INHG;
                    break;
                default:
                    break;

            }
        } else {
            metrics = Constants.KPA;
        }
        return metrics;
    }
}
