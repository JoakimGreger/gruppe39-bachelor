package com.example.joakim.ceapp;

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

    TextView resultTxt;
    ImageView lowImage;
    ImageView medImage;
    ImageView fullImage;

    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lowImage = (ImageView) findViewById(R.id.lowImg);
        medImage = (ImageView) findViewById(R.id.medImg);
        fullImage = (ImageView) findViewById(R.id.fullImg);

        lowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowImage.setColorFilter(Color.GREEN);
                medImage.setColorFilter(Color.DKGRAY);
                fullImage.setColorFilter(Color.DKGRAY);

                result = 1;

            }
        });

        medImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medImage.setColorFilter(Color.GREEN);
                lowImage.setColorFilter(Color.DKGRAY);
                fullImage.setColorFilter(Color.DKGRAY);

               result = 2;
            }
        });

        fullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullImage.setColorFilter(Color.GREEN);
                lowImage.setColorFilter(Color.DKGRAY);
                medImage.setColorFilter(Color.DKGRAY);

               result = 3;
            }
        });

        Button buttonButton = (Button) findViewById(R.id.buttonButton);
        buttonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                TextView resultsTxt = (TextView) findViewById(R.id.resultTxt);

                if (result == 1){
                    resultsTxt.setText("Lav");
                } if (result == 2){
                    resultsTxt.setText("Medium");
                } if (result == 3){
                    resultsTxt.setText("Høy");
                }
            }
        });
    }



}
