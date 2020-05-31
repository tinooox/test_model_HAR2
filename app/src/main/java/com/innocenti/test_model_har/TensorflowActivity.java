package com.innocenti.test_model_har;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TensorflowActivity extends AppCompatActivity {
    Interpreter tflite;
    Button doInference;
    private static final String TAG = "TensorflowActivity" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tensorflow);
        Bundle bundle = getIntent().getExtras();
        float[][] dati = (float[][]) bundle.getSerializable("value");
        Log.d(TAG, "onCreate: bundle attivo");
        Object a = null;
        doInference = (Button) findViewById(R.id.doInference);
        try{
            a = loadModelFile();
            Log.d(TAG, "onCreate: " + a);


        } catch (IOException e) {
            e.printStackTrace();
        }
        tflite = new Interpreter((MappedByteBuffer) a);


    }

    private MappedByteBuffer loadModelFile() throws IOException{
        Log.d(TAG, "loadModelFile: dentro la procedura");
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("converted_model2.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel  fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d(TAG, "loadModelFile: fuori la procedura");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        
    }

    
}
