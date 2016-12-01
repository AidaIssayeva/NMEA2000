package com.aida.nmeasensors.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aida.nmeasensors.DBAlertHandler;
import com.aida.nmeasensors.DBHandler;
import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.JsonObjects.Trigger;
import com.aida.nmeasensors.R;
import com.aida.nmeasensors.utils.Constants;
import com.aida.nmeasensors.utils.ConversionUtils;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 9/28/16.
 */

public class TriggerListViewAdapter extends ArrayAdapter {
    private ArrayList<Trigger> list;
    private Context context;
    private DBAlertHandler db;
    private NmeaSensor nmeaSensor;
    private DBHandler dbHandler;



    public TriggerListViewAdapter(Context context, ArrayList<Trigger> list,NmeaSensor nmeaSensor, DBAlertHandler db,DBHandler dbHandler) {
        super(context,0,list);
        this.context=context;
        this.list=list;
        this.db=db;
        this.nmeaSensor=nmeaSensor;
        this.dbHandler=dbHandler;


    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trigger_settings_item, parent, false);
        }
        // Lookup view for data population
        TextView type=(TextView) convertView.findViewById(R.id.type) ;
        TextView value=(TextView) convertView.findViewById(R.id.valueOftype);
        ImageView delete=(ImageView)convertView.findViewById(R.id.deleteTrigger);

        // Populate the data into the template view using the data object
        if(list.get(position).getSensorId()==nmeaSensor.getId()){
            type.setText(list.get(position).getTriggertype());


            String unitFromTrigger=list.get(position).getUnit();
            String unitFromSensor=nmeaSensor.getMetric();
            double valueFromTRigger=list.get(position).getTriggerValue();
            if(!unitFromSensor.equals(unitFromTrigger)){
                ConversionUtils conversionUtils=new ConversionUtils();
                Trigger trigger=list.get(position);
                double changing=0;

                if(unitFromSensor.equals(Constants.CELSIUS)){
                    changing=Math.round(conversionUtils.convertToCelsius(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.FARENHEIT)){
                    changing=Math.round(conversionUtils.convertToFarenheit(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.KELVIN)){
                    changing=Math.round(conversionUtils.convertToKelvin(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.DEGREE)){
                    changing=Math.round(conversionUtils.convertToDegree(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.RADIAN)){
                    changing=Math.round(conversionUtils.convertToRadian(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.INHG)){
                    changing=Math.round(conversionUtils.convertToinHG(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.KPA)){
                    changing=Math.round(conversionUtils.convertToKPA(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.PSI)){
                    changing=Math.round(conversionUtils.convertToPSI(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.KNOTS)){
                    changing=Math.round(conversionUtils.convertToKnots(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.MPH)){
                    changing=Math.round(conversionUtils.convertToMPH(unitFromTrigger,valueFromTRigger));
                }else if(unitFromSensor.equals(Constants.MPS)){
                    changing=Math.round(conversionUtils.convertToMPS(unitFromTrigger,valueFromTRigger));
                }


                trigger.setTriggerValue(changing);
                trigger.setUnit(unitFromSensor);
                db.updateTrigger(trigger);
                value.setText(changing+trigger.getUnit());
            }else{
                value.setText(list.get(position).getTriggerValue()+list.get(position).getUnit());
            }

        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                removeItem(position);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
    public void removeItem(int position){
        Trigger trigger=list.get(position);
        list.remove(position);
        db.deleteTrigger(trigger);
        nmeaSensor.setActive(false);
        dbHandler.updateSensor(nmeaSensor);


       notifyDataSetChanged();

    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }
}
