package com.aida.nmeasensors;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 8/9/16.
 */
public class SubscriberSend {
 private   int mSentValue;

    public SubscriberSend(int sensorName) {

        mSentValue = sensorName;
    }



    public  int getResultValue() {
        return mSentValue;
    }
}
