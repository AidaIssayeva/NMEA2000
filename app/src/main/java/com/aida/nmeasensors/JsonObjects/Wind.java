package com.aida.nmeasensors.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AIssayeva on 8/18/16.
 */
public class Wind {
    @SerializedName("angleApparent")
    private AngleApparent angleApparent;
    @SerializedName("speedApparent")
    private SpeedApparent speedApparent;

    public void setAngleApparent(AngleApparent angleApparent) {
        this.angleApparent = angleApparent;
    }

    public void setSpeedApparent(SpeedApparent speedApparent) {
        this.speedApparent = speedApparent;
    }

    public AngleApparent getAngleApparent() {
        return angleApparent;
    }

    public SpeedApparent getSpeedApparent() {
        return speedApparent;
    }
}
