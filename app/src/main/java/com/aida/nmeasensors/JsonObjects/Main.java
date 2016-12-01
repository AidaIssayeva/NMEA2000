package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Main {
//    @SerializedName("self")
//    private String self;

    @SerializedName("vessels")
    private Vessels vessels;

//    public void setSelf(String self) {
//        this.self = self;
//    }
//
//    public String getSelf() {
//        return self;
//    }

    public Vessels getVessels() {
        return vessels;
    }

    public void setVessels(Vessels vessels) {
        this.vessels = vessels;
    }
}
