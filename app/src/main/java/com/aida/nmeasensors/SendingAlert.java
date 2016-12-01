package com.aida.nmeasensors;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.JsonObjects.Trigger;

import java.util.NavigableMap;

/**
 * Created by AIssayeva on 9/20/16.
 */

public class SendingAlert {
    private NmeaSensor mSentValue;

    private Trigger mtrigger;


    public SendingAlert(NmeaSensor sensorName,Trigger trigger) {

        mSentValue = sensorName;

        mtrigger=trigger;
    }


public Trigger getTrigger(){
    return mtrigger;
}

    public  NmeaSensor getResultValue() {
        return mSentValue;
    }

}
