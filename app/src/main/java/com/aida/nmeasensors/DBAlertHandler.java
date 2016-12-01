package com.aida.nmeasensors;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;
import com.aida.nmeasensors.JsonObjects.Trigger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 9/29/16.
 */

public class DBAlertHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SensorAlerts";
    // Contacts table name
    private static final String TABLE_SENSOR = "alerts";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TRIGGER_TYPE ="trigger_type";
    private static final String KEY_TRIGGER_VALUE ="trigger_value";
    private static final String KEY_UNIT ="unit";
    private static final String KEY_SENSOR_ID ="sensor_id";
    private static final String KEY_NOTIFIYING ="is_notifiying";
    public DBAlertHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SENSOR + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TRIGGER_TYPE+" TEXT,"+KEY_TRIGGER_VALUE + " REAL, "+KEY_UNIT+" TEXT,"+KEY_SENSOR_ID+" TEXT, "+KEY_NOTIFIYING+" BOOLEAN "+" )";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        onCreate(db);

    }

    public void addTrigger(Trigger trigger) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRIGGER_TYPE,trigger.getTriggertype());
        values.put(KEY_TRIGGER_VALUE,trigger.getTriggerValue());
        values.put(KEY_UNIT,trigger.getUnit());
        values.put(KEY_SENSOR_ID,trigger.getSensorId());
        values.put(KEY_NOTIFIYING,trigger.getNotifying());



// Inserting Row
        db.insert(TABLE_SENSOR, null, values);
        db.close(); // Closing database connection

    }

    public Trigger getNmeaSensor(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SENSOR, new String[]{ KEY_ID,
                        KEY_TRIGGER_TYPE,KEY_TRIGGER_VALUE,KEY_UNIT,KEY_SENSOR_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Trigger contact = new Trigger(Integer.parseInt(cursor.getString(0)),cursor.getString(1), Double.parseDouble(cursor.getString(2)),cursor.getString(3), Integer.parseInt(cursor.getString(4)),Boolean.parseBoolean(cursor.getString(5)));


        cursor.close();
// return
        return contact;

    }
    public ArrayList<Trigger> getAllTriggers() {
        ArrayList<Trigger> shopList = new ArrayList<Trigger>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_SENSOR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Trigger nmeaSensor = new Trigger();
                nmeaSensor.setId(Integer.parseInt(cursor.getString(0)));
                nmeaSensor.setTriggertype(cursor.getString(1));
                nmeaSensor.setTriggerValue(Double.parseDouble(cursor.getString(2)));
                nmeaSensor.setUnit(cursor.getString(3));
                nmeaSensor.setSensorId(Integer.parseInt(cursor.getString(4)));
                if (cursor.isNull(5) || cursor.getShort(5) == 0) {
                    nmeaSensor.setNotifying(false);
                } else {
                    nmeaSensor.setNotifying(true);
                }

                shopList.add(nmeaSensor);
            } while (cursor.moveToNext());
        }
        db.close();
        return shopList;

    }
    public boolean contains(int sensorID){
        ArrayList<Trigger> list=getAllTriggers();
        for(int i=0;i<list.size();i++) {
            int id = list.get(i).getSensorId();
            if (id==sensorID) return true;
        }
        return false;





    }
    public int getTriggersCount() {
        String countQuery = "SELECT * FROM " + TABLE_SENSOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }

    public int updateTrigger(Trigger shop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRIGGER_TYPE,shop.getTriggertype());
        values.put(KEY_TRIGGER_VALUE,shop.getTriggerValue());
        values.put(KEY_UNIT,shop.getUnit());
        values.put(KEY_SENSOR_ID,shop.getSensorId());
        values.put(KEY_NOTIFIYING,shop.getNotifying());

// updating row
        return db.update(TABLE_SENSOR, values, KEY_ID + "  = ?",
                new String[]{String.valueOf(shop.getId())});

    }

    // Deleting a shop
    public void deleteTrigger(Trigger shop) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSOR, KEY_ID + "  = ?",
                new String[] { String.valueOf(shop.getId()) });
        EventBus.getDefault().post(new DBUpdated(true));
        db.close();
    }

}
