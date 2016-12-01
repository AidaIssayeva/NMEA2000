package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class SensorEnviroment {
    @SerializedName("temperature")
    private Temperature temperature;
    @SerializedName("wind")
    private Wind wind;
    @SerializedName("humidity")
    private Humidity humidity;
    @SerializedName("barometricpressure")
    private BarPressure barPressure;

    public void setBarPressure(BarPressure barPressure) {
        this.barPressure = barPressure;
    }

    public void setHumidity(Humidity humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public BarPressure getBarPressure() {
        return barPressure;
    }

    public Humidity getHumidity() {
        return humidity;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Wind getWind() {
        return wind;
    }
}
