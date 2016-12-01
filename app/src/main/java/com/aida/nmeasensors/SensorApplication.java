package com.aida.nmeasensors;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by AIssayeva on 8/30/16.
 */
public class SensorApplication  extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
