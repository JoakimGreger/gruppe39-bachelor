package com.example.joakim.ceapp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private Button buttonButton;
    private Button scoreBtn;
    private Button loginBtn;
    private Button logoutBtn;
    private Button emojiButton;
    private String email;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForLocationPermission();
        //if (isServiceRunning(LocationService.class) == false) {
        startService(new Intent(this, LocationService.class));
        //}

        final LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        buttonButton = (Button) findViewById(R.id.buttonButton);
        scoreBtn = (Button) findViewById(R.id.scoreBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("session_token", Context.MODE_PRIVATE);
        token = pref.getString("session_token", null);
        email = pref.getString("email", null);
        if (token != null && email != null) {
            Log.e("Exception", "TOKEN:" + token);
            Log.e("Exception", "EMAIL:" + email);
            loginBtn.setVisibility(View.GONE);
            logoutBtn = (Button) findViewById(R.id.logoutBtn);
            logoutBtn.setVisibility(View.VISIBLE);
        }

        buttonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        switchActivityDrag();

                        return;
                    } else {
                        noGpsDialogBox();
                    }
                }
            });
        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivityScore();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivityLogin();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("session_token", Context.MODE_PRIVATE);
        token = pref.getString("session_token", null);
        email = pref.getString("email", null);
        if (token != null && email != null) {
            Log.e("Exception", "TOKEN:" + token);
            Log.e("Exception", "EMAIL:" + email);
            loginBtn.setVisibility(View.GONE);
            logoutBtn = (Button) findViewById(R.id.logoutBtn);
            logoutBtn.setVisibility(View.VISIBLE);
        }
    }


    public void logoutBtnClicked(View v) {
        new LogoutTask().execute("http://webapp.bimorstad.tech/user/logout");
    }

    private void switchActivityDrag(){
        Intent intent = new Intent(this, LocationMapsActivity.class);
        startActivity(intent);
    }

    private void switchActivityLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void switchActivityScore(){
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }

    public void askForLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void noGpsDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Din GPS ser ut til å være slått av. Venligst slå på GPS")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public class LogoutTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {
                String url = params[0] + "?email=" + email + "&uuid=" + token;
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("POST");
                Log.e("Exception", "URL:" + url);
                httpURLConnection.setDoOutput(true);

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("You have been logged out")) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("session_token", Context.MODE_PRIVATE);
                pref.edit().clear().commit();
                loginBtn.setVisibility(View.VISIBLE);
                logoutBtn.setVisibility(View.GONE);
            } else {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("session_token", Context.MODE_PRIVATE);
                pref.edit().clear().commit();
                loginBtn.setVisibility(View.VISIBLE);
                logoutBtn.setVisibility(View.GONE);
                Log.e("Exception", "Logout failed:" + result);
            }

        }
    }
}
