package com.example.joakim.ceapp;

import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.File;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;

public class SecondActivity extends AppCompatActivity {

    TextView textTxt;
    TextView testTxt;
    ImageButton buttonUp;
    ImageButton buttonDown;
    Button doneButton;

    int i = 3;

    private String[] tekst = {
            "gladest",
            "gladere",
            "glad",
            "nøytral",
            "sur",
            "surere",
            "surest"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        doneButton = (Button) findViewById(R.id.doneBtn);

        testTxt = (TextView) findViewById(R.id.testTxt);

        textTxt = (TextView) findViewById(R.id.textTxt);
        textTxt.setText(tekst[i]);

        buttonUp = (ImageButton) findViewById(R.id.buttonUp);
        buttonDown = (ImageButton) findViewById(R.id.buttonDown);

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i -= 1;
                if (i < 0){
                    i = 0;
                }
                if (i >= 0) {
                    textTxt.setText(tekst[i]);
                    testTxt.setText("" + i);
                }
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i += 1;
                if (i > 6){
                    i = 6;
                }
                if (i <= 6) {
                    textTxt.setText(tekst[i]);
                    testTxt.setText("" + i);
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick (View v) {
                storeData("ID=1, Svar="+i);
            }
        });
    }
            public void storeData(String data){
                final File path = Environment.getExternalStoragePublicDirectory
                        (
                                Environment.DIRECTORY_DCIM + "/CEappData/" //Hvor dataen lagres
                        );
                if (!path.exists()){ // sjekker om mappen finnes
                    path.mkdirs(); // lager den hvis ikke
                }
                final File file = new File(path, "CEdata.txt");
                try{
                    file.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(data);

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();
                }
                catch (IOException e){
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                finish();
            }
}
