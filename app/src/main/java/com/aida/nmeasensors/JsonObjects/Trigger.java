package com.aida.nmeasensors.JsonObjects;

/**
 * Created by AIssayeva on 9/29/16.
 */

public class Trigger {
    private String triggertype;
    private double triggerValue;
    private int sensorId;
    private String unit;
    private int id;
    private boolean notifying;
 public Trigger(){}

    public Trigger(int id,String triggertype,double triggerValue,String unit,int sensorId,boolean notifying){
        this.id=id;
        this.triggertype=triggertype;
        this.triggerValue=triggerValue;
        this.unit=unit;
        this.sensorId=sensorId;
        this.notifying=notifying;

    }

    public void setNotifying(boolean notifying) {
        this.notifying = notifying;
    }
    public boolean getNotifying(){
        return notifying;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setSensorId(int id) {
        this.sensorId = id;
    }

    public void setTriggertype(String triggertype) {
        this.triggertype = triggertype;
    }

    public void setTriggerValue(double triggerValue) {
        this.triggerValue = triggerValue;
    }

    public int getSensorId() {
        return sensorId;
    }

    public double getTriggerValue() {
        return triggerValue;
    }

    public String getTriggertype() {
        return triggertype;
    }
}
