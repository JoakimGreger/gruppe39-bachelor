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
import android.widget.Toast;

import java.io.File;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = sdf.format(date);
                storeData(dateString + "," +i); // Lagrer svaret med dato og svar nummer
            }
        });
    }
            public void storeData(String data){
                String linebreak = System.getProperty("line.separator");
                final File path = Environment.getExternalStoragePublicDirectory
                        (
                                Environment.DIRECTORY_DOWNLOADS + "/CEdata/" //Hvor dataen lagres
                        );
                if (!path.exists()){ // sjekker om mappen finnes
                    path.mkdirs(); // lager den hvis ikke
                }
                //final File file = new File(path, "CEdata.csv"); //navn på fil
                File file = new File(path,"CEdata.csv");
                try{
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file, true);
                    writer.append(data + linebreak);
                    writer.flush();
                    writer.close();
                    Toast.makeText(this, "Data har blitt lagret, takk for svar:)", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e){
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                finish();
            }
}
