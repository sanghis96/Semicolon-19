package com.example.team.semicolon_19;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class InstructionsActivity extends AppCompatActivity {

    private TextView sampleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        sampleTextView = (TextView) findViewById(R.id.sampleText);



    }


}
