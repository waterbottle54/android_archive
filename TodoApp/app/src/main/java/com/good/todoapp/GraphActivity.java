package com.good.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.good.todoapp.components.GraphView;

import java.util.Locale;

public class GraphActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // 완료율을 구한다.
        double completedRate = getIntent().getDoubleExtra(
                MainActivity.EXTRA_COMPLETED_RATE, 0);

        // 완료율을 그래프의 value 로 설정한다
        GraphView graph = findViewById(R.id.graph);
        double zValue = graph.getZRange() * (-1 + 2 * completedRate) / 2;
        graph.setZValue( zValue, true);

        // 완료율을 텍스트뷰에 표시한다.
        TextView rateText = findViewById(R.id.txt_completed_rate);
        String strRate = String.format(Locale.getDefault(),
                "완료율: %.0f %%", completedRate * 100);
        rateText.setText(strRate);

        // 버튼을 초기화한다.
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_cancel) {
            // 액티비티를 끝낸다.
            finish();
        }
    }
}