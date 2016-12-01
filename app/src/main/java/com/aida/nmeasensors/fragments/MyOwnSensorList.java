package com.aida.nmeasensors.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aida.nmeasensors.DBAlertHandler;
import com.aida.nmeasensors.DBHandler;
import com.aida.nmeasensors.DBUpdated;
import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.R;
import com.aida.nmeasensors.RecyclerAdapter;

import com.aida.nmeasensors.activities.ViewPagerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * Created by AIssayeva on 8/16/16.
 */
public class MyOwnSensorList  extends Fragment implements Runnable  {
    private static final String LOG="MyOwnSensorList";
    private static final String BACKGROUND_COLOR = "backgroundColor";
    public static final String PAGE = "page";
    public static final String ARG_OBJECT = "object";
    private RecyclerAdapter gridAdapter;

    private  ArrayList<NmeaSensor> sensorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ArrayList<NmeaSensor> listsPerFragment=new ArrayList<NmeaSensor>(8);
    private FrameLayout frameLayout;
    private DBHandler db;

    public static int sendingPage;
    public static float [] datalist=new float[2];
    private Handler handler;
    private int delay=5000;
    private DBAlertHandler dbAlertHandler;



    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && isResumed()){
                settingAdapter();
        }
    }
    public MyOwnSensorList(){}


    public static MyOwnSensorList newInstance(int page) {
        MyOwnSensorList f = new MyOwnSensorList(page);
        return f;
    }
    public MyOwnSensorList(int page){

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.new_ui_activity_layout, container, false);




        frameLayout=(FrameLayout)rootView.findViewById(R.id.framlayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        datalist[0]=0;
        db = new DBHandler(getContext());
        dbAlertHandler=new DBAlertHandler(getContext());
        handler=new Handler();
        handler.post(this);

        return rootView;
    }
    @Override
    public void run() {

        sensorList.clear();
        sensorList=db.getAllSensors();

        for(int i=0;i<sensorList.size();i++){
            NmeaSensor nmeaSensor=sensorList.get(i);

            //  Log.v(LOG,"sensor:"+nmeaSensor);
            if(nmeaSensor.getPageNumber()==sendingPage){
                Log.v(LOG,"sending page while applying to fragment:"+sendingPage);
                //   Log.v(LOG,"idDisplayed:"+i+";isIt:"+nmeaSensor.getDisplayed());
                listsPerFragment.set(nmeaSensor.getPosition(),nmeaSensor);


            }
        }

        gridAdapter = new RecyclerAdapter(getContext(), listsPerFragment,db,dbAlertHandler);
        recyclerView.setAdapter(gridAdapter);
        handler.postDelayed(this,delay);
    }




    private void settingAdapter(){
        sendingPage=ViewPagerActivity.positionForNewAdded;
        Log.v(LOG,"sendingpage after swiping:"+sendingPage);
        db = new DBHandler(getContext());

        listsPerFragment=new ArrayList<>();
        if(listsPerFragment.size()<8) {
            //    Log.v(LOG,"list size:"+listsPerFragment);
            int needed=8-listsPerFragment.size();
            //  Log.v(LOG,"less than 8:"+needed);
            for (int i = 0; i <needed ; i++) {
                listsPerFragment.add(i,new NmeaSensor());
            }


        }
        sensorList.clear();
        sensorList=db.getAllSensors();

        for(int i=0;i<sensorList.size();i++){
            NmeaSensor nmeaSensor=sensorList.get(i);

          //  Log.v(LOG,"sensor:"+nmeaSensor);
            if(nmeaSensor.getPageNumber()==sendingPage){
                Log.v(LOG,"sending page while applying to fragment:"+sendingPage);
             //   Log.v(LOG,"idDisplayed:"+i+";isIt:"+nmeaSensor.getDisplayed());
                listsPerFragment.set(nmeaSensor.getPosition(),nmeaSensor);


            }
        }



        gridAdapter = new RecyclerAdapter(getContext(), listsPerFragment,db,dbAlertHandler);
        recyclerView.setAdapter(gridAdapter);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG,"onPause called from page:"+sendingPage);




    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);





    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DBUpdated serviceResult) {
        Log.v(LOG,"receiving event to fragment");
        if(serviceResult.getUpdated()){
           settingAdapter();

        }

    }



    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG,"onResume called from page:"+sendingPage);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        settingAdapter();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        db.close();

    }



}
