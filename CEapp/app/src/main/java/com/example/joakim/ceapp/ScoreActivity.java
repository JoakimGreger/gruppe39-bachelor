package com.example.joakim.ceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreTxt;
    ImageView img;
    Button resetBtn;
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scoreTxt = (TextView) findViewById(R.id.scoreTxt);
        img =(ImageView) findViewById(R.id.img);
        resetBtn = (Button) findViewById(R.id.resetBtn);

        SharedPreferences prefs = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        score = prefs.getInt("qScore", 0);

        scoreTxt.setText("Din score: " + score);

        img.getLayoutParams().width=(score/20);
        img.getLayoutParams().height=(score/20);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetScore();
            }
        });

    }

    public void resetScore(){
        //Lagrer tidligere score + nye score som kan hentes på alle activities med riktig key (qScore)
        SharedPreferences prefs = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("qScore",0);
        editor.commit();
        finish();
    }
}
