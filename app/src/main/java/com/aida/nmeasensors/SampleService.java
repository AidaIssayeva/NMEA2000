package com.aida.nmeasensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aida.nmeasensors.JsonObjects.AngleApparent;
import com.aida.nmeasensors.JsonObjects.BarPressure;
import com.aida.nmeasensors.JsonObjects.EngineRoom;
import com.aida.nmeasensors.JsonObjects.Main;
import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.JsonObjects.Outside;
import com.aida.nmeasensors.JsonObjects.OutsideTemp;
import com.aida.nmeasensors.JsonObjects.SpeedApparent;
import com.aida.nmeasensors.JsonObjects.Trigger;
import com.aida.nmeasensors.utils.Constants;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by AIssayeva on 9/27/16.
 */

public class SampleService extends Service  implements Runnable{
    private Handler handler;
    private int delay=5000;
    private static final String LOG="SAMPLESERVICE";
    public static SharedPreferences sharedPreferences;
    private final static String HTTP_ADDRESS="http";
    private String address;
    private DBHandler dbHandler;
    private double engineRoom=0.0,outside=0.0,humidity=0.0,pressure=0.0,windSpeed=0.0,windAngle=0.0;
    private OutsideTemp outsideTemp;
    private EngineRoom engineRoomTemp;
    private Outside hum;
    private BarPressure press;
    private SpeedApparent speed;
    private AngleApparent angle;
    private int readLine=0;
    private  DBAlertHandler dbAlertHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler=new Handler();
        handler.post(this);
        EventBus.getDefault().register(this);
        sharedPreferences = this.
                getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        Log.v(LOG,"service created");
        address=sharedPreferences.getString(HTTP_ADDRESS,"");
        Log.v(LOG,"address from shared pref:"+address);
        dbHandler=new DBHandler(this);
        dbAlertHandler=new DBAlertHandler(this);

        sharedPreferences = this. getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE);

        handler.post(this);


    }
    private void amber(final NmeaSensor nmeaSensor, final Trigger trigger){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        final WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.amber_alert, null);

        TextView expla=(TextView) myView.findViewById(R.id.explanation);
        if(nmeaSensor.getData()>trigger.getTriggerValue()){
            expla.setText("Sensor "+nmeaSensor.getName()+" is more than "+trigger.getTriggerValue());
        }else if(nmeaSensor.getData()<trigger.getTriggerValue()){
            expla.setText("Sensor "+nmeaSensor.getName()+" is less than "+trigger.getTriggerValue());
        }else if (nmeaSensor.getData()==trigger.getTriggerValue()){
            expla.setText("Sensor "+nmeaSensor.getName()+" is equal to "+trigger.getTriggerValue());
        }

        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone ringtone = RingtoneManager.getRingtone(this, notification);
        ringtone.play();
        final Button disable=(Button)myView.findViewById(R.id.disableAlert);
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trigger.setNotifying(false);
                dbAlertHandler.updateTrigger(trigger);
                //}

                wm.removeView(myView);
                ringtone.stop();

            }
        });

        // Add layout to window manager
        wm.addView(myView, params);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SendingAlert serviceResult) {
        Log.v(LOG,"receiving event from fragment");
        amber(serviceResult.getResultValue(),serviceResult.getTrigger());



    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ServiceResult serviceResult) {
        Log.v(LOG,"receiving event from async");
        ArrayList list=serviceResult.getResultValue();
        ArrayList fromDb=dbHandler.getAllSensors();
        Log.v(LOG,"is list empty:"+list.isEmpty());
        if(list.isEmpty()){
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Please check NMEA bus connection",Toast.LENGTH_SHORT).show();
                }
            });

        }else {

            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < fromDb.size(); j++) {
                    NmeaSensor sensor = (NmeaSensor) list.get(i);
                    NmeaSensor ownSensor = (NmeaSensor) fromDb.get(j);
                    if (sensor.getName().equals(ownSensor.getName())) {
                        Log.v(LOG, "names equal");
                        ownSensor.setData(sensor.getData());
                        ownSensor.setMetric(sensor.getMetric());


                        dbHandler.updateSensor(ownSensor);

                    }

                }
            }
            for (int i = 0; i < fromDb.size(); i++) {
                NmeaSensor ownSensor = (NmeaSensor) fromDb.get(i);

                    ArrayList<Trigger> triggerLIst=dbAlertHandler.getAllTriggers();
                if(triggerLIst.isEmpty()) {
                    ownSensor.setActive(false);
                }else {
                    for (int k = 0; k < triggerLIst.size(); k++) {
                        Trigger trigger = triggerLIst.get(k);
                        if (ownSensor.getId() == trigger.getSensorId()) {
                            if (trigger.getTriggertype().equals(Constants.ABOVE)) {
                                if (ownSensor.getData() > trigger.getTriggerValue()) {
                                    ownSensor.setActive(true);
                                    if (trigger.getNotifying()) {

                                        EventBus.getDefault().post(new SendingAlert(ownSensor, trigger));
                                    }

                                }else{
                                    ownSensor.setActive(false);
                                }
                            } else if (trigger.getTriggertype().equals(Constants.BELOW)) {
                                if (ownSensor.getData() < trigger.getTriggerValue()) {
                                    ownSensor.setActive(true);
                                    if (trigger.getNotifying()) {

                                        EventBus.getDefault().post(new SendingAlert(ownSensor, trigger));
                                    }
                                }else{
                                    ownSensor.setActive(false);
                                }
                            } else if (trigger.getTriggertype().equals(Constants.EQUAL)) {
                                if (ownSensor.getData() == trigger.getTriggerValue()) {
                                    ownSensor.setActive(true);
                                    if (trigger.getNotifying()) {

                                        EventBus.getDefault().post(new SendingAlert(ownSensor, trigger));
                                    }
                                }else{
                                    ownSensor.setActive(false);
                                }
                            }

                        }
                    }

                }
                dbHandler.updateSensor(ownSensor);

            }
        }


    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(SubscriberSend serviceResult) {
        Log.v(LOG,"receiving event from fragment");


        try
        {
            int   sharedPress = sharedPreferences.getInt(Constants.SHARED_PREF_PRES, -1);
            int   sharedWind=sharedPreferences.getInt(Constants.SHARED_PREF_WIND,-1);
            int    sharedAngle=sharedPreferences.getInt(Constants.SHARED_PREF_ANGLE,-1);
            int   sharedTemp = sharedPreferences.getInt(Constants.SHARED_PREF_TEMP, -1);
            String fileData = loadSampleFile(readLine);

                Gson gson=new Gson();
               Main main=gson.fromJson(fileData,Main.class);
                if(main!=null) {
                    engineRoom = main.getVessels().getShip().getSensorEnviroment().getTemperature().getZero().getEngineRoom().getValue();
                    Log.v(LOG, "engine room temp:" + engineRoom);
                    outside = main.getVessels().getShip().getSensorEnviroment().getTemperature().getZero().getOutsideTemp().getValue();
                    humidity = main.getVessels().getShip().getSensorEnviroment().getHumidity().getOutside().getValue();
                    pressure = main.getVessels().getShip().getSensorEnviroment().getBarPressure().getValue();
                    windSpeed = main.getVessels().getShip().getSensorEnviroment().getWind().getSpeedApparent().getValue();
                    windAngle = main.getVessels().getShip().getSensorEnviroment().getWind().getAngleApparent().getValue();
                }


                hum=new Outside();
                hum.setMetric("%");

                outsideTemp=new OutsideTemp(sharedTemp);

                press=new BarPressure(sharedPress);

                engineRoomTemp=new EngineRoom(sharedTemp);

                speed=new SpeedApparent(sharedWind);

                angle=new AngleApparent(sharedAngle);


                hum.setData(humidity);
                outsideTemp.setData(outside);
                press.setData(pressure);
                engineRoomTemp.setData(engineRoom);
                speed.setData(windSpeed);
                angle.setData(windAngle);
                ArrayList<NmeaSensor> list=new ArrayList<>();
                list.add(hum);
                list.add(outsideTemp);
                list.add(press);
                list.add(engineRoomTemp);
                list.add(speed);
                list.add(angle);
                Log.v(LOG,"windSpeed:"+list.get(4).getData());
                EventBus.getDefault().post(new ServiceResult(list));

        }
        catch (Exception e)
        {
            Log.w(LOG, e);
        }


    }



    private String loadSampleFile(int line) throws IOException
    {
        final File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/sample.json");
        FileInputStream inputStream=new FileInputStream(file);
       // InputStream inputStream = getResources().openRawResource(R.raw.sample);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String receiveString="";
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<line; i++)
        {
            receiveString=bufferedReader.readLine();
//            stringBuilder.append(receiveString);
//            stringBuilder.append("\n");
        }

        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        Log.v(LOG,"received string:"+receiveString);

        return receiveString;

    }

    @Override
    public void run() {
        try {
            int   sharedPress = sharedPreferences.getInt(Constants.SHARED_PREF_PRES, -1);
            int   sharedWind=sharedPreferences.getInt(Constants.SHARED_PREF_WIND,-1);
            int    sharedAngle=sharedPreferences.getInt(Constants.SHARED_PREF_ANGLE,-1);
            int   sharedTemp = sharedPreferences.getInt(Constants.SHARED_PREF_TEMP, -1);
            String fileData = loadSampleFile(readLine);
            Gson gson=new Gson();
            Main main=gson.fromJson(fileData,Main.class);
            if(main!=null) {
                engineRoom = main.getVessels().getShip().getSensorEnviroment().getTemperature().getZero().getEngineRoom().getValue();
                Log.v(LOG, "engine room temp:" + engineRoom);
                outside = main.getVessels().getShip().getSensorEnviroment().getTemperature().getZero().getOutsideTemp().getValue();
                humidity = main.getVessels().getShip().getSensorEnviroment().getHumidity().getOutside().getValue();
                pressure = main.getVessels().getShip().getSensorEnviroment().getBarPressure().getValue();
                windSpeed = main.getVessels().getShip().getSensorEnviroment().getWind().getSpeedApparent().getValue();
                windAngle = main.getVessels().getShip().getSensorEnviroment().getWind().getAngleApparent().getValue();
            }


            hum=new Outside();
            hum.setMetric("%");

            outsideTemp=new OutsideTemp(sharedTemp);

            press=new BarPressure(sharedPress);

            engineRoomTemp=new EngineRoom(sharedTemp);

            speed=new SpeedApparent(sharedWind);

            angle=new AngleApparent(sharedAngle);


            hum.setData(humidity);
            outsideTemp.setData(outside);
            press.setData(pressure);
            engineRoomTemp.setData(engineRoom);
            speed.setData(windSpeed);
            angle.setData(windAngle);
            ArrayList<NmeaSensor> list=new ArrayList<>();
            list.add(hum);
            list.add(outsideTemp);
            list.add(press);
            list.add(engineRoomTemp);
            list.add(speed);
            list.add(angle);
            Log.v(LOG,"windSpeed:"+list.get(4).getData());
            EventBus.getDefault().post(new ServiceResult(list));
            readLine++;
            handler.postDelayed(this,delay);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        dbHandler.close();
        handler.removeCallbacks(this);
    }
}
