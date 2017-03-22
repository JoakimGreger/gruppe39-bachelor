package com.example.joakim.ceapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

/**
 * Created by pedjo on 01-Mar-17.
 */

public class LocationChecker extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = DragActivity.class.getSimpleName();

    //Variabler for lokasjonssjekking
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public String mLatitudeText;
    public String mLongitudeText;

    //start lokasjons ting
    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //får tak i lengegrad og breddegrad
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
        }
        else {
            handleNewLocation(location);
        }

        // sjekker om telefonens lokasjon er nær Rema1000 som er nær skolen
        float[] dist = new float[1];
        float[] dist2 = new float[1];
        Location.distanceBetween(mLastLocation.getLatitude(),mLastLocation.getLongitude(),59.914530, 10.756848,dist);
        if(dist[0] < 50){
            locationCheck();
        }
        Location.distanceBetween(mLastLocation.getLatitude(),mLastLocation.getLongitude(),60.124089, 11.464895,dist2);
        if(dist2[0] < 50){
            locationCheck();
        }

    }
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
        mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
    }
    private void locationCheck(){

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
    //end lokasjonsting
}
