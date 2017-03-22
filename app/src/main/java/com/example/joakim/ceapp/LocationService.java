package com.example.joakim.ceapp;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pedjo on 01-Mar-17.
 */

public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //location variabler
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    //notification variabler

    //JSON variabler
    List<Double> latitude = new ArrayList<>();
    List<Double> longitude = new ArrayList<>();
    List<String> title = new ArrayList<>();
    //List<String> questions = new ArrayList<>();
    //private Double[] latitude;
    //private Double[] longitude;

    public LocationService() {

    }

    @Override
    public void onCreate() {
        new getJSON().execute("http://webapp.bimorstad.tech/usertest/read");
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //start hent JSON fra nettside funksjon
    public class getJSON extends AsyncTask <String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try{
                String address = params[0];
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line=reader.readLine())!= null){
                    builder.append(line);
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if (connection != null){
                    connection.disconnect();
                }
                try{
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return builder.toString();
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++){
                    JSONObject obj = json.getJSONObject(i);
                    latitude.add(obj.getDouble("latitude"));
                    longitude.add(obj.getDouble("longitude"));
                    title.add(obj.getString("title"));
                    //questions.add(obj.getString("Questions"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // slutt hent JSON fra nettside funksjon

    @Override
    public void onLocationChanged(Location location) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            final float[] dist = new float[1];
            //sjekker om mobilens lokasjon er lik en annen lokasjon innen 50 meter og lagrer lat og long som kan hentes i DragActivity
            for (int i = 0; i<latitude.size(); i++) {
                Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), latitude.get(i), longitude.get(i), dist);
                if (dist[0] < 50) {
                    final int finalI = i;
                    // countdown for 7 minutter
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            //hvis location er fortsatt lik etter 7 min, send notifikasjon
                            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), latitude.get(finalI), longitude.get(finalI), dist);
                            if(dist[0]<50) {
                                createNotification(title.get(finalI), "Ny undersøkelse fra " + title.get(finalI));
                                Cords.getInstance().setLatitude(latitude.get(finalI));
                                Cords.getInstance().setLongitude(longitude.get(finalI));

                            }
                        }
                    }.start();
                }
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(60000); //Opdaterer lokasjon hvert minutt

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "Location services stopped", Toast.LENGTH_LONG).show();

        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public void createNotification(String title, String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_img)
                        .setContentTitle(title)
                        .setContentText(content);
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, DragActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setAutoCancel(true);
        //vibrasjon builder.setVibrate(new long[] { 600, 600});
        nManager.notify(NOTIFICATION_ID, builder.build());

    }

    }


