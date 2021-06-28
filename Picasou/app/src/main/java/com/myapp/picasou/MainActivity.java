package com.myapp.picasou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 커스텀 캔버스 뷰
    private CanvasView mCanvasView;

    // 버튼
    private Button mLineButton;
    private Button mRectButton;
    private Button mCircleButton;
    private Button mCurveButton;
    private Button mEraseButton;
    private Button mClearButton;
    private Button mEmbossButton;
    private Button mBlurButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();

        mCanvasView = findViewById(R.id.canvas_view);
        setPaintMode(mCanvasView.getPaintMode());
        setFilterMode(mCanvasView.getFilterMode());
        setPenColorRes(R.color.color_palette_black);
        setPenWidth(4);
    }

    // 버튼에 리스너 설정

    private void initButtons() {

        mLineButton = findViewById(R.id.btn_line);
        mRectButton = findViewById(R.id.btn_rect);
        mCircleButton = findViewById(R.id.btn_circle);
        mCurveButton = findViewById(R.id.btn_curve);
        mEraseButton = findViewById(R.id.btn_erase);
        mClearButton = findViewById(R.id.btn_clear);
        mEmbossButton = findViewById(R.id.btn_emboss);
        mBlurButton = findViewById(R.id.btn_blur);

        mLineButton.setOnClickListener(this);
        mRectButton.setOnClickListener(this);
        mCircleButton.setOnClickListener(this);
        mCurveButton.setOnClickListener(this);
        mEraseButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mEmbossButton.setOnClickListener(this);
        mBlurButton.setOnClickListener(this);
    }

    // 버튼 클릭 처리

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_line:
                setPaintMode(CanvasView.PAINT_MODE_LINE);
                break;
            case R.id.btn_rect:
                setPaintMode(CanvasView.PAINT_MODE_RECT);
                break;
            case R.id.btn_circle:
                setPaintMode(CanvasView.PAINT_MODE_CIRCLE);
                break;
            case R.id.btn_curve:
                setPaintMode(CanvasView.PAINT_MODE_CURVE);
                break;
            case R.id.btn_erase:
                setPaintMode(CanvasView.PAINT_MODE_ERASE);
                break;
            case R.id.btn_clear:
                tryClear();
                break;
            case R.id.btn_emboss:
                setFilterMode(CanvasView.FILTER_EMBOSS);
                break;
            case R.id.btn_blur:
                setFilterMode(CanvasView.FILTER_BLUR);
                break;
        }
    }

    // 캔버스 설정

    private void setPaintMode(int paintMode) {

        mCanvasView.setPaintMode(paintMode);

        // 페인트 모드 버튼 색상 업데이트

        int colorNormal = getResources().getColor(R.color.color_button_normal);
        int colorActivated = getResources().getColor(R.color.color_button_activated);

        mLineButton.setTextColor(colorNormal);
        mRectButton.setTextColor(colorNormal);
        mCircleButton.setTextColor(colorNormal);
        mCurveButton.setTextColor(colorNormal);
        mEraseButton.setTextColor(colorNormal);

        switch (mCanvasView.getPaintMode()) {
            case CanvasView.PAINT_MODE_LINE:
                mLineButton.setTextColor(colorActivated);
                break;
            case CanvasView.PAINT_MODE_RECT:
                mRectButton.setTextColor(colorActivated);
                break;
            case CanvasView.PAINT_MODE_CIRCLE:
                mCircleButton.setTextColor(colorActivated);
                break;
            case CanvasView.PAINT_MODE_CURVE:
                mCurveButton.setTextColor(colorActivated);
                break;
            case CanvasView.PAINT_MODE_ERASE:
                mEraseButton.setTextColor(colorActivated);
                break;
        }
    }

    private void setFilterMode(int filterMode) {

        int lastFilterMode = mCanvasView.getFilterMode();

        if (filterMode == lastFilterMode) {
            mCanvasView.setFilterMode(CanvasView.FILTER_NONE);
        } else {
            mCanvasView.setFilterMode(filterMode);
        }

        // 필터 모드 버튼 색상 업데이트

        int colorNormal = getResources().getColor(R.color.color_button_normal);
        int colorActivated = getResources().getColor(R.color.color_button_activated);

        mEmbossButton.setTextColor(colorNormal);
        mBlurButton.setTextColor(colorNormal);

        switch (mCanvasView.getFilterMode()) {
            case CanvasView.FILTER_EMBOSS:
                mEmbossButton.setTextColor(colorActivated);
                break;
            case CanvasView.FILTER_BLUR:
                mBlurButton.setTextColor(colorActivated);
                break;
        }
    }

    private void tryClear() {

        // 대화상자로 "다 지울지" 를 사용자에게 물어본 후 캔버스를 지운다.

        new AlertDialog.Builder(this)
                .setTitle("Do you want to clear?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCanvasView.clear();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void setPenWidth(int widthInDP) {

        // DP 를 픽셀로 바꾸어서 캔버스 펜 굵기 설정

        float density = getResources().getDisplayMetrics().density;
        mCanvasView.setPenWidth(widthInDP * density);
    }

    private void setPenColorRes(int colorRes) {

        // 캔버스 펜 색상 설정

        Resources res = getResources();
        mCanvasView.setPenColor(res.getColor(colorRes));
    }

    // 옵션 메뉴 생성 : 리소스 사용

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // 옵션 메뉴 버튼 클릭 처리

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_weight:
                showWeightDialog();
                break;
            case R.id.btn_color:
                showColorDialog();
                break;
            case R.id.btn_emboss:
                setFilterMode(CanvasView.FILTER_EMBOSS);
                break;
            case R.id.btn_blur:
                setFilterMode(CanvasView.FILTER_BLUR);
                break;
            default:
                return false;
        }
        return true;
    }

    // 옵션 메뉴 체크 상태 업데이트

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        for (int i = 0; i < menu.size(); i++) {

            MenuItem item = menu.getItem(i);
            int filterMode = mCanvasView.getFilterMode();

            switch (item.getItemId()) {
                case R.id.btn_emboss:
                    item.setChecked(filterMode == CanvasView.FILTER_EMBOSS);
                    break;
                case R.id.btn_blur:
                    item.setChecked(filterMode == CanvasView.FILTER_BLUR);
                    break;
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // 굵기 대화상자 띄우기

    private void showWeightDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_PicasouDialog)
                .create();

        // 굵기 설정 레이아웃의 뷰에 리스너 설정
        View weightView = View.inflate(this, R.layout.weight_view, null);
        View weight2View = weightView.findViewById(R.id.view_weight_2);
        View weight4View = weightView.findViewById(R.id.view_weight_4);
        View weight6View = weightView.findViewById(R.id.view_weight_6);
        View weight8View = weightView.findViewById(R.id.view_weight_8);
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.view_weight_2:
                        setPenWidth(2);
                        break;
                    case R.id.view_weight_4:
                        setPenWidth(4);
                        break;
                    case R.id.view_weight_6:
                        setPenWidth(6);
                        break;
                    case R.id.view_weight_8:
                        setPenWidth(8);
                        break;
                    default: return;
                }
                alertDialog.dismiss();
            }
        };
        weight2View.setOnClickListener(listener);
        weight4View.setOnClickListener(listener);
        weight6View.setOnClickListener(listener);
        weight8View.setOnClickListener(listener);

        // 색상 대화상자에 뷰 설정 후 띄우기
        alertDialog.setView(weightView);
        alertDialog.show();
    }

    // 색상 대화상자 띄우기

    private void showColorDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_PicasouDialog)
                .create();

        // 색상 설정 레이아웃을 생성하고, 뷰에 리스너 설정
        View colorView = View.inflate(this, R.layout.color_view, null);
        TextView blackText = colorView.findViewById(R.id.txt_black);
        TextView blueText = colorView.findViewById(R.id.txt_blue);
        TextView emeraldText = colorView.findViewById(R.id.txt_emerald);
        TextView grayText = colorView.findViewById(R.id.txt_gray);
        TextView greenText = colorView.findViewById(R.id.txt_green);
        TextView pinkText = colorView.findViewById(R.id.txt_pink);
        TextView redText = colorView.findViewById(R.id.txt_red);
        TextView yellowText = colorView.findViewById(R.id.txt_yellow);
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txt_black:
                        setPenColorRes(R.color.color_palette_black);
                        break;
                    case R.id.txt_blue:
                        setPenColorRes(R.color.color_palette_blue);
                        break;
                    case R.id.txt_emerald:
                        setPenColorRes(R.color.color_palette_emerald);
                        break;
                    case R.id.txt_gray:
                        setPenColorRes(R.color.color_palette_gray);
                        break;
                    case R.id.txt_green:
                        setPenColorRes(R.color.color_palette_green);
                        break;
                    case R.id.txt_pink:
                        setPenColorRes(R.color.color_palette_pink);
                        break;
                    case R.id.txt_red:
                        setPenColorRes(R.color.color_palette_red);
                        break;
                    case R.id.txt_yellow:
                        setPenColorRes(R.color.color_palette_yellow);
                        break;
                    default: return;
                }
                alertDialog.dismiss();
            }
        };
        blackText.setOnClickListener(listener);
        blueText.setOnClickListener(listener);
        emeraldText.setOnClickListener(listener);
        grayText.setOnClickListener(listener);
        greenText.setOnClickListener(listener);
        pinkText.setOnClickListener(listener);
        redText.setOnClickListener(listener);
        yellowText.setOnClickListener(listener);

        // 색상 대화상자에 뷰 설정 후 띄우기
        alertDialog.setView(colorView);
        alertDialog.show();
    }

}


