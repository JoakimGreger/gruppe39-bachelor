package com.example.joakim.ceapp;

import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class DragActivity extends Activity implements GestureDetector.OnGestureListener {

    TextView dirTxt;
    GestureDetector detector;
    ImageView smileyImg;

    int i = 2;
    int opp;
    int ned;
    int smileys[] = {
            R.mipmap.frown_smiley,
            R.mipmap.sad_smiley,
            R.mipmap.neutral_smiley,
            R.mipmap.smile_smiley,
            R.mipmap.happy_smiley
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

        dirTxt = (TextView) findViewById(R.id.dirTxt);
        detector = new GestureDetector(this, this);
        smileyImg = (ImageView) findViewById(R.id.smileyImg);
        smileyImg.setImageResource(R.mipmap.neutral_smiley);
    }
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
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
            dirTxt.setText("bottom" + i);
            ned += 1;
            if(ned == 10){
                i -= 1;
                ned = 0;
                checkI();
                smileyImg.setImageResource(smileys[i]);
            }

        } else {
            dirTxt.setText("top" + i);
            opp += 1;
            if (opp == 10) {
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


