package com.example.joakim.ceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreTxt;
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scoreTxt = (TextView) findViewById(R.id.scoreTxt);

        SharedPreferences prefs = this.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        score = prefs.getInt("qScore", 0);

        scoreTxt.setText("" + score);
    }
}
