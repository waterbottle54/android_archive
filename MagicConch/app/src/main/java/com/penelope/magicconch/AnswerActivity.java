package com.penelope.magicconch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class AnswerActivity extends AppCompatActivity {

    public static final String EXTRA_ANSWER = "com.penelope.magicconch.answer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        String answer = getIntent().getStringExtra(EXTRA_ANSWER);
        if (answer == null) {
            finish();
        }

        TextView answerTextView = findViewById(R.id.textViewAnswer);
        answerTextView.setText(answer);

        Button questionAgainButton = findViewById(R.id.buttonQuestionAgain);
        Button exitButton = findViewById(R.id.buttonExit);

        questionAgainButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
        });
        exitButton.setOnClickListener(v -> exitApp());
    }

    private void exitApp() {
        Intent intent = new Intent();
        intent.putExtra(QuestionActivity.EXTRA_EXIT, true);
        setResult(RESULT_OK, intent);
        finish();
    }

}



