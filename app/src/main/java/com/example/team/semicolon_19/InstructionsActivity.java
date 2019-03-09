package com.example.team.semicolon_19;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class InstructionsActivity extends AppCompatActivity implements RecognitionListener {

    TextToSpeech t1;
    public SpeechRecognizer speech;
    public Intent intent;
    public String[] questionList;
    public int questionNumber;
    public String[] conditionList;
    List<dataWrapper> dw = new ArrayList<dataWrapper>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 10);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 10);
            }
        }

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(InstructionsActivity.this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SharedQuestionNo", MODE_PRIVATE);
        questionNumber = pref.getInt("questionNumber",0);

        String text = readFile(R.raw.questions_set);
        Log.d("Samarth",text);

        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour>0 && hour<12) {
            text = text.replace("{Time}","Morning");
        } else if(hour>=12 && hour<=18) {
            text = text.replace("{Time}","Afternoon");
        } else {
            text = text.replace("{Time}","Evening");
        }

        text = text.replace("{Patient_Name}","Samarth");
        text = text.replace("{activity_name}","Swimming");

        text = text.replace("{hand}",pref.getString("hand","left"));

        text = text.replace("{previous_data}","6");
        Log.d("Samarth",text);
        questionList = text.split("\n");

        String conditions = readFile(R.raw.condition_set);
        conditionList = conditions.split("\n");

        Boolean isQuestion = Boolean.valueOf(questionList[questionNumber].substring(questionList[questionNumber].indexOf("@")));
        Log.d("Samarth", String.valueOf(isQuestion));
        String textToSpeak = questionList[questionNumber].substring(0,questionList[questionNumber].indexOf("@"));
        Log.d("Samarth",textToSpeak);

        getVoiceInput(textToSpeak,isQuestion);

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
                                Log.d("Samarth","Question hai");
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
                                        }
                                    };
                                    mainHandler.post(myRunnable);
                                } else {
                                    Toast.makeText(context, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                questionNumber = Integer.parseInt(conditionList[questionNumber].split("#")[1].trim().split(":")[0].trim());;
                                if(questionNumber < questionList.length) {
                                    String txtToSpeak = questionList[questionNumber].split("@")[0].trim();
                                    String value = questionList[questionNumber].split("@")[1].trim();
                                    Boolean isQues = Boolean.valueOf(value);

                                    Log.d("Samarth",txtToSpeak);
                                    Log.d("Samarth",""+isQues);

                                    getVoiceInput(txtToSpeak, isQues);
                                } else {
                                    Log.d("Samarth", dw.toString());
                                    SharedPreferences sp = getSharedPreferences("sharedVoiceData",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit().putString("voiceData",dw.toString());
                                    editor.commit();
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
        dw.add(new dataWrapper(questionNumber,text.split("\n")[0]));

        if(questionNumber < questionList.length) {
            int nextQuestionYes = Integer.parseInt(conditionList[questionNumber].split("#")[1].trim().split(":")[0].trim());
            int nextQuestionNo = Integer.parseInt(conditionList[questionNumber].split("#")[1].trim().split(":")[1].trim());

            String txtToSpeak;
            String value;

            if(text.split("\n")[0].equalsIgnoreCase("No")) {
                txtToSpeak = questionList[nextQuestionNo].split("@")[0].trim();
                value = questionList[nextQuestionNo].split("@")[1].trim();
                questionNumber = nextQuestionNo;
            } else {
                txtToSpeak = questionList[nextQuestionYes].split("@")[0].trim();
                value = questionList[nextQuestionYes].split("@")[1].trim();
                questionNumber = nextQuestionYes;
            }
            Boolean isQues = Boolean.valueOf(value);

            Log.d("Samarth",txtToSpeak);
            Log.d("Samarth",""+isQues);

            getVoiceInput(txtToSpeak, isQues);
        } else {
            //getVoiceInput("Now let's take a picture of your hand.", false);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private String readFile(final int fileId) {
        InputStream inputStream = getResources().openRawResource(fileId);
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
        String fileText = byteArrayOutputStream.toString();
        //String[] lineWiseText = fileText.split("\n");
        return fileText;
    }
}
