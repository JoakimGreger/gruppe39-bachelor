package com.example.joakim.ceapp;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonButton = (Button) findViewById(R.id.buttonButton);

        buttonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                switchActivity();
            }
        });

    }

    private void switchActivity(){
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
