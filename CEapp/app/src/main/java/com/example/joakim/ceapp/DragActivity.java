package com.example.joakim.ceapp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.CollationElementIterator;
import java.text.SimpleDateFormat;


public class
DragActivity extends Activity implements GestureDetector.OnGestureListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = DragActivity.class.getSimpleName();

    TextView innholdTxt;
    Button nextBtn;
    Button doneBtn;
    GestureDetector detector;
    ImageView smileyImg;
    ImageView handImg;
    Animation slideAnim;
    Animation slideUpAnim;
    Animation fadeOutAnim;
    Animation fadeInAnim;
    NotificationCompat.Builder notification;
    private static final int uniqueID = 12345;
    // div variabler som blir brukt
    int i = 2;
    int qOne;
    int qTwo;
    int opp;
    int ned;
    int smileys[] = {
            R.drawable.ic_madface,
            R.drawable.ic_sadface,
            R.drawable.ic_neutralface,
            R.drawable.ic_smileface,
            R.drawable.ic_happyface
    };
    //Variabler for lokasjonssjekking
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitudeText;
    private String mLongitudeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

        // Googles Api client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //div knapper og tekstfelt som blir brukt
        innholdTxt = (TextView) findViewById(R.id.innholdTxt);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        doneBtn = (Button) findViewById(R.id.doneBtn);
        detector = new GestureDetector(this, this);
        smileyImg = (ImageView) findViewById(R.id.smileyImg);
        handImg = (ImageView) findViewById(R.id.handImg);

        //animasjoner
        slideAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        slideUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup);
        fadeOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);

        //notifikasjon
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        handImg.setImageResource(R.drawable.hand);
        smileyImg.setImageResource(R.drawable.ic_neutralface);
        startHandAnims();

        //Gjemmer nextBtn og viser doneBtn
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFadeAnims();
                qOne = i;
                //doneBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
                i = 2;
            }
        });

        //Lagrer data og setter en score onClick
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qTwo = i;
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = sdf.format(date);
                storeScore(50);
                createNotification(v);
                storeData(dateString + "," + qOne + "," + qTwo + "," + mLatitudeText + "," + mLongitudeText); // Lagrer svaret med dato og svar nummer
            }
        });

    }

    public void storeScore(Integer score) {

        //Henter tidliger score hvis den finnes
        SharedPreferences startscore = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        int firstscore = startscore.getInt("qScore", 0);

        //Lagrer tidligere score + nye score som kan hentes på alle activities med riktig key (qScore)
        SharedPreferences prefs = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("qScore", (firstscore + score));
        editor.commit();
    }

    public void storeData(String data) {
        String linebreak = System.getProperty("line.separator"); //linjeskift
        final File path = Environment.getExternalStoragePublicDirectory
                (
                        Environment.DIRECTORY_DOWNLOADS + "/CEdata/" //Hvor dataen lagres
                );
        if (!path.exists()) { // sjekker om mappen finnes
            path.mkdirs(); // lager den hvis ikke
        }
        //final File file = new File(path, "CEdata.csv"); //navn på fil
        File file = new File(path, "CEdata.csv");
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file, true);
            writer.append(data + linebreak);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Takk for svar. 50 poeng tildelt!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        finish(); //sender deg tilbake til MainActivity når den er ferdig
    }

    public void createNotification(View view) {
        notification.setSmallIcon(R.drawable.ic_notification_img);
        notification.setLargeIcon(BitmapFactory.decodeResource(view.getResources(),
                R.mipmap.app_icon));
        notification.setTicker("Dette er en ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Notifikasjon");
        notification.setContentText("Dette er notifikasjonens innhold");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float diffY = e2.getY() - e1.getY();
        if (diffY > 0) {

            ned += 1;
            if (ned == 10) { //brukes for smoothere scrolling
                i -= 1;
                ned = 0;
                checkI();
                smileyImg.setImageResource(smileys[i]);
            }

        } else {

            opp += 1;
            if (opp == 10) { //brukes for smoothere scrolling
                i += 1;
                opp = 0;
                checkI();
                smileyImg.setImageResource(smileys[i]);
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void checkI() {
        if (i > smileys.length - 1) {
            i = smileys.length - 1;
        }
        if (i < 0) {
            i = 0;
        }
    }

    public void startHandAnims() {

        handImg.startAnimation(slideAnim);
        slideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handImg.startAnimation(slideUpAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slideUpAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handImg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startFadeAnims() {
        innholdTxt.startAnimation(fadeOutAnim);
        smileyImg.startAnimation(fadeOutAnim);
        //doneBtn.startAnimation(fadeInAnim);

        fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                innholdTxt.startAnimation(fadeInAnim);
                smileyImg.startAnimation(fadeInAnim);
                innholdTxt.setText("Hvor fornøyd er du med tiden handelen tok?");
                smileyImg.setImageResource(R.drawable.ic_neutralface);
                doneBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //start lokasjons ting
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
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
            // Blank for a moment...
        }
        else {
            handleNewLocation(location);
        };
    }
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
        mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
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


