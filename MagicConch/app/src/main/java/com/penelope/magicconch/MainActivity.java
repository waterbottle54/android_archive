package com.penelope.magicconch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> questionActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        boolean exit = data.getBooleanExtra(QuestionActivity.EXTRA_EXIT, false);
                        if (exit) {
                            finish();
                        }
                    }
                }
        );

        Button startButton = findViewById(R.id.buttonStart);
        startButton.setOnClickListener(v -> startQuestionActivity());
    }

    private void startQuestionActivity() {
        Intent intent = new Intent(this, QuestionActivity.class);
        questionActivityLauncher.launch(intent);
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
    }

}