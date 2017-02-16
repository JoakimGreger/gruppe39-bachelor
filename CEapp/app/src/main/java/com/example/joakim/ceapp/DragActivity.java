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
import android.media.Image;
import android.os.Environment;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class
DragActivity extends Activity implements GestureDetector.OnGestureListener {

    TextView innholdTxt;
    Button nextBtn;
    Button doneBtn;
    GestureDetector detector;
    ImageView smileyImg;
    ImageView handImg;
    Animation slideAnim;
    Animation slideUpAnim;
    NotificationCompat.Builder notification;
    private static final int uniqueID = 12345;
    // div variabler som blir brukt
    private static final int REQUEST_WRITE_STORAGE = 112;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

        //div knapper og tekstfelt som blir brukt
        innholdTxt = (TextView) findViewById(R.id.innholdTxt);
        nextBtn = (Button)findViewById(R.id.nextBtn);
        doneBtn = (Button) findViewById(R.id.doneBtn);
        detector = new GestureDetector(this, this);
        smileyImg = (ImageView) findViewById(R.id.smileyImg);
        handImg = (ImageView) findViewById(R.id.handImg);
        slideAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide);
        slideUpAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slideup);
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        handImg.setImageResource(R.drawable.hand);
        smileyImg.setImageResource(R.drawable.ic_neutralface);
        startAnims();

        //Gjemmer nextBtn og viser doneBtn
        nextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                qOne = i;
                innholdTxt.setText("Hvor fornøyd er du med tiden handelen tok?");
                doneBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
                startAnims();
                smileyImg.setImageResource(R.drawable.ic_neutralface);
            }
        });

        //Lagrer data og setter en score onClick
        doneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick (View v) {
                qTwo = i;
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = sdf.format(date);
                storeScore(50);
                createNotification(v);
                storeData(dateString + "," + qOne + "," + qTwo ); // Lagrer svaret med dato og svar nummer
            }
        });

    }

    public void storeScore(Integer score){

        //Henter tidliger score hvis den finnes
        SharedPreferences startscore = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        int firstscore = startscore.getInt("qScore", 0);

        //Lagrer tidligere score + nye score som kan hentes på alle activities med riktig key (qScore)
        SharedPreferences prefs = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("qScore", (firstscore+score));
        editor.commit();
    }

    public void storeData(String data){
        String linebreak = System.getProperty("line.separator"); //linjeskift
        final File path = Environment.getExternalStoragePublicDirectory
                (
                        Environment.DIRECTORY_DOWNLOADS + "/CEdata/" //Hvor dataen lagres
                );
        if (!path.exists()){ // sjekker om mappen finnes
            path.mkdirs(); // lager den hvis ikke
        }
        //final File file = new File(path, "CEdata.csv"); //navn på fil
        File file = new File(path,"CEdata.csv");
        try{
            file.createNewFile();
            FileWriter writer = new FileWriter(file, true);
            writer.append(data + linebreak);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data har blitt lagret, takk for svar:)", Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
        }

        finish(); //sender deg tilbake til MainActivity når den er ferdig
    }

    public void createNotification(View view) {
        notification.setSmallIcon(R.drawable.ic_smileface);
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
        if (diffY > 0){

            ned += 1;
            if(ned == 10){ //brukes for smoothere scrolling
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
    public void checkI(){
        if (i > smileys.length-1){
            i = smileys.length-1;
        }
        if (i < 0){
            i = 0;
        }
    }
    public void startAnims(){

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
}


