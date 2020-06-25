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

        Button realTimeAct = findViewById(R.id.realTimeAct);

        recordAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: bottone record cliccato");
                Intent intent = new Intent(MainActivity.this, SaveSensorDataActivity.class);
                startActivity(intent);
            }
        });

        realTimeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: bottone TF cliccato");
                Intent intent = new Intent(MainActivity.this, RunTimeHarActivity.class);
                startActivity(intent);
            }
        });




    }
}

