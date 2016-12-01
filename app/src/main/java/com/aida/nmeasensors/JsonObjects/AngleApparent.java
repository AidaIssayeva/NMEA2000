package com.aida.nmeasensors.JsonObjects;

import com.aida.nmeasensors.utils.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class AngleApparent extends NmeaSensor{
    @SerializedName("value")
    private double value;
    private String metrics;
    private double datas;
    private int sharedPref;
    @Override
    public String getName() {
        return  "Apparent wind angle";
    }
    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public AngleApparent(int sharedPref){
        this.sharedPref=sharedPref;
    }
    @Override
    public String getMetric() {
        if (sharedPref != -1) {
            switch (sharedPref) {
                case 0:
                    metrics = Constants.RADIAN;
                    break;
                case 1:
                    metrics = Constants.DEGREE;
                    break;
                default:
                    break;

            }
        } else {
            metrics = Constants.RADIAN;
        }
        return metrics;
    }



    @Override
    public void setData(double data) {
        if(sharedPref!=-1) {
            switch (sharedPref) {
                case 0:
                    datas = data ;
                    break;
                case 1:
                    datas = (data * 57.2958);
                    if(datas>360){
                        datas=datas-360;
                    }
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
