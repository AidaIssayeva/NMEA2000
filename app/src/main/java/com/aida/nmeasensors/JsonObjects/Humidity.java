package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Humidity {
    @SerializedName("outside")
    private Outside outside;

    public void setOutside(Outside outside) {
        this.outside = outside;
    }

    public Outside getOutside() {
        return outside;
    }
}
