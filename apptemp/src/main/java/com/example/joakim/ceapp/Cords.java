package com.example.joakim.ceapp;

import android.app.Application;
import android.net.NetworkInfo;

/**
 * Created by pedjo on 09-Mar-17.
 */

    public class Cords extends Application{
    public  Double latitude;
    public  Double longitude;

    public  Double getLatitude(){
        return latitude;
    }
    public void setLatitude(Double lat){
        this.latitude = lat;
    }
    public  Double getLongitude(){
        return longitude;
    }
    public void setLongitude(Double lng){
        this.longitude = lng;
    }

    private static Cords instance;

    static {
        instance = new Cords();
    }
    private Cords(){}
    public static Cords getInstance(){
        return Cords.instance;
    }
}
