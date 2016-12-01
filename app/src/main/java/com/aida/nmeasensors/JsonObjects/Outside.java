package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Outside extends NmeaSensor {
    @SerializedName("value")
    private double value;
    @Override
    public String getName() {
        return  "Outside Humidity";
    }
    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
