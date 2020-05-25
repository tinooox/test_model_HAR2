package com.innocenti.test_model_har;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SensorDataActivity extends AppCompatActivity implements SensorEventListener {
    float[][] a = new float[12][300];
    int i = 0;

    private static final String TAG = "SensorDataActivity";
    private SensorManager sensorManager;
    Sensor attitude, gravity, acceleration, rotationRate;
    Button buttonStart, buttonStop;

    TextView attitudeX, attitudeY, attitudeZ;
    TextView gravityX, gravityY, gravityZ;
    TextView accelerationX, accelerationY, accelerationZ;
    TextView rotationRateX,rotationRateY,rotationRateZ;

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

        accelerationX = (TextView) findViewById(R.id.accelerationX);
        accelerationY = (TextView) findViewById(R.id.accelerationY);
        accelerationZ = (TextView) findViewById(R.id.accelerationZ);

        rotationRateX = (TextView) findViewById(R.id.rotationRateX);
        rotationRateY= (TextView) findViewById(R.id.rotationRateY);
        rotationRateZ = (TextView) findViewById(R.id.rotationRateZ);

        //start();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);





        //gestione start stop con bottone
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "prova " + a[1][2] );
                onPause();

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        start();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        //Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]+ " Y: "+ sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
        if(sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR){
            attitudeX.setText("X: "+ sensorEvent.values[0]);
            attitudeY.setText("Y: "+ sensorEvent.values[1]);
            attitudeZ.setText("Z: "+ sensorEvent.values[2]);
            a[0][i] = sensorEvent.values[0];
            a[1][i] = sensorEvent.values[1];
            a[2][i] = sensorEvent.values[2];
        }
        if(sensor.getType() == Sensor.TYPE_GRAVITY){
            gravityX.setText("X: " + sensorEvent.values[0]);
            gravityY.setText("Y: " + sensorEvent.values[1]);
            gravityZ.setText("Z: " + sensorEvent.values[2]);
            a[3][i] = sensorEvent.values[0];
            a[4][i] = sensorEvent.values[1];
            a[5][i] = sensorEvent.values[2];
        }
        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            accelerationX.setText("X: "+ sensorEvent.values[0]);
            accelerationY.setText("Y: "+ sensorEvent.values[1]);
            accelerationZ.setText("Z: "+ sensorEvent.values[2]);
            a[6][i] = sensorEvent.values[0];
            a[7][i] = sensorEvent.values[1];
            a[8][i] = sensorEvent.values[2];
        }

        if(sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED){
            rotationRateX.setText("X: "+ sensorEvent.values[0]);
            rotationRateY.setText("Y: "+ sensorEvent.values[1]);
            rotationRateZ.setText("Z: "+ sensorEvent.values[2]);
            a[9][i] = sensorEvent.values[0];
            a[10][i] = sensorEvent.values[1];
            a[11][i] = sensorEvent.values[2];
        }
        i++;

        if (i == 300) {
            Log.d(TAG, "onSensorChanged: DATI SALVATI");
            Log.d(TAG, "onSensorChanged: " + a[0].length + " ");
            onPause();
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void start(){
        sensorManager.registerListener(this, attitude, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, rotationRate, SensorManager.SENSOR_DELAY_NORMAL);
        gravity =  sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        attitude = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rotationRate = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

    }


}
