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

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import androidx.annotation.LongDef;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SensorDataActivity extends AppCompatActivity implements SensorEventListener {
    float[][][][] a = new float[1][12][50][1];
    float[][][][] b = new float[1][3][50][1];
    float[][] out = new float[1][5];
    int i = 0;
    int j = 0;
    boolean registered = false;
    Interpreter tflite;
    private static final String TAG = "SensorDataActivity";
    private SensorManager sensorManager;
    Sensor attitude, gravity, acceleration, rotationRate, accelerometer;
    Button buttonStart, buttonStop, buttonClassifica;

    TextView attitudeX, attitudeY, attitudeZ, result1;
    TextView gravityX, gravityY, gravityZ;
    TextView accelerationX, accelerationY, accelerationZ;
    TextView rotationRateX,rotationRateY,rotationRateZ;
    TextView counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data2);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonClassifica = (Button) findViewById(R.id.classifica);

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

        result1 = (TextView) findViewById(R.id.result1);
        counter = (TextView) findViewById(R.id.counter);
        //start();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        try {
            tflite = new Interpreter(loadModelFile());
            Log.d(TAG, "onCreate: modello caricato correttamente");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: modell non caricato");
        }





        //gestione start stop con bottone
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "hai registrato " + a.length + "secondi" );
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

        buttonClassifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registered) {
                    float [][][][] data = createInputData(a);

                    Log.d(TAG, "onClick: passiamo allactivity tensorflow");
                    Intent intent = new Intent(SensorDataActivity.this, TensorflowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("value", a);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
                else{

                    Context context = getApplicationContext();
                    CharSequence text = "ERRORE, devi prima registrare un attività";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
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
        j++;
        //counter.setText("a" + j);
        Sensor sensor = sensorEvent.sensor;



        //Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]+ " Y: "+ sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
        if(sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR){
            attitudeX.setText("X: "+ sensorEvent.values[0]);
            attitudeY.setText("Y: "+ sensorEvent.values[1]);
            attitudeZ.setText("Z: "+ sensorEvent.values[2]);
            a[0][0][i][0] = sensorEvent.values[0];
            a[0][1][i][0] = sensorEvent.values[1];
            a[0][2][i][0] = sensorEvent.values[2];
        }
        if(sensor.getType() == Sensor.TYPE_GRAVITY){
            gravityX.setText("X: " + sensorEvent.values[0]/9.81);
            gravityY.setText("Y: " + sensorEvent.values[1]/9.81);
            gravityZ.setText("Z: " + sensorEvent.values[2]/9.81);
            a[0][3][i][0] = (float) (sensorEvent.values[0]/9.81);
            a[0][4][i][0] = (float) (sensorEvent.values[0]/9.81);
            a[0][5][i][0] = (float) (sensorEvent.values[0]/9.81);
        }
        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

            accelerationX.setText("X: " + sensorEvent.values[0]);
            accelerationY.setText("Y: " + sensorEvent.values[1]);
            accelerationZ.setText("Z: " + sensorEvent.values[2]);


            a[0][6][i][0]= (float) (sensorEvent.values[0]/-9.81);
            a[0][7][i][0] = (float) (sensorEvent.values[1]/-9.81);
            a[0][8][i][0] = (float) (sensorEvent.values[2]/-9.81);

        }

        if(sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED){
            rotationRateX.setText("X: "+ sensorEvent.values[0]);
            rotationRateY.setText("Y: "+ sensorEvent.values[1]);
            rotationRateZ.setText("Z: "+ sensorEvent.values[2]);
            a[0][9][i][0] = sensorEvent.values[0];
            a[0][10][i][0] = sensorEvent.values[1];
            a[0][11][i][0] = sensorEvent.values[2];
        }



        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float d1 = (float) (sensorEvent.values[0]/9.81);
            float d2 = (float) (sensorEvent.values[1]/9.81);
            float d3 = (float) (sensorEvent.values[2]/9.81);
            b[0][0][i][0] = d1;
            b[0][1][i][0] = d2;
            b[0][2][i][0] = d3;


        }



        i++;



        if (i == 50) {

            out = doInference(a);
            printAct(out);



            //onPause();
            i = 0;
            registered = true;
        }



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

    public float[][][][] createInputData(float[][][][] a){
        int length = a.length;
        float[][][][] data = new float[a.length][12][50][1];
        for(int i = 0; i< a.length; i++){
            for (int j = 0; j< 12; j++){
                for (int h = 0; h<50; h++)
                    data[i][j][h][0] = a[i][j][h][0];
                }
            }
        return data;
    }

    public float[][] doInference(float[][][][] dati_final) {

        float[][] outputval = new float[1][5];
        tflite.run(dati_final, outputval);
        return outputval;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        Log.d(TAG, "loadModelFile: dentro la procedura");
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("5_out.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d(TAG, "loadModelFile: fuori la procedura");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    public void printAct(float[][] prediction){
        int index = 0;
        float max = 0;
        for(int i = 0; i<5; i++){
            if(prediction[0][i]>max){
                max = prediction[0][i];
                index = i;
            }
        }
        switch(index){
            case 0:
                result1.setText("DOWNSTAIRS");
                counter.setText(""+max);
                break;

            case 1:
                result1.setText("UPSTAIRS" );
                counter.setText(""+max);
                break;

            case 2:
                result1.setText("WALKING");
                counter.setText(""+max);
                break;
            case 3:
                result1.setText("JOGGING");
                counter.setText(""+max);
                break;
            case 4:
                result1.setText("SITTING");
                counter.setText(""+max);
                break;

        }
    }


}
