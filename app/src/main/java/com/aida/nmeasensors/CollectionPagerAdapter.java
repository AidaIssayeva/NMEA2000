package com.aida.nmeasensors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.aida.nmeasensors.activities.ViewPagerActivity;
import com.aida.nmeasensors.fragments.MyOwnSensorList;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by AIssayeva on 8/17/16.
 */
public class CollectionPagerAdapter extends FragmentPagerAdapter {
    private static final String LOG="CollectionPagerAdapter";
    private ArrayList<MyOwnSensorList> list;
    private long baseId = 0;
    private int index;
    public CollectionPagerAdapter(FragmentManager fm,ArrayList<MyOwnSensorList> list) {
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int i) {
        index=i;
        return MyOwnSensorList.newInstance(i);

    }
    public void update(ArrayList<MyOwnSensorList> list){
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);

           // return POSITION_NONE;


    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return baseId + position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    public void notifyChangeInPosition(int n) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += getCount() + n;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return "List " + (position + 1);
    }
}

