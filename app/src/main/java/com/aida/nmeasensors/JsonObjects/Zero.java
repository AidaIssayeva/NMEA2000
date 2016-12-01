package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Zero {
    @SerializedName("engineroom")
    private EngineRoom engineRoom;
    @SerializedName("outside")
    private OutsideTemp outsideTemp;

    public void setEngineRoom(EngineRoom engineRoom) {
        this.engineRoom = engineRoom;
    }

    public void setOutsideTemp(OutsideTemp outsideTemp) {
        this.outsideTemp = outsideTemp;
    }

    public EngineRoom getEngineRoom() {
        return engineRoom;
    }

    public OutsideTemp getOutsideTemp() {
        return outsideTemp;
    }
}
