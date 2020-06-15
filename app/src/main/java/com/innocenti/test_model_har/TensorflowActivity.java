package com.innocenti.test_model_har;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TensorflowActivity extends AppCompatActivity {
    Interpreter tflite;
    TextView result;
    Button inferenceButton;
    private static final String TAG = "TensorflowActivity" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tensorflow);
        Bundle bundle = getIntent().getExtras();
        final float[][][][] data = (float[][][][]) bundle.getSerializable("value");
        int length = data.length;
        Log.d(TAG, "onCreate: bundle attivo, finestre di tempo da classificare:  "+ length);
        inferenceButton = (Button) findViewById(R.id.doInference);
        result = (TextView) findViewById(R.id.result);

        //caricamento modello
        try {
            tflite = new Interpreter(loadModelFile());
            Log.d(TAG, "onCreate: modello caricato correttamente");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: modell non caricato");
        }

        //do inference
        inferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float[][] prediction = doInference(data);
                for(int i = 0; i<5; i++){
                    Log.d(TAG, " " + prediction[0][i]);
                }
                printAct(prediction);
            }


        });

    }





    private MappedByteBuffer loadModelFile() throws IOException{
        Log.d(TAG, "loadModelFile: dentro la procedura");
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("5_out.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel  fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d(TAG, "loadModelFile: fuori la procedura");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    public float[][] doInference(float[][][][] dati_final) {

        float[][] outputval = new float[1][5];

        tflite.run(dati_final, outputval);
        return outputval;
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
                result.setText("DOWNSTAIRS");
                break;

            case 1:
                result.setText("UPSTAIRS");
                break;

            case 2:
                result.setText("WALKING");
                break;
            case 3:
                result.setText("JOGGING");
                break;
            case 4:
                result.setText("SITTING");
                break;

        }
    }

    
}
