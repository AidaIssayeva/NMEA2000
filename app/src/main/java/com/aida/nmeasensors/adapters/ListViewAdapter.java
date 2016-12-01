package com.aida.nmeasensors.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aida.nmeasensors.DBHandler;
import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.R;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 9/7/16.
 */
public class ListViewAdapter extends ArrayAdapter {
    private ArrayList<NmeaSensor> list;
    private Context context;
    private DBHandler db;
    private ArrayList<NmeaSensor> fromDb;

    public ListViewAdapter(Context context, ArrayList<NmeaSensor> list,DBHandler db,ArrayList<NmeaSensor> fromDb) {
        super(context,0,list);
        this.context=context;
        this.list=list;
        this.db=db;
        this.fromDb=fromDb;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.choose_adapter, parent, false);
        }
        // Lookup view for data population
        TextView allNames=(TextView)convertView.findViewById(R.id.all_sensors_name);
        ImageView added_already=(ImageView)convertView.findViewById(R.id.already_added);

        // Populate the data into the template view using the data object
        allNames.setText(list.get(position).getName());



//        if(db.contains(list.get(position).getName())){
//            NmeaSensor nmeaSensor=list.get(position);
//
//            added_already.setVisibility(View.VISIBLE);
//          allNames.setText(list.get(position).getName()+"( "+  db.getNmeaSensor(1).getAlternateName()+")");
//
//
//        }
        for(int i =0;i<fromDb.size();i++) {
            if (fromDb.get(i).getName().equals(list.get(position).getName())) {
                list.get(position).setAlternateName(fromDb.get(i).getAlternateName());
                added_already.setVisibility(View.VISIBLE);
                allNames.setText(list.get(position).getName() + " (" + list.get(position).getAlternateName() + ")");
            }
        }




        // Return the completed view to render on screen
        return convertView;
    }

}
