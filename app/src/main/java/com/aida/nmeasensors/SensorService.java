package com.aida.nmeasensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.aida.nmeasensors.DiscoverServicesOnNetwork.NsdHelper;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by AIssayeva on 8/9/16.
 */
public class SensorService extends Service implements Runnable {

    private ReadFileFromHttp readFileFromHttp;
   private Handler handler;
   private int delay=30000;
    private static final String LOG="SENSORSERVICE";
    public static SharedPreferences sharedPreferences;
private final static String HTTP_ADDRESS="http";
    private String address;
    private DBHandler dbHandler;
private DBAlertHandler dbAlertHandler;
    NsdHelper mNsdHelper;


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




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(SubscriberSend serviceResult) {
        Log.v(LOG,"receiving event from fragment");

        address=sharedPreferences.getString(HTTP_ADDRESS,"");
        readFileFromHttp=new ReadFileFromHttp(address);
        readFileFromHttp.execute();


    }

    @Override
    public void run() {
        address=sharedPreferences.getString(HTTP_ADDRESS,"");
        Log.v(LOG,"address running:"+address);
        if(address.equals("")){
            mNsdHelper = new NsdHelper(this);
            mNsdHelper.initializeNsd();
            mNsdHelper.discoverServices();
            mNsdHelper.registerService(8080);
        }

        readFileFromHttp=new ReadFileFromHttp(address);
        readFileFromHttp.execute();

        handler.postDelayed(this,delay);
        Log.v(LOG,"delay running");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        dbHandler.close();
        dbAlertHandler.close();
        handler.removeCallbacks(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }






    private static  class ReadFileFromHttp extends AsyncTask<String, String, String> {
      private   HttpURLConnection urlConnection;
   private String urlString;
      private   SharedPreferences  sharedPreferences;
        private double engineRoom=0.0,outside=0.0,humidity=0.0,pressure=0.0,windSpeed=0.0,windAngle=0.0;
        private OutsideTemp outsideTemp;
        private EngineRoom engineRoomTemp;
        private Outside hum;
        private BarPressure press;
        private SpeedApparent speed;
        private AngleApparent angle;

    public ReadFileFromHttp(String url){
        this.urlString=url;
    }


        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();


                    if (!urlString.equals("")) {

                        try {
                            URL url = new URL(urlString);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                    return result.toString();


        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.v(LOG,"results:"+result);
             sharedPreferences = SensorService.sharedPreferences;
         int   sharedTemp = sharedPreferences.getInt(Constants.SHARED_PREF_TEMP, -1);
            Log.v(LOG,"sharedTemp in Service:"+sharedTemp);
         int   sharedPress = sharedPreferences.getInt(Constants.SHARED_PREF_PRES, -1);
         int   sharedWind=sharedPreferences.getInt(Constants.SHARED_PREF_WIND,-1);
        int    sharedAngle=sharedPreferences.getInt(Constants.SHARED_PREF_ANGLE,-1);
            if(!result.equals("")){
                Log.v(LOG,"result is NOT null");

                Gson gson=new Gson();
                Main main=gson.fromJson(result,Main.class);
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


            }else{
                Log.v(LOG,"result is null");
                ArrayList<NmeaSensor> list=new ArrayList<>();
                EventBus.getDefault().post(new ServiceResult(list));

            }



        }


    }



}
