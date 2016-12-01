package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Temperature {
    @SerializedName("0")
    private Zero zero;

    public void setZero(Zero zero) {
        this.zero = zero;
    }

    public Zero getZero() {
        return zero;
    }
}
