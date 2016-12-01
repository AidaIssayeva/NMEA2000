package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Vessels {
    @SerializedName("urn:mrn:imo:mmsi:0")
    private Ship ship;

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }
}
