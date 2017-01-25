package com.example.joakim.ceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView oneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        oneTextView = (TextView) findViewById(R.id.oneTextView);

        Button buttonButton = (Button) findViewById(R.id.buttonButton);
        buttonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                oneTextView.setText("Yoyoyoyo");
            }
        });
    }



}
