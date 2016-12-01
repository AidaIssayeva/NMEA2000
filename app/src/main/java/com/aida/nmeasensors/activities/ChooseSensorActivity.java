package com.aida.nmeasensors.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aida.nmeasensors.DBHandler;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.R;
import com.aida.nmeasensors.ServiceResult;
import com.aida.nmeasensors.SubscriberSend;
import com.aida.nmeasensors.adapters.ListViewAdapter;
import com.aida.nmeasensors.fragments.MyOwnSensorList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 8/15/16.
 */
public class ChooseSensorActivity extends AppCompatActivity {
    private static final String LOG="ChooseSensorActivity";

    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private int page,position1;
   private ArrayList<NmeaSensor> arrays;
    private DBHandler dbHandler;
    private ArrayList<NmeaSensor> fromDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_sensor_activity);

        listView=(ListView)findViewById(R.id.all_list);

        dbHandler = new DBHandler(this);
        fromDb= dbHandler.getAllSensors();
      page =getIntent().getIntExtra(MyOwnSensorList.ARG_OBJECT,-1);
         position1=getIntent().getIntExtra("position",-1);
        Log.v(LOG,"page number to chooseActivty:"+page);
    EventBus.getDefault().post(new SubscriberSend(page));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NmeaSensor sensor=arrays.get(position);
                Log.v(LOG,"adding sensor to sensorSet:"+sensor.getName());


                Log.v(LOG,"page received:"+page);
                sensor.setPageNumber(page);
                sensor.setPosition(position1);
                sensor.setDisplayed(true);

                if(dbHandler.contains(sensor.getName())){
                    Toast.makeText(getApplicationContext(),"You've already added this sensor",Toast.LENGTH_LONG).show();
                }else {
                    dbHandler.addNmeaSensor(sensor);
                }
                onBackPressed();
                Log.v(LOG,"setDisplayed:"+sensor.getDisplayed());
                listViewAdapter.notifyDataSetChanged();


            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServiceResult serviceResult){

        arrays=new ArrayList<>();
        Log.v(LOG,"receiving event");
        ArrayList<NmeaSensor> allSensors=serviceResult.getResultValue();
        for(int i=0;i<allSensors.size();i++){
            NmeaSensor nmeaSensor=allSensors.get(i);
            arrays.add(nmeaSensor);
        }

        listViewAdapter=new ListViewAdapter(this,arrays,dbHandler,fromDb);
        listView.setAdapter(listViewAdapter);
        Log.v(LOG,"arrayList 0:"+allSensors.get(0).getName());


    }
}
