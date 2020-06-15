package com.innocenti.test_model_har;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import android.content.Context;

public class Classifier {
    private static final String TAG ="Classifier" ;
    private Interpreter tflite;


    public Classifier(Context context) {
        try {
            tflite = new Interpreter(loadModelFile(context));
            Log.d(TAG, "onCreate: modello caricato correttamente");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: modello non caricato");
        }

    }

    public float[][] doInference(float[][][][] dati_final) {
        float[][] outputval = new float[1][5];
        tflite.run(dati_final, outputval);
        return outputval;
    }






    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        Log.d(TAG, "loadModelFile: dentro la procedura");
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("5_out.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d(TAG, "loadModelFile: fuori la procedura");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }




}
