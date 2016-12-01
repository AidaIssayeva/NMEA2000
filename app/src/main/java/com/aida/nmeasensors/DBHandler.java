package com.aida.nmeasensors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aida.nmeasensors.JsonObjects.NmeaSensor;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by AIssayeva on 8/25/16.
 */
public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NmeaSensorInfo";
    // Contacts table name
    private static final String TABLE_SENSOR = "NmeaSensor";
    // Shops Table Columns names
     private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PAGE_NUMBER = "page_number";
    private static final String KEY_POSTION = "position";
    private static final String KEY_VALUE = "value";
    private static final String KEY_IS_DISPLAYED = "is_displayed";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_MINIMUM="trigger_value";
    private static final String KEY_TRIGGER_TYPE ="trigger_type";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_IS_NOTIFIED="is_receiving_alerts";
    private static final String KEY_IS_TRANSMITTED="is_transmitted";
    private static final String KEY_ALTERNATE_NAME="alternate_name";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SENSOR + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + "  TEXT,"
                + KEY_PAGE_NUMBER + "  INTEGER, " + KEY_POSTION + " INTEGER, " + KEY_VALUE + " REAL, "
                + KEY_IS_DISPLAYED + " BOOLEAN, " + KEY_UNIT+" TEXT, "+KEY_MINIMUM + "  TEXT, "+ KEY_TRIGGER_TYPE + "  TEXT, "
                +KEY_IS_ACTIVE + " BOOLEAN, "+ KEY_IS_NOTIFIED + " BOOLEAN, "+KEY_IS_TRANSMITTED + " BOOLEAN, "+KEY_ALTERNATE_NAME+" TEXT "+" )";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        onCreate(db);

    }

    public void addNmeaSensor(NmeaSensor nmeaSensor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, nmeaSensor.getName());
        values.put(KEY_PAGE_NUMBER, nmeaSensor.getPageNumber());
        values.put(KEY_POSTION, nmeaSensor.getPosition());
        values.put(KEY_VALUE,nmeaSensor.getData());
        values.put(KEY_IS_DISPLAYED, nmeaSensor.getDisplayed());
        values.put(KEY_UNIT,nmeaSensor.getMetric());
        values.put(KEY_MINIMUM,nmeaSensor.getTriggerValue());
        values.put(KEY_TRIGGER_TYPE,nmeaSensor.getTriggerType());
        values.put(KEY_IS_ACTIVE, nmeaSensor.getisActive());
        values.put(KEY_IS_NOTIFIED,nmeaSensor.getisNotif());
        values.put(KEY_IS_TRANSMITTED,nmeaSensor.getIsTRansmitted());
        values.put(KEY_ALTERNATE_NAME,nmeaSensor.getAlternateName());
        Log.v("db","isDisplayed:"+nmeaSensor.getDisplayed());


// Inserting Row
        db.insert(TABLE_SENSOR, null, values);
        db.close(); // Closing database connection

    }

        public NmeaSensor getNmeaSensor(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SENSOR, new String[]{ KEY_ID,
                KEY_NAME, KEY_PAGE_NUMBER,KEY_POSTION,KEY_VALUE,KEY_IS_DISPLAYED,KEY_UNIT,KEY_MINIMUM, KEY_TRIGGER_TYPE,KEY_IS_ACTIVE,KEY_IS_NOTIFIED,KEY_IS_TRANSMITTED,KEY_ALTERNATE_NAME}, KEY_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        NmeaSensor contact = new NmeaSensor(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),
                Double.parseDouble(cursor.getString(4)),Boolean.parseBoolean(cursor.getString(5)),cursor.getString(6),
                cursor.getString(7),cursor.getString(8),Boolean.parseBoolean(cursor.getString(9)),
                Boolean.parseBoolean(cursor.getString(10)),Boolean.parseBoolean(cursor.getString(11)),cursor.getString(12));


            cursor.close();
// return
        return contact;

    }
    public ArrayList<NmeaSensor> getAllSensors() {
        ArrayList<NmeaSensor> shopList = new ArrayList<NmeaSensor>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_SENSOR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NmeaSensor nmeaSensor = new NmeaSensor();
                nmeaSensor.setId(Integer.parseInt(cursor.getString(0)));
                nmeaSensor.setName(cursor.getString(1));
                nmeaSensor.setPageNumber(Integer.parseInt(cursor.getString(2)));
                nmeaSensor.setPosition(Integer.parseInt(cursor.getString(3)));
                nmeaSensor.setData(Double.parseDouble(cursor.getString(4)));

                if (cursor.isNull(5) || cursor.getShort(5) == 0) {
                   nmeaSensor.setDisplayed(false);
                } else {
                    nmeaSensor.setDisplayed(true);
                }
                nmeaSensor.setMetric(cursor.getString(6));

                nmeaSensor.setTriggerValue(cursor.getString(7));
                nmeaSensor.setTriggerType(cursor.getString(8));
                if (cursor.isNull(9) || cursor.getShort(9) == 0) {
                    nmeaSensor.setActive(false);
                } else {
                    nmeaSensor.setActive(true);
                }


                if (cursor.isNull(10) || cursor.getShort(10) == 0) {
                    nmeaSensor.setNotif(false);
                } else {
                    nmeaSensor.setNotif(true);
                }
                if (cursor.isNull(11) || cursor.getShort(11) == 0) {
                    nmeaSensor.setTRansmitted(false);
                } else {
                    nmeaSensor.setTRansmitted(true);
                }
                nmeaSensor.setAlternateName(cursor.getString(12));

                shopList.add(nmeaSensor);
            } while (cursor.moveToNext());
        }
        db.close();
        return shopList;

    }
public boolean contains(String sensorName){
    ArrayList<NmeaSensor> list=getAllSensors();
   for(int i=0;i<list.size();i++) {
       String name = list.get(i).getName();
       if (name.equals(sensorName)) return true;
   }
    return false;





}
    public int getSensorsCount() {
        String countQuery = "SELECT * FROM " + TABLE_SENSOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }

    public int updateSensor(NmeaSensor shop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, shop.getName());
        values.put(KEY_PAGE_NUMBER, shop.getPageNumber());
        values.put(KEY_POSTION, shop.getPosition());
        values.put(KEY_VALUE, shop.getData());
        values.put(KEY_IS_DISPLAYED,shop.getDisplayed());
        values.put(KEY_UNIT,shop.getMetric());
        values.put(KEY_MINIMUM,shop.getTriggerValue());
        values.put(KEY_TRIGGER_TYPE,shop.getTriggerType());
        values.put(KEY_IS_ACTIVE,shop.getisActive());
        values.put(KEY_IS_NOTIFIED,shop.getisNotif());
        values.put(KEY_IS_TRANSMITTED,shop.getIsTRansmitted());
        values.put(KEY_ALTERNATE_NAME,shop.getAlternateName());

// updating row
        return db.update(TABLE_SENSOR, values, KEY_ID + "  = ?",
        new String[]{String.valueOf(shop.getId())});

    }

    // Deleting a shop
    public void deleteSensor(NmeaSensor shop) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSOR, KEY_ID + "  = ?",
        new String[] { String.valueOf(shop.getId()) });
        EventBus.getDefault().post(new DBUpdated(true));
        db.close();
    }

}
