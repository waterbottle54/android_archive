package com.good.paintboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.good.paintboard.adapters.ColorDataAdapter;

import java.util.ArrayList;
import java.util.List;

public class ColorPaletteDialog extends AppCompatActivity {

    // 컬러 선택시 호출될 인터페이스
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public static OnColorSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_palette_dialog);

        // 타이틀 설정
        setTitle("색상 선택");

        // 그리드뷰 초기화
        GridView gridView = findViewById(R.id.color_grid);

        int numColumns = 7;
        int spacing = 4;
        gridView.setNumColumns(numColumns);
        gridView.setHorizontalSpacing(spacing);
        gridView.setVerticalSpacing(spacing);

        // 그리드 넓이 계산
        int gridViewWidth = gridView.getLayoutParams().width;
        int gridWidth = (int)(gridViewWidth / numColumns) - spacing;

        // 그리드뷰 어댑터 초기화
        ColorDataAdapter adapter = new ColorDataAdapter(getColorList(), gridWidth, listener);
        gridView.setAdapter(adapter);

        // 닫기 버튼 리스너 설정
        Button closeButton = findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private List<Integer> getColorList() {

        List<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(0, 0, 0));
        colors.add(Color.rgb(0,0,127));
        colors.add(Color.rgb(0,0,254));
        colors.add(Color.rgb(0, 127, 0));
        colors.add(Color.rgb(0, 127, 127));
        colors.add(Color.rgb(0, 255, 0));
        colors.add(Color.rgb(0, 254, 126));

        colors.add(Color.rgb(0, 254, 254));
        colors.add(Color.rgb(127, 0, 127));
        colors.add(Color.rgb(126, 0, 254));
        colors.add(Color.rgb(127, 127, 0));
        colors.add(Color.rgb(127, 127, 127));
        colors.add(Color.rgb(255, 0, 0));
        colors.add(Color.rgb(255, 0, 127));

        colors.add(Color.rgb(255, 0, 255));
        colors.add(Color.rgb(255, 127, 0));
        colors.add(Color.rgb(255, 127, 127));
        colors.add(Color.rgb(255, 127, 255));
        colors.add(Color.rgb(255, 255, 0));
        colors.add(Color.rgb(255, 255, 127));
        colors.add(Color.rgb(255, 255, 255));

        return colors;
    }

}