package com.example.team.semicolon_19;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        SharedPreferences sp = getSharedPreferences("SharedQuestionNo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("questionNumber",0);
        editor.putString("hand","left");
        editor.commit();

        Intent in = new Intent(SampleActivity.this, InstructionsActivity.class);
        startActivity(in);
    }
}
