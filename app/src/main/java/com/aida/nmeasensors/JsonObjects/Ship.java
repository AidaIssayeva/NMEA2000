package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Ship {

    @SerializedName("environment")
    private SensorEnviroment sensorEnviroment;

    public SensorEnviroment getSensorEnviroment() {
        return sensorEnviroment;
    }

    public void setSensorEnviroment(SensorEnviroment sensorEnviroment) {
        this.sensorEnviroment = sensorEnviroment;
    }
}
