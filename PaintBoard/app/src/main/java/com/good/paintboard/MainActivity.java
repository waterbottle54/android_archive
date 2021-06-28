package com.good.paintboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 페인트 보드 객체
    private BestPaintBoard paintBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintBoard = findViewById(R.id.board);

        // 텍스트 뷰 초기화
        updateTextViews();

        // UI 버튼에 클릭 리스너 설정하기.
        Button colorBtn = findViewById(R.id.btn_color);
        Button penBtn = findViewById(R.id.btn_pen);
        Button eraseBtn = findViewById(R.id.btn_eraser);
        Button undoBtn = findViewById(R.id.btn_undo);

        // 색상 버튼 클릭 : 펜 컬러 설정 다이얼로그를 띄운다.
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 컬러 선택 시 리스너 설정
                ColorPaletteDialog.listener = new ColorPaletteDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        // 페인트 보드의 패스 색상을 변경한다.
                        paintBoard.setPathStyle(color, paintBoard.getPathWidth());
                        updateTextViews();
                    }
                };

                Intent intent = new Intent(getApplicationContext(),
                        ColorPaletteDialog.class);

                startActivity(intent);
            }
        });

        // 펜 버튼 클릭 : 펜 굵기 설정 다이얼로그를 띄운다.
        penBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 펜 굵기 변경 리스너 설정
                PenWidthDialog.listener = new PenWidthDialog.OnWidthChangedListener() {
                    @Override
                    public void onWidthChanged(int width) {
                        // 페인트 보드의 펜 굵기를 변경한다.
                        paintBoard.setPathStyle(paintBoard.getPathColor(), width);
                        updateTextViews();
                    }
                };

                Intent intent = new Intent(getApplicationContext(),
                        PenWidthDialog.class);

                // 현재 펜 굵기 전달
                intent.putExtra(PenWidthDialog.EXTRA_PEN_WIDTH, paintBoard.getPathWidth());

                startActivity(intent);
            }
        });

        // Undo 버튼 클릭 : 페인트 보드에서 Undo 실행
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Undo 를 실행한다.
                boolean result = paintBoard.undo();
                if (!result) {
                    // Undo 기록 없음 메세지.
                    Toast.makeText(MainActivity.this,
                            "기록이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Erase 버튼 클릭 : 페인트 보드에서 Erase 실행
        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Erase 를 실행한다.
                boolean result = paintBoard.erase();
                if (!result) {
                    // 보드 empty 메세지.
                    Toast.makeText(MainActivity.this,
                            "지울 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateTextViews() {

        // 색상, 굵기 표시 텍스트 뷰 업데이트
        TextView colorTextView = findViewById(R.id.txt_current_color);
        TextView sizeTextView = findViewById(R.id.txt_current_size);

        colorTextView.setBackgroundColor(paintBoard.getPathColor());
        sizeTextView.setText(String.format(Locale.getDefault(),
                "Size: %d", paintBoard.getPathWidth()));
    }

}