package com.innocenti.test_model_har;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button recordAct  = findViewById(R.id.recordAct);
        Button classifyWEKA = findViewById(R.id.classifyWEKA);
        Button classifyTF = findViewById(R.id.classifyTF);

        recordAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: bottone record cliccato");
                Intent intent = new Intent(MainActivity.this, SensorDataActivity.class);
                startActivity(intent);
            }
        });

        classifyTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: bottone TF cliccato");
                Intent intent = new Intent(MainActivity.this, RunTimeHar.class);
                startActivity(intent);
            }
        });

        classifyWEKA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: bottone WEKA cliccato");
                Intent intent = new Intent(MainActivity.this, WekaActivity.class);
                startActivity(intent);
            }
        });


    }
}

