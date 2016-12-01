package com.aida.nmeasensors.utils;

/**
 * Created by AIssayeva on 9/29/16.
 */

public class ConversionUtils {

    public double convertToFarenheit(String unit,double value){
        double farenheit=0;
        if(unit.equals(Constants.KELVIN)){
            farenheit= ((value * 9 / 5) - 459.67);
        }else{
            farenheit=((value*9/5)+32);
        }

        return farenheit;

    }
    public double convertToCelsius(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.KELVIN)){
            celsuis= (value  - 273.15);
        }else{
            celsuis=((value-32)*5/9);
        }

        return celsuis;

    }
    public double convertToKelvin(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.CELSIUS)){
            celsuis= (value  + 273.15);
        }else{
            celsuis=((value+459.67)*5/9);
        }

        return celsuis;

    }
    public double convertToPSI(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.KPA)){
            celsuis= (value *0.145038);
        }else{
            celsuis=(value*0.491154);
        }

        return celsuis;

    }
    public double convertToinHG(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.KPA)){
            celsuis= (value*0.2953);
        }else{
            celsuis=(value*2.03602);
        }

        return celsuis;

    }
    public double convertToKPA(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.PSI)){
            celsuis= (value*6.89475 );
        }else{
            celsuis=(value*3.38639);
        }

        return celsuis;

    }
    public double convertToKnots(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.MPS)){
            celsuis= (value*1.94384 );
        }else{
            celsuis=(value*0.868976);
        }
        return celsuis;

    }
    public double convertToMPH(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.MPS)){
            celsuis= (value*2.23694 );
        }else{
            celsuis=(value*1.15078);
        }
        return celsuis;

    }
    public double convertToMPS(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.MPH)){
            celsuis= (value*0.44704 );
        }else{
            celsuis=(value*0.514444);
        }
        return celsuis;

    }
    public double convertToDegree(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.RADIAN)){
            celsuis= (value* 57.2958);
            if(celsuis>360){
                celsuis=celsuis-360;
            }
        }
        return celsuis;

    }
    public double convertToRadian(String unit,double value){
        double celsuis=0;
        if(unit.equals(Constants.DEGREE)){
           celsuis=value*0.0174533;
        }

        return celsuis;

    }

}
