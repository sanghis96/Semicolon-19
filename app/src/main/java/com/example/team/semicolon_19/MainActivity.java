package com.example.team.semicolon_19;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
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

public class MainActivity extends AppCompatActivity implements RecognitionListener {


    private TextView txvResult;
    TextToSpeech t1;
    private CheckBox cBYes;
    private CheckBox cBNo;
    public SpeechRecognizer speech;
    private Integer onDoneCounter = 0;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvResult = (TextView) findViewById(R.id.txvResult);
        cBYes = (CheckBox) findViewById(R.id.checkBoxYes);
        cBNo = (CheckBox) findViewById(R.id.checkBoxNo);

        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 12);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 13);
            }
        }

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(MainActivity.this);

        getVoiceInput("Please say launch camera");

    }

    public void getVoiceInput(final String textToSpeak){
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                final Context context = getApplicationContext();
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    String toSpeak = textToSpeak;
                    t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {
                            //getInput();
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Handler mainHandler = new Handler(context.getMainLooper());
                            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                            if (intent.resolveActivity(getPackageManager()) != null){

                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("Inside run" , "");
                                        speech.startListening(intent);
                                    } // This is your code
                                };
                                mainHandler.post(myRunnable);
                                //startActivityForResult(intent, 10);
                            } else {
                                Toast.makeText(context, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                            }
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

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) {
            Log.i("onResults", result);
            text += result + "\n";
        }

        Log.i("text", text);
        if(text.contains("camera") ) {
            Log.i("in camera intent", "camera");
            new Thread(new Runnable() {
                public void run() {
                // a potentially time consuming task
                Intent in = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(in);
                }
            }).start();
        }
        else{
            getVoiceInput("Please say again I can't understand that");
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
