package com.aida.nmeasensors.JsonObjects;

/**
 * Created by AIssayeva on 8/15/16.
 */
public class NmeaSensor {
    private String name,alternateName;
    private double data;
    private String metric;
   private int position;
    private int pageNumber;
    private Main main;
    private int id;
    private String triggerType;
    private String triggerValue;
    private boolean isActive,isNotif,isTRansmitted;

private boolean isDisplayed;
    public NmeaSensor(){

    }


    public NmeaSensor(int id, String name, int pageNumber, int position, double data, boolean isDisplayed, String unit, String triggerValue,String triggerType, boolean isActive, boolean isNotif, boolean isTRansmitted, String alternateName){
        this.id=id;
        this.name=name;
        this.data=data;
        this.position=position;
        this.pageNumber=pageNumber;
        this.isDisplayed=isDisplayed;
        this.metric=unit;
        this.triggerValue = triggerValue;
        this.triggerType=triggerType;
        this.isActive=isActive;
        this.isNotif=isNotif;
        this.isTRansmitted=isTRansmitted;
        this.alternateName=alternateName;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public boolean getIsTRansmitted() {
        return isTRansmitted;
    }

    public void setTRansmitted(boolean TRansmitted) {
        this.isTRansmitted = TRansmitted;
    }

    public boolean getisActive() {
        return isActive;
    }

    public boolean getisNotif() {
        return isNotif;
    }

    public void setActive(boolean active) {
       this.isActive = active;
    }

    public void setNotif(boolean notif) {
        this.isNotif = notif;
    }



    public String getTriggerValue() {
        return triggerValue;
    }



    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public boolean getDisplayed(){
        return isDisplayed;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public int getPosition() {
        return position;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public double getData() {
        return data;
    }

    public String getMetric() {

        return metric;
    }
    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getName() {
        return name;
    }

    public void setData(double data) {
        this.data = data;
    }


    public void setPosition(int position) {
        this.position = position;
    }



    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
