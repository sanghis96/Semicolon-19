package com.example.team.semicolon_19;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Locale;

public class InstructionsActivity extends AppCompatActivity implements RecognitionListener {


    TextToSpeech t1;
    public SpeechRecognizer speech;
    public Intent intent;
    public String[] question;
    public int questionNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

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
        speech.setRecognitionListener(InstructionsActivity.this);

        getVoiceInput("Hi! Let's begin with your assessment.", false);
        InputStream inputStream = getResources().openRawResource(R.raw.questions_list);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String questions = byteArrayOutputStream.toString();
        question = questions.split("\n");

    }

    public void getVoiceInput(final String textToSpeak, final Boolean isQuestion){
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
                            if(isQuestion) {
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
                            } else {
                                if(questionNumber < question.length) {
                                    getVoiceInput(question[questionNumber++], true);
                                } else {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            // a potentially time consuming task
                                            Intent in = new Intent(InstructionsActivity.this, CameraActivity.class);
                                            startActivity(in);
                                        }
                                    }).start();
                                }
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
        System.out.println("Samarth"+text);
        if(questionNumber < question.length) {
            getVoiceInput(question[questionNumber++], true);
        } else {
            getVoiceInput("Now let's take a picture of your hand.", false);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
