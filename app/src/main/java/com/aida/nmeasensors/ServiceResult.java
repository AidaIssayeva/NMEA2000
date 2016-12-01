package com.aida.nmeasensors;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by AIssayeva on 8/9/16.
 */
public class ServiceResult {

   private ArrayList<NmeaSensor> mResultValue;

    public ServiceResult(ArrayList<NmeaSensor> resultValue) {

        mResultValue = resultValue;
    }



    public  ArrayList<NmeaSensor> getResultValue() {
        return mResultValue;
    }
}
