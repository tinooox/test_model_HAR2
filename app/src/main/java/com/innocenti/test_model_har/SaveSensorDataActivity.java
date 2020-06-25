package com.innocenti.test_model_har;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
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
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import androidx.annotation.LongDef;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SaveSensorDataActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "SensorDataActivity";
    //variabili per inference
    float[][][][] inputData = new float[1][12][50][1];
    float[][] outputData = new float[1][5];
    Classifier classifier;
    //componenti per scrittura su file
    int badData = 0;
    boolean is_registered = false;
    CSVWriter writer, writer1;
    String fileName = "";
    String[] firstLine = {"attitude.roll", "attitude.pitch", "attitude.jaw", "gravity.x", "gravity.y", "gravity.z", "userAcc.x", "userAcc.y","userAcc.x", "rotationRate.x", "rotationRate.y", "rotationRate.z"};
    String[] dataFile = new String[12];
    int i = 0;
    //variabili sensori
    private SensorManager sensorManager;
    Sensor attitude, gravity, acceleration, rotationRate, accelerometer;
    //componenti grafici
    Button buttonStart, buttonStop;
    TextView attitudeX, attitudeY, attitudeZ, result1;
    TextView gravityX, gravityY, gravityZ;
    TextView accelerationX, accelerationY, accelerationZ;
    TextView rotationRateX,rotationRateY,rotationRateZ;
    TextView counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        badData = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_sensor_data);

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
        rotationRateY = (TextView) findViewById(R.id.rotationRateY);
        rotationRateZ = (TextView) findViewById(R.id.rotationRateZ);

        result1 = (TextView) findViewById(R.id.result1);
        counter = (TextView) findViewById(R.id.counter);
        //start();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        classifier = new Classifier(getApplicationContext());



        //gestione start stop con bottone
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                onResume();
                fileName = getData();
                final String data_csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()  + "/Test_HAR_data/"  + fileName + "DATA" +".csv";
                final String prediciton_csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()  + "/Test_HAR_data/"  + fileName + "PREDICTION"+".csv";

                try {
                    writer = new CSVWriter(new FileWriter(data_csv));
                    writer1 = new CSVWriter(new FileWriter(prediciton_csv));
                    Log.d(TAG, "onCreate: file creato ");
                    writer.writeNext(firstLine);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onCreate: ERRORE" + e);
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if(is_registered){
            try {
                writer.close();
                writer1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    protected  void onDestroy(){
        super.onDestroy();

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
            inputData[0][0][i][0] = sensorEvent.values[0];
            inputData[0][1][i][0] = sensorEvent.values[1];
            inputData[0][2][i][0] = sensorEvent.values[2];


        }

        if(sensor.getType() == Sensor.TYPE_GRAVITY){
            gravityX.setText("X: " + sensorEvent.values[0]/9.81);
            gravityY.setText("Y: " + sensorEvent.values[1]/9.81);
            gravityZ.setText("Z: " + sensorEvent.values[2]/9.81);
            inputData[0][3][i][0] = (float) (sensorEvent.values[0]/9.81);
            inputData[0][4][i][0] = (float) (sensorEvent.values[0]/9.81);
            inputData[0][5][i][0] = (float) (sensorEvent.values[0]/9.81);

        }

        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            accelerationX.setText("X: " + sensorEvent.values[0]);
            accelerationY.setText("Y: " + sensorEvent.values[1]);
            accelerationZ.setText("Z: " + sensorEvent.values[2]);
            inputData[0][6][i][0]= (float) (sensorEvent.values[0]/-9.81);
            inputData[0][7][i][0] = (float) (sensorEvent.values[1]/-9.81);
            inputData[0][8][i][0] = (float) (sensorEvent.values[2]/-9.81);

        }

        if(sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED){
            rotationRateX.setText("X: "+ sensorEvent.values[0]);
            rotationRateY.setText("Y: "+ sensorEvent.values[1]);
            rotationRateZ.setText("Z: "+ sensorEvent.values[2]);
            inputData[0][9][i][0] = sensorEvent.values[0];
            inputData[0][10][i][0] = sensorEvent.values[1];
            inputData[0][11][i][0] = sensorEvent.values[2];

        }

        if(badData>1000) {
            for (int h = 0; h < 12; h++) {
                dataFile[h] = String.valueOf(inputData[0][h][i][0]);
            }
            is_registered = true;
            writer.writeNext(dataFile);



            i++;

            if (i == 50) {
                outputData = classifier.doInference(inputData);
                //printAct(outputData);
                String[] act_prob = {getACT(outputData), getProb(outputData)};
                writer.writeNext(act_prob);
                writer1.writeNext(act_prob);

                i = 0;
            }

        }
        Log.d(TAG, "onSensorChanged: bad_data" + badData);
        badData++;






    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void start(){
        sensorManager.registerListener(this, attitude, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener( this, gravity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener( this, acceleration, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener( this, rotationRate, SensorManager.SENSOR_DELAY_FASTEST);

        gravity =  sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        attitude = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rotationRate = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);



    }



    public void printAct(float[][] prediction){
       String s = getACT(prediction);
       result1.setText(s);

    }

    public String getData(){
        Calendar time = Calendar.getInstance ();
        int year = time.get (time.YEAR);
        int month = time.get (time.MONTH) +1;
        int day = time.get (time.DAY_OF_MONTH);
        int hour = time.get (time.HOUR_OF_DAY);
        int minute = time.get (time.MINUTE);
        int second = time.get (time.SECOND);
        int ms = time.get (time.MILLISECOND);
        String nowtime = year + "_" + month + "_" + day + "," + hour + ":" + minute + ":" + second ;
        return nowtime;
    }

    public String getProb(float[][] out){
        float max = 0;
        for(int i = 0; i<5; i++){
            if(out[0][i]>max){
                max = out[0][i];
            }
        }
        return ""+ max;

    }

    public String getACT(float[][] out){
        String act = "";
        int index = 0;
        float max = 0;
        for(int i = 0; i<5; i++){
            if(out[0][i]>max){
                max = out[0][i];
                index = i;
            }
        }
        switch(index){
            case 0:
                act = "DOWNSTAIRS";
                break;
            case 1:
                act = "UPSTAIRS";
                break;
            case 2:
                act = "WALKING";
                break;
            case 3:
                act = "JOGGING";
                break;
            case 4:
                act = "SITTING";
                break;
        }
        return act;
    }


}
