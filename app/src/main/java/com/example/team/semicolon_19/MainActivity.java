package com.example.team.semicolon_19;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private TextView txvResult;
    TextToSpeech t1;
    private CheckBox cBYes;
    private CheckBox cBNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvResult = (TextView) findViewById(R.id.txvResult);
        cBYes = (CheckBox) findViewById(R.id.checkBoxYes);
        cBNo = (CheckBox) findViewById(R.id.checkBoxNo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        }

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Context context = getApplicationContext();
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    String toSpeak = "Please Say Yes or No";
                    t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {
                            //getInput();
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            getInput();
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "1");
                }
            }
        });
    }

    public void getSpeechInput(View view) {
        getInput();
    }

    public void getInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    Log.i("result", result.get(0));
                    if(result.get(0).equalsIgnoreCase("Yes")) {
                        Log.i("Yes", "Yes");
                        cBYes.setChecked(Boolean.TRUE);
                        cBNo.setChecked(Boolean.FALSE);
                    } else if(result.get(0).equalsIgnoreCase("No")){
                        Log.i("No", "No");
                        cBNo.setChecked(Boolean.TRUE);
                        cBYes.setChecked(Boolean.FALSE);
                    }

                    new Thread(new Runnable() {
                        public void run() {
                            // a potentially time consuming task
                            Intent in = new Intent(MainActivity.this, CameraActivity.class);
                            startActivity(in);
                        }
                    }).start();

                }
        }
    }
}
