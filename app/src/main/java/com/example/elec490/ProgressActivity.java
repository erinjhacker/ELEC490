package com.example.elec490;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class ProgressActivity extends AppCompatActivity {

    private static final String TAG = "ProgressActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_progress);

        Button startButton = findViewById(R.id.begin2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProgressActivity.this, MainActivity.class));
            }
        });

    }
}
