package com.example.joakim.ceapp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Random;

public class MainActivity extends AppCompatActivity {



    private Button buttonButton;
    private Button scoreBtn;
    private Button emojiButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForLocationPermission();
        //if (isServiceRunning(LocationService.class) == false) {
            startService(new Intent(this, LocationService.class));
        //}



        buttonButton = (Button) findViewById(R.id.buttonButton);
        scoreBtn = (Button) findViewById(R.id.scoreBtn);
        emojiButton = (Button) findViewById(R.id.emojiButton);

            buttonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Cords.getInstance().getLatitude() != null) {
                        switchActivityDrag();
                    } else {
                        Toast.makeText(MainActivity.this, "Du har desverre ikke fått en undersøkelse ennå. Sjekk igjen senere", Toast.LENGTH_LONG).show();
                    }
                }
            });
        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivityScore();
            }
        });

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivityEmoji();
            }
        });

}

    private void switchActivityDrag(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, DragActivity.class);
            startActivity(intent);
        } else {
            askForStoragePermission();
        }
    }
    private void switchActivityScore(){
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }
    private void switchActivityEmoji(){
        Intent intent = new Intent(this, EmojiActivity.class);
        startActivity(intent);
    }

    public void askForStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
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
}
