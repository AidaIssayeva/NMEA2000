package com.aida.nmeasensors.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aida.nmeasensors.CollectionPagerAdapter;

import com.aida.nmeasensors.R;
import com.aida.nmeasensors.SampleService;
import com.aida.nmeasensors.SensorService;
import com.aida.nmeasensors.ServiceResult;
import com.aida.nmeasensors.fragments.MyOwnSensorList;
import com.aida.nmeasensors.utils.Gauge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by AIssayeva on 8/16/16.
 */
public class ViewPagerActivity extends FragmentActivity implements Runnable  {
    private Context context;

    private Handler handler;
 private ViewPager viewPager;
    private static final String TAG = "ViewPagerActivity";
    private ImageButton btnNext, btnDelete;
    private ImageView[] dots;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private CollectionPagerAdapter collectionPagerAdapter;
    private ArrayList<MyOwnSensorList> list=new ArrayList<>();
    private static String PAGE_NUMBER="pageNumber";
    public static int positionForNewAdded;
    private SharedPreferences sharedPreferences;
    public final static int REQUEST_CODE = 65635;
    int pages;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_ui_pager_layout);
        context=this;
        handler=new Handler();
        handler.post(this);
        checkDrawOverlayPermission();
        getApplicationContext().startService(new Intent(this,SensorService.class));
        //getApplicationContext().startService(new Intent(this,SampleService.class));
         viewPager=(ViewPager)findViewById(R.id.pager);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
          collectionPagerAdapter=new CollectionPagerAdapter(getSupportFragmentManager(),list);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnDelete = (ImageButton)findViewById(R.id.btn_delete);
        sharedPreferences=this.getPreferences(Context.MODE_PRIVATE);
         pages=sharedPreferences.getInt(PAGE_NUMBER,0);
        Log.v("viewpager","pages from shred:"+pages);
        if(sharedPreferences!=null){

            if(pages==0){
                list.add(new MyOwnSensorList(0));
            }else{
                for(int i=0;i<pages;i++){
                    list.add(new MyOwnSensorList(i));
                }


            }


        }


        viewPager.setAdapter(collectionPagerAdapter);
        if((collectionPagerAdapter.getCount()-1)==pages){
            btnNext.setVisibility(View.VISIBLE);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFragment();

                Log.v("click","adding new fragment");
            }
        });


        setUiPageViewController();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                positionForNewAdded=position;

                collectionPagerAdapter.notifyDataSetChanged();
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));


                Toast.makeText(getApplicationContext(),"Chosen List:"+position,Toast.LENGTH_LONG).show();
                Log.v("viewpager","collectionPager count:"+collectionPagerAdapter.getCount());
                Log.v("viewpager","position"+position);
                if(position==(collectionPagerAdapter.getCount()-1)){
                    btnNext.setVisibility(View.VISIBLE);
                }else{
                    btnNext.setVisibility(View.INVISIBLE);
                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {



            }
        });


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted

                }
            }
        }

    }
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.getPackageName()));
                /** request permission via start activity for result */
                this.startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }



    private void addNewFragment(){
        list.add(new MyOwnSensorList(viewPager.getCurrentItem()+1));
        collectionPagerAdapter.notifyChangeInPosition(viewPager.getCurrentItem()+1);
        collectionPagerAdapter.notifyDataSetChanged();

        for(ImageView c:dots){
            pager_indicator.removeView(c);


        }
        btnNext.setVisibility(View.INVISIBLE);
       setUiPageViewController();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PAGE_NUMBER, list.size());

        editor.commit();
    }
    private void setUiPageViewController() {
        collectionPagerAdapter.notifyDataSetChanged();
        dotsCount = collectionPagerAdapter.getCount();
        Log.v(TAG,"size of adapter:"+dotsCount);
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);


        }

       dots[positionForNewAdded].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        if(viewPager.getCurrentItem()==0){
            super.onBackPressed();
        }else{
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if(collectionPagerAdapter !=null){
            collectionPagerAdapter.notifyDataSetChanged();
           // collectionPagerAdapter.update(list);
        }


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServiceResult serviceResult){


    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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

    /**
     * checking every 5 minutes if wifi is on and it's connected to ikommunicate(1-015)
     */
    @Override
    public void run() {

        WifiManager wifiOn = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiOn.getConnectionInfo();
        String desiredSsid="iKConnect";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + desiredSsid + "\"";
        //check if wifi is on,if not-notify user and redirect him to Settings
        if (wifiOn.isWifiEnabled()){
            String connectedSsid = wifiInfo.getSSID();
            Log.v("pager","wifi is connected to:"+connectedSsid);

            if(!connectedSsid.equals(conf.SSID)){
                toSettingsDialog("Tablet is not connected to ikconnect.Go to Settings to connect");
            }else{
                Toast.makeText(this,"Connected to ikconnect",Toast.LENGTH_LONG).show();
            }


        }else{
            toSettingsDialog("Wi-fi is Off. Go to Settings to turn it on");

        }
        handler.postDelayed(this,300000);




    }
    private void toSettingsDialog(String title){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // builder.setTitle("Delete");
        builder.setMessage(title);
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
