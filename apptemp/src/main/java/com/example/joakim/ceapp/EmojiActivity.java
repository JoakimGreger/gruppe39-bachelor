package com.example.joakim.ceapp;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EmojiActivity extends AppCompatActivity {

    EditText emojiOneText;
    EditText emojiTwoText;
    Button doneButton;
    TextView qOneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);


        emojiOneText = (EditText) findViewById(R.id.emojiOneText);
        emojiTwoText = (EditText) findViewById(R.id.emojiTwoText);
        doneButton = (Button) findViewById(R.id.doneBtnEmoji);
        qOneText = (TextView) findViewById(R.id.qOneText);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmojiActivity.this, ""+emojiOneText.getText()+","+emojiTwoText.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
