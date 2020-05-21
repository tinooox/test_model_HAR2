package com.innocenti.test_model_har;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorDataActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SensorDataActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;

    TextView accelerometerX, accelerometerY, accelerometerZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data2);

        accelerometerX = (TextView) findViewById(R.id.accelerometerX);
        accelerometerY = (TextView) findViewById(R.id.accelerometerY);
        accelerometerZ = (TextView) findViewById(R.id.accelerometerZ);


        Log.d(TAG, "onCreate: Inizializzazione sensor services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: regtistrazione accelerometro");
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]+ "Y: "+ sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
        accelerometerX.setText("X: "+ sensorEvent.values[0]);
        accelerometerY.setText("Y: "+ sensorEvent.values[1]);
        accelerometerZ.setText("Z: "+ sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
