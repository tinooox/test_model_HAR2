package com.innocenti.test_model_har;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorDataActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SensorDataActivity";
    private SensorManager sensorManager;
    Sensor attitude, gravity;
    Button buttonStart, buttonStop;

    TextView attitudeX, attitudeY, attitudeZ;
    TextView gravityX, gravityY, gravityZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data2);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);

        attitudeX = (TextView) findViewById(R.id.attitudeX);
        attitudeY = (TextView) findViewById(R.id.attitudeY);
        attitudeZ = (TextView) findViewById(R.id.attitudeZ);

        gravityX = (TextView) findViewById(R.id.gravityX);
        gravityY = (TextView) findViewById(R.id.gravityY);
        gravityZ = (TextView) findViewById(R.id.gravityZ);
        //start();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);





        //gestione start stop con bottone
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]+ " Y: "+ sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
        attitudeX.setText("X: "+ sensorEvent.values[0]);
        attitudeY.setText("Y: "+ sensorEvent.values[1]);
        attitudeZ.setText("Z: "+ sensorEvent.values[2]);



        gravityX.setText("X: " + sensorEvent.values[0]);
        gravityY.setText("Y: " + sensorEvent.values[0]);
        gravityZ.setText("Z: " + sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start(){
        sensorManager.registerListener(this, attitude, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        gravity =  sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        attitude = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

    }


}
