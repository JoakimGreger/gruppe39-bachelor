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

import java.util.Random;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreTxt;
    TextView answerTxt;
    ImageView img;
    Button resetBtn;
    int score;
    int answers;

    int scorePics[] = {
            R.drawable.score_bilde,
            R.drawable.score_bilde2,
            R.drawable.score_bilde3,
            R.drawable.score_bilde4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        answerTxt = (TextView) findViewById(R.id.answerTxt);
        scoreTxt = (TextView) findViewById(R.id.scoreTxt);
        img =(ImageView) findViewById(R.id.img);
        resetBtn = (Button) findViewById(R.id.resetBtn);

        SharedPreferences prefs = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        score = prefs.getInt("qScore", 0);

        SharedPreferences prefsX = this.getSharedPreferences("qAnswer", Context.MODE_PRIVATE);
        answers = prefsX.getInt("qAnswer",0);

        answerTxt.setText("Antall besvarelser: " + answers );
        scoreTxt.setText("Din score: " + score);

        //en random for tilfeldig score bilde
        Random rand = new Random();
        int n = rand.nextInt(scorePics.length);

        img.setImageResource(scorePics[n]);

        img.getLayoutParams().width=(score/10);
        img.getLayoutParams().height=(score/10);

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
        SharedPreferences prefsX = this.getSharedPreferences("qAnswer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorX = prefsX.edit();
        editorX.putInt("qAnswer", 0);
        editorX.commit();
        finish();
    }
}
