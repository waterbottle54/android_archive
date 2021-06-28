package com.good.paintboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.material.slider.Slider;

public class PenWidthDialog extends AppCompatActivity {

    public static final String EXTRA_PEN_WIDTH = "penWidth";

    public interface OnWidthChangedListener {
        void onWidthChanged(int width);
    }

    public static OnWidthChangedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pen_width_dialog);

        // 타이틀 설정
        setTitle("굵기 설정");

        // 슬라이더 현재값 초기화
        Slider slider = findViewById(R.id.slider);
        slider.setValue(getIntent().getIntExtra(EXTRA_PEN_WIDTH, 3));

        // 슬라이더 리스너 설정
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                // 슬라이더 값이 바뀔때마다 펜 굵기 변경 리스너 호출.
                if (listener != null) {
                    listener.onWidthChanged((int)value);
                }
            }
        });

        // 닫기 버튼 리스너 설정
        Button closeButton = findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}