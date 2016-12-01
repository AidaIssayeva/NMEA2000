package com.aida.nmeasensors.JsonObjects;

import com.aida.nmeasensors.utils.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class OutsideTemp extends NmeaSensor {
    @SerializedName("value")
    private double value;
    private String metrics;
    private double datas;
    private int fromSharedPrefUnit;
    public OutsideTemp(int fromSharedPrefUnit){
        this.fromSharedPrefUnit=fromSharedPrefUnit;
    }
    @Override
    public String getName() {
        return  "Outside Temperature";
    }
    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
    @Override
    public void setData(double data) {
        if(fromSharedPrefUnit!=-1) {
            switch (fromSharedPrefUnit) {
                case 0:
                    datas = (data - 273.15);
                    break;
                case 1:
                    datas = ((data * 9 / 5) - 459.67);
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
    @Override
    public String getMetric() {
        if (fromSharedPrefUnit != -1) {
            switch (fromSharedPrefUnit) {
                case 0:
                    metrics = Constants.CELSIUS;
                    break;
                case 1:
                    metrics = Constants.FARENHEIT;
                    break;
                case 2:
                    metrics = Constants.KELVIN;
                    break;
                default:
                    break;

            }
        } else {
            metrics = Constants.KELVIN;
        }
        return metrics;
    }
}
