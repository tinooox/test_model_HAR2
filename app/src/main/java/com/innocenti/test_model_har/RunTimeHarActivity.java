package com.innocenti.test_model_har;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class RunTimeHarActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static int prevIdx = -1;

    private static final String TAG = "RunTimeActivity" ;
    private TextView downstairsTextView;
    private TextView joggingTextView;
    private TextView sittingTextView;
    private TextView upstairsTextView;
    private TextView walkingTextView;


    private TableRow downstairsRow;
    private TableRow joggingRow;
    private TableRow sittingRow;
    private TableRow upstairsRow;
    private TableRow walkingRow;
    private Classifier classifier;

    private SensorManager sensorManager;
    Sensor attitude, gravity, acceleration, rotationRate, accelerometer;
    String[] labels = {"downstairs", "upstairs", "walking", "jogging", "sitting"};
    private float[][][][] input_data;
    private float[][] output_data;
    int i;

    private TextToSpeech textToSpeech;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_time_har);
        input_data = new float[1][12][50][1];
        output_data = new float[1][5];
        i = 0;
        classifier = new Classifier(getApplicationContext());
        downstairsTextView = (TextView) findViewById(R.id.downstairs_prob);
        joggingTextView = (TextView) findViewById(R.id.jogging_prob);
        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
        upstairsTextView = (TextView) findViewById(R.id.upstairs_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);


        downstairsRow = (TableRow) findViewById(R.id.downstairs_row);
        joggingRow = (TableRow) findViewById(R.id.jogging_row);
        sittingRow = (TableRow) findViewById(R.id.sitting_row);
        upstairsRow = (TableRow) findViewById(R.id.upstairs_row);
        walkingRow = (TableRow) findViewById(R.id.walking_row);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        assert sensorManager != null;
        gravity =  sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        attitude = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rotationRate = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, attitude, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, rotationRate, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);

    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if(sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR){
            input_data[0][0][i][0] = sensorEvent.values[0];
            input_data[0][1][i][0] = sensorEvent.values[1];
            input_data[0][2][i][0] = sensorEvent.values[2];
        }

        if(sensor.getType() == Sensor.TYPE_GRAVITY){
            input_data[0][3][i][0] = (float) (sensorEvent.values[0]/9.81);
            input_data[0][4][i][0] = (float) (sensorEvent.values[0]/9.81);
            input_data[0][5][i][0] = (float) (sensorEvent.values[0]/9.81);
        }
        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            input_data[0][6][i][0]= (float) (sensorEvent.values[0]/-9.81);
            input_data[0][7][i][0] = (float) (sensorEvent.values[1]/-9.81);
            input_data[0][8][i][0] = (float) (sensorEvent.values[2]/-9.81);

        }

        if(sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED){
            input_data[0][9][i][0] = sensorEvent.values[0];
            input_data[0][10][i][0] = sensorEvent.values[1];
            input_data[0][11][i][0] = sensorEvent.values[2];
        }
        i++;

        if (i == 50) {
            output_data = classifier.doInference(input_data);
            prediction(output_data);
            i = 0;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void prediction(float probabilities[][]){
        int index = 0;
        float max = 0;
        for(int i = 0; i<5; i++){
            if(probabilities[0][i]>max){
                max = probabilities[0][i];
                index = i;
            }
        }
        colorRow(index);
        setProbabilities();

    }

    public void colorRow(int i){

        downstairsRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        joggingRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        sittingRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        upstairsRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        walkingRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));

        switch (i){
            case 0:
                downstairsRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
                break;
            case 1:
                upstairsRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
                break;
            case 2:
                walkingRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
                break;
            case 3:
                joggingRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
                break;
            case 4:
                sittingRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));

                break;





        }
    }

    @SuppressLint("SetTextI18n")
    public void setProbabilities(){
        downstairsTextView.setText(Float.toString(approssima(output_data[0][0])));
        upstairsTextView.setText(Float.toString(approssima(output_data[0][1])));
        walkingTextView.setText(Float.toString(approssima(output_data[0][2])));
        joggingTextView.setText(Float.toString(approssima(output_data[0][3])));
        sittingTextView.setText(Float.toString(approssima(output_data[0][4])));
    }

    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (output_data == null || output_data.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < output_data[0].length; i++) {
                    if (output_data[0][i] > max) {
                        idx = i;
                        max = output_data[0][i];
                    }
                }

                if(max > 0.50 && idx != prevIdx) {
                    textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null,
                            Integer.toString(new Random().nextInt()));
                    prevIdx = idx;
                }
            }
        }, 1000, 3000);
    }

    protected void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private static float approssima(float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }




}

