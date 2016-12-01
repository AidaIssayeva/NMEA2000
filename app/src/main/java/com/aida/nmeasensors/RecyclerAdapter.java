package com.aida.nmeasensors;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.JsonObjects.Trigger;
import com.aida.nmeasensors.activities.ChooseSensorActivity;
import com.aida.nmeasensors.adapters.TriggerListViewAdapter;
import com.aida.nmeasensors.fragments.MyOwnSensorList;
import com.aida.nmeasensors.utils.Constants;
import com.cardiomood.android.controls.gauge.SpeedometerGauge;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 8/8/16.
 * TODO: add library back in gradle
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<NmeaSensor> list;
    private Context context;
    private SharedPreferences sharedPref;
   private DBHandler db;
    private DBAlertHandler dbAlertHandler;
    private TriggerListViewAdapter triggerListViewAdapter;
    private ListView triggerSettings;


    public RecyclerAdapter(Context context, ArrayList<NmeaSensor> list,DBHandler db,DBAlertHandler dbAlertHandler){
        this.context=context;
        this.list=list;
        this.db=db;
        this.dbAlertHandler=dbAlertHandler;

    }
    public void updateList(ArrayList<NmeaSensor> mlist) {

        list = mlist;
        notifyDataSetChanged();
    }
    public void addItem(int position,NmeaSensor mlist){
        list.add(position,mlist);
        notifyItemInserted(position);
    }
    public void removeItem(int position){
        NmeaSensor nmeaSensor=list.get(position);
        list.remove(position);
        db.deleteSensor(nmeaSensor);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,list.size());

            ArrayList<Trigger> triggers=dbAlertHandler.getAllTriggers();
            for(int i=0;i<triggers.size();i++){
                Trigger trigger=triggers.get(i);
                if(trigger.getSensorId()==list.get(position).getId()){
                    dbAlertHandler.deleteTrigger(trigger);

                }
            }


        addItem(position,new NmeaSensor());
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
            ViewHolder vh=new ViewHolder(v);
            return vh;
    }
private void setUIforAlert(int position,ViewHolder holder){
    NmeaSensor ownSensor=list.get(position);


    if(ownSensor.getIsTRansmitted()){
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)holder.satelite.getLayoutParams();
        params.setMarginEnd(30);
        holder.satelite.setLayoutParams(params);
    }

    holder.relativeLayout.setBackgroundColor(Color.RED);
}
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(list.get(position).getDisplayed()){
            holder.add.setVisibility(View.GONE);
            holder.metric.setVisibility(View.VISIBLE);
            holder.data.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.VISIBLE);
            holder.cardView.setAlpha((float) 1.0);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRadioButtonDialog(position);
                }
            });

                NmeaSensor ownSensor=list.get(position);
            if(ownSensor.getIsTRansmitted()){
                holder.satelite.setVisibility(View.VISIBLE);

            }else{
                holder.satelite.setVisibility(View.INVISIBLE);
            }
            if(ownSensor.getisNotif()){
                RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)holder.satelite.getLayoutParams();
                params.setMarginEnd(30);
                holder.satelite.setLayoutParams(params);
                holder.alert.setVisibility(View.VISIBLE);
            }else{
                holder.alert.setVisibility(View.GONE);
            }
          if(ownSensor.getisActive()){
              setUIforAlert(position,holder);

            }else{
                ownSensor.setActive(false);
                db.updateSensor(ownSensor);
            }
            if (list.get(position).getName().equals("Apparent wind speed")) {
                setVisibilityForGauges(holder);
                setSpeedGaugeSpeedometer(list.get(position).getData(), holder.gauge,list.get(position).getMetric());
            }else if(list.get(position).getName().equals("Barometric Pressure")){
                setVisibilityForGauges(holder);
                setPressureGaugeSpeedometer(list.get(position).getData(), holder.gauge, list.get(position).getMetric());

            }else if(list.get(position).getName().equals("Apparent wind angle")){
                if(list.get(position).getMetric().equals(Constants.RADIAN)) {
                    setVisibilityForGauges(holder);
                    setWindAngleGaugeSpeedometer(list.get(position).getData(), holder.gauge);
                }else{
                    holder.compass.setVisibility(View.VISIBLE);
                    holder.needle.setVisibility(View.VISIBLE);
                    holder.metric.setVisibility(View.GONE);
                    holder.data.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);
                    showCompass(list.get(position).getData(),holder.needle);
                }
            }else{
                holder.gauge.setVisibility(View.GONE);
            }
        }else{
            holder.add.setVisibility(View.VISIBLE);
            holder.cardView.setAlpha((float) 0.7);
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseSensorActivity.class);
                intent.putExtra(MyOwnSensorList.ARG_OBJECT,MyOwnSensorList.sendingPage);
                intent.putExtra("position",position);
                context.startActivity(intent);
                holder.add.setVisibility(View.GONE);

            }
        });

            holder.metric.setText(list.get(position).getMetric());

            holder.data.setText(""+list.get(position).getData());
            if(list.get(position).getAlternateName()!=null){
                holder.name.setText(list.get(position).getAlternateName());
            }else{
                holder.name.setText(list.get(position).getName());
            }



    }
    private void setVisibilityForGauges(ViewHolder holder){
        holder.gauge.setVisibility(View.VISIBLE);
        holder.data.setVisibility(View.GONE);
        holder.metric.setVisibility(View.GONE);
    }


    private void showDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
       // builder.setTitle("Delete");
        builder.setMessage("Delete this sensor?");
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position);

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

    private  void setSpeedGaugeSpeedometer(double data, final SpeedometerGauge speedometerGauge,String unit){
        if(unit.equals(Constants.KNOTS)){
            speedometerGauge.setMaxSpeed(25);
            speedometerGauge.setMajorTickStep(5);
            speedometerGauge.setMinorTicks(1);
            speedometerGauge.addColoredRange(0, 9, Color.BLUE);
            speedometerGauge.addColoredRange(9, 18, Color.GREEN);
            speedometerGauge.addColoredRange(18, 25, Color.RED);

        } else if(unit.equals(Constants.MPH)){
            speedometerGauge.setMaxSpeed(25);
            speedometerGauge.setMajorTickStep(5);
            speedometerGauge.setMinorTicks(1);
            speedometerGauge.addColoredRange(0, 9, Color.BLUE);
            speedometerGauge.addColoredRange(9, 18, Color.GREEN);
            speedometerGauge.addColoredRange(18, 25, Color.RED);

        }else if(unit.equals(Constants.MPS)){
            speedometerGauge.setMaxSpeed(10);
            speedometerGauge.setMajorTickStep(1);
            speedometerGauge.setMinorTicks(1);
            speedometerGauge.addColoredRange(0, 3, Color.BLUE);
            speedometerGauge.addColoredRange(3, 7, Color.GREEN);
            speedometerGauge.addColoredRange(7, 10, Color.RED);

        }

        speedometerGauge.setLabelTextSize(10);
        speedometerGauge.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        speedometerGauge.setSpeed(data);


    }


    private  void setWindAngleGaugeSpeedometer(double data, final SpeedometerGauge speedometerGauge){
             speedometerGauge.setMaxSpeed(10);
             speedometerGauge.setMajorTickStep(1);
             speedometerGauge.setMinorTicks(1);
             speedometerGauge.addColoredRange(0, 2, Color.GREEN);
             speedometerGauge.addColoredRange(2, 5, Color.YELLOW);
             speedometerGauge.addColoredRange(5, 7, Color.RED);
             speedometerGauge.addColoredRange(7, 10, Color.BLUE);

        speedometerGauge.setLabelTextSize(10);
        speedometerGauge.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        speedometerGauge.setSpeed(data);


    }
    private void setPressureGaugeSpeedometer(double data, final SpeedometerGauge speedometerGauge, String unit){
        if(unit.equals(Constants.PSI)){
            speedometerGauge.setMaxSpeed(50);
            speedometerGauge.setMajorTickStep(5);
            speedometerGauge.setMinorTicks(1);
            speedometerGauge.addColoredRange(0, 15, Color.BLUE);
            speedometerGauge.addColoredRange(15, 35, Color.GREEN);
            speedometerGauge.addColoredRange(35, 50, Color.RED);

        } else if(unit.equals(Constants.KPA)){
            speedometerGauge.setMaxSpeed(300);
            speedometerGauge.setMajorTickStep(50);
            speedometerGauge.setMinorTicks(1);
            speedometerGauge.addColoredRange(0, 100, Color.BLUE);
            speedometerGauge.addColoredRange(100, 200, Color.GREEN);
            speedometerGauge.addColoredRange(200, 300, Color.RED);

        }else if(unit.equals(Constants.INHG)){
            speedometerGauge.setMaxSpeed(90);
            speedometerGauge.setMajorTickStep(10);
            speedometerGauge.setMinorTicks(5);
            speedometerGauge.addColoredRange(0, 30, Color.BLUE);
            speedometerGauge.addColoredRange(30, 60, Color.GREEN);
            speedometerGauge.addColoredRange(60, 90, Color.RED);

        }

        speedometerGauge.setLabelTextSize(10);
        speedometerGauge.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
                    speedometerGauge.setSpeed(data);

    }
    private void showCompass(double data, ImageView image){

        MyOwnSensorList.datalist[1]=(float) data;
        Log.v("recycler","in showCompass daralist[1]="+MyOwnSensorList.datalist[1]);
        Log.v("recycler","in showCompass before applying current position daralist[0]="+MyOwnSensorList.datalist[0]);
        RotateAnimation ra = new RotateAnimation(
                MyOwnSensorList.datalist[0],
                MyOwnSensorList.datalist[1],
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        MyOwnSensorList.datalist[0]=MyOwnSensorList.datalist[1];
        Log.v("recycler","in showCompass datalist[0]="+MyOwnSensorList.datalist[0]);
        ra.setDuration(700);

        ra.setFillAfter(true);

        image.startAnimation(ra);

    }
    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 1;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private void setRadioGroup(RadioButton rb1, RadioButton rb2, RadioButton rb3, RadioGroup rg, String rb1Data, String rb2Data, String rb3Data, final String fromSharedPref,final int position){
        Log.v("recycler","setRadioGroup");
        rb1.setTextSize(25);
        rb2.setTextSize(25);
        rb3.setTextSize(25);
        rb1.setText(rb1Data);
        rb2.setText(rb2Data);
        rb3.setText(rb3Data);
        sharedPref =context.getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        if(sharedPref !=null){
            int intTemp=sharedPref.getInt(fromSharedPref,-1);
            Log.v("recycler","sharedPred is not null,int temp:"+intTemp);

            int childCount=rg.getChildCount();
            for(int i=0;i<childCount;i++){
                RadioButton rbuttom=(RadioButton) rg.getChildAt(i);
                Log.v("recycler","radiobutton:"+rbuttom.isChecked());
                if(i==intTemp){
                    rbuttom.setChecked(true);
                }

            }

        }


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                Log.v("recycler","onchecked");

                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);

                    if (btn.isChecked()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(fromSharedPref, x);
                        Log.v("recycler","adding wind to sharedPref:"+x);
                        editor.commit();
                        EventBus.getDefault().post(new SubscriberSend(x));
                    }

                }


            }
        });

    }



    private void showRadioButtonDialog(final int position) {

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.radiobutton_diaog);
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        TextView title=(TextView) dialog.findViewById(R.id.titlefordialog);

        sharedPref =context.getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        if(list.get(position).getAlternateName()!=null){
            title.setText("Settings for "+list.get(position).getAlternateName());
        }else{
            title.setText("Settings for "+list.get(position).getName());
        }

        RadioButton  rb1=(RadioButton) rg.findViewById(R.id.button_one);
        RadioButton  rb2=(RadioButton) rg.findViewById(R.id.button_two);
        RadioButton  rb3=(RadioButton) rg.findViewById(R.id.button_three);
        rb1.setTextSize(25);
        if (list.get(position).getName().equals("Apparent wind speed")) {
            setRadioGroup(rb1,rb2,rb3,rg,Constants.KNOTS,Constants.MPH,Constants.MPS,Constants.SHARED_PREF_WIND,position);

        }else if(list.get(position).getName().equals("Outside Temperature") ||list.get(position).getName().equals("Engine Room Temperature")) {

            setRadioGroup(rb1,rb2,rb3,rg,Constants.CELSIUS,Constants.FARENHEIT,Constants.KELVIN,Constants.SHARED_PREF_TEMP,position);



        }else if(list.get(position).getName().equals("Apparent wind angle")){
            setRadioGroup(rb1,rb2,rb3,rg,Constants.RADIAN,Constants.DEGREE,"",Constants.SHARED_PREF_ANGLE,position);

            rb3.setVisibility(View.GONE);

        } else if(list.get(position).getName().equals("Barometric Pressure")){
            setRadioGroup(rb1,rb2,rb3,rg,Constants.PSI,Constants.KPA,Constants.INHG,Constants.SHARED_PREF_PRES,position);

        }else{
            rb1.setText(Constants.PERCENT);
            rb1.setChecked(true);
            rb2.setVisibility(View.GONE);
            rb3.setVisibility(View.GONE);
        }


        final CheckBox enableAlert=(CheckBox)dialog.findViewById(R.id.enable_alert);
        final RelativeLayout alertLayout=(RelativeLayout) dialog.findViewById(R.id.alertz);

        final RelativeLayout satellite=(RelativeLayout)dialog.findViewById(R.id.satellite);
        ImageView delete=(ImageView)dialog.findViewById(R.id.deleteIcon);


        if(list.get(position).getisNotif()) {
            enableAlert.setChecked(true);
            alertLayout.setVisibility(View.VISIBLE);


        }

        enableAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    alertLayout.setVisibility(View.VISIBLE);
                   for(int k=0;k<dbAlertHandler.getAllTriggers().size();k++){
                       Trigger trigger=dbAlertHandler.getAllTriggers().get(k);
                       if(list.get(position).getId()==trigger.getId()){
                           trigger.setNotifying(true);
                           dbAlertHandler.updateTrigger(trigger);
                       }

                   }
                   list.get(position).setNotif(true);
                    alertLayout.setVisibility(View.VISIBLE);

                }else{

                    list.get(position).setNotif(false);
                    alertLayout.setVisibility(View.GONE);
                }
                db.updateSensor(list.get(position));

            }
        });


        final CheckBox checkBox=(CheckBox) dialog.findViewById(R.id.checkbox);
        final Spinner frequency=(Spinner)dialog.findViewById(R.id.frequency);
        final TextView freqName=(TextView) dialog.findViewById(R.id.nameOfFreq);
        final CheckBox alertTransmit=(CheckBox) dialog.findViewById(R.id.alertTransmit);

        if(list.get(position).getIsTRansmitted()){
            checkBox.setChecked(true);
            freqName.setVisibility(View.VISIBLE);
            frequency.setVisibility(View.VISIBLE);
            alertTransmit.setVisibility(View.VISIBLE);

        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.transmit_frequency, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        frequency.setAdapter(adapter);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    freqName.setVisibility(View.VISIBLE);
                    frequency.setVisibility(View.VISIBLE);
                    alertTransmit.setVisibility(View.VISIBLE);
                    list.get(position).setTRansmitted(true);

                }else {
                    freqName.setVisibility(View.GONE);
                    frequency.setVisibility(View.GONE);
                    alertTransmit.setVisibility(View.GONE);
                    list.get(position).setTRansmitted(false);


                }
                db.updateSensor(list.get(position));

            }
        });
        //TODO:set frequency to send data thru transmission
        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
                dialog.dismiss();
            }
        });
        final  EditText alternateNameView=(EditText) dialog.findViewById(R.id.changeName);

        if(db.getNmeaSensor(list.get(position).getId()).getAlternateName()!=null){
            alternateNameView.setText(db.getNmeaSensor(list.get(position).getId()).getAlternateName());
        }else{
            alternateNameView.setText(db.getNmeaSensor(list.get(position).getId()).getName());
        }


        ImageButton change=(ImageButton)dialog.findViewById(R.id.clickToEdit) ;

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alternateNameView.setFocusableInTouchMode(true);
                alternateNameView.setInputType(InputType.TYPE_CLASS_TEXT);
                alternateNameView.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(alternateNameView, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alternateNameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alternateNameView.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(alternateNameView, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {


                list.get(position).setAlternateName(alternateNameView.getText().toString());

                db.updateSensor(list.get(position));
            }
        });

         triggerSettings=(ListView) alertLayout.findViewById(R.id.triggersavedSettings);
        //
        ArrayList<Trigger> trigerLIst=dbAlertHandler.getAllTriggers();
        ArrayList<Trigger> toAdapter=new ArrayList<>();
        int index=0;
        for(int k=0;k<trigerLIst.size();k++){
            if(list.get(position).getId()==trigerLIst.get(k).getSensorId()){
                toAdapter.add(index,trigerLIst.get(k));
                index++;
            }
        }
          triggerListViewAdapter=new TriggerListViewAdapter(context,toAdapter,list.get(position),dbAlertHandler,db);
        triggerSettings.setVisibility(View.VISIBLE);
        triggerSettings.setAdapter(triggerListViewAdapter);



        final Button addTRigger=(Button) alertLayout.findViewById(R.id.addTrigger);
        addTRigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTriggerItems(alertLayout,addTRigger,position);

            }
        });

        //sending event when user chahges units,so the view can be updated
        EventBus.getDefault().post(new SubscriberSend(position));

        dialog.show();

    }
    private void addTriggerItems(RelativeLayout layout, Button button, final int posList){
        layout.removeView(button);

        final LinearLayout thisLayout=new LinearLayout(context);
        thisLayout.setOrientation(LinearLayout.HORIZONTAL);
        thisLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        p.addRule(RelativeLayout.BELOW, R.id.triggersavedSettings);

        thisLayout.setLayoutParams(p);
        final Spinner triggerType=new Spinner(context);
        final EditText addValue=new EditText(context);
        addValue.setHint("Value");
        addValue.setInputType(InputType.TYPE_CLASS_NUMBER);

        final ArrayAdapter<CharSequence> triggeradapter = ArrayAdapter.createFromResource(context,
                R.array.trigger_type, android.R.layout.simple_spinner_item);

        triggeradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        triggerType.setAdapter(triggeradapter);

        LinearLayout.LayoutParams spinner=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        thisLayout.addView(triggerType,spinner);
        thisLayout.addView(addValue,spinner);
        final Trigger trigger=new Trigger();
        trigger.setSensorId(list.get(posList).getId());
        trigger.setUnit(list.get(posList).getMetric());
        triggerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trigger.setTriggertype(""+triggeradapter.getItem(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addValue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_ENTER:
                            Double value= Double.parseDouble(addValue.getText().toString());
                            trigger.setTriggerValue(value);
                            trigger.setNotifying(true);
                            addValue.clearFocus();
                            dbAlertHandler.addTrigger(trigger);
                            thisLayout.removeView(addValue);
                            thisLayout.removeView(triggerType);


                            //updating listview of triggers
                            ArrayList<Trigger> trigerLIst=dbAlertHandler.getAllTriggers();
                            ArrayList<Trigger> toAdapter=new ArrayList<>();
                            int index=0;
                            for(int k=0;k<trigerLIst.size();k++){
                                if(list.get(posList).getId()==trigerLIst.get(k).getSensorId()){
                                    toAdapter.add(index,trigerLIst.get(k));
                                    index++;
                                }
                            }
                            triggerListViewAdapter.clear();
                            triggerListViewAdapter.addAll(toAdapter);


                            return true;
                        default:break;
                    }
                }
                return false;
            }
        });



        RelativeLayout.LayoutParams buttonParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.BELOW,  thisLayout.getId());
        button.setLayoutParams(buttonParams);

        layout.addView(thisLayout);
        layout.addView(button);



    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView metric;
        protected ImageButton add;
        protected TextView name;
        protected TextView data;
        protected SpeedometerGauge gauge;
        protected Context context;
        protected ImageView compass;
        protected ImageView needle;
        protected RelativeLayout relativeLayout;
        protected CardView cardView;
        protected ImageView alert;
        protected ImageView satelite;
        protected ViewHolder(View v) {
            super(v);

            add=(ImageButton) v.findViewById(R.id.addToThePage);
            name=(TextView) v.findViewById(R.id.sensor_name);
            data=(TextView) v.findViewById(R.id.sensor_data);
            metric=(TextView) v.findViewById(R.id.sensor_metrics);
            relativeLayout=(RelativeLayout)v.findViewById(R.id.cardview_layout);
            gauge=(SpeedometerGauge)v.findViewById(R.id.adapterGauge);
            cardView=(CardView)v.findViewById(R.id.card_view);
            compass=(ImageView)v.findViewById(R.id.angle);
            needle=(ImageView)v.findViewById(R.id.needle);
            alert =(ImageView)v.findViewById(R.id.alert);
            satelite=(ImageView)v.findViewById(R.id.transmission);




        }


    }

}

