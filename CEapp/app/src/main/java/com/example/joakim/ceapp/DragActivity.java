package com.example.joakim.ceapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class DragActivity extends Activity implements GestureDetector.OnGestureListener {

    TextView innholdTxt;
    Button nextBtn;
    Button doneBtn;
    GestureDetector detector;
    ImageView smileyImg;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

        //div knapper og tekstfelt som blir brukt
        innholdTxt = (TextView) findViewById(R.id.innholdTxt);
        nextBtn = (Button)findViewById(R.id.nextBtn);
        doneBtn = (Button) findViewById(R.id.doneBtn);
        detector = new GestureDetector(this, this);
        smileyImg = (ImageView) findViewById(R.id.smileyImg);

        smileyImg.setImageResource(R.drawable.ic_neutralface);

        //Gjemmer nextBtn og viser doneBtn
        nextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                qOne = i;
                innholdTxt.setText("Hvor fornøyd er du med tiden handelen tok?");
                doneBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
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
            Toast.makeText(this, "Data har blitt lagret, takk for svar:)", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
        }

        finish(); //sender deg tilbake til MainActivity når den er ferdig
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
}


