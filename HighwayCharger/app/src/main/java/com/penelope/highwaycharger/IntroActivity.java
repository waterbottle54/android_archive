package com.penelope.highwaycharger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 조회 버튼 클릭 시 메인 액티비티 (조회 화면) 을 시작한다
        Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // 종료 버튼 클릭 시 앱을 종료한다
        Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(v -> finish());
    }
}