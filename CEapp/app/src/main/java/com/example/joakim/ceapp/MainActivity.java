package com.example.joakim.ceapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button buttonButton;
    private Button scoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonButton = (Button) findViewById(R.id.buttonButton);
        scoreBtn = (Button) findViewById(R.id.scoreBtn);

        buttonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                switchActivityDrag();
            }
        });

        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivityScore();
            }
        });

}

    private void switchActivityDrag(){
        Intent intent = new Intent(this, DragActivity.class);
        startActivity(intent);
    }
    private void switchActivityScore(){
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }
}
