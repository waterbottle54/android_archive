package com.good.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class DateActivity extends AppCompatActivity implements View.OnClickListener {

    // 데이트 피커
    private DatePicker mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        // 자식 뷰들을 초기화한다.
        mDatePicker = findViewById(R.id.date_picker);

        Button selectDateButton = findViewById(R.id.btn_select_duty_date);
        Button cancelButton = findViewById(R.id.btn_cancel);
        selectDateButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_select_duty_date:
                // 현재 선택된 날짜가 없으면 경고.
                if (isSelectedDatePast()) {
                    Toast.makeText(this,
                            "과거를 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 현재 선택된 날짜를 구한다.
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int dayOfMonth = mDatePicker.getDayOfMonth();

                // 그 날짜를 데이터로 설정하고 액티비티를 끝낸다.
                Intent data = new Intent();
                data.putExtra(AddActivity.EXTRA_YEAR, year);
                data.putExtra(AddActivity.EXTRA_MONTH, month);
                data.putExtra(AddActivity.EXTRA_DAY_OF_MONTH, dayOfMonth);
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.btn_cancel:
                // 액티비티를 취소한다.
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private boolean isSelectedDatePast() {

        // 선택된 날짜가 현재 날짜보다 과거인지를 판단한다.
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int dayOfMonth = mDatePicker.getDayOfMonth();
        long epochDay = LocalDate.of(year, month + 1, dayOfMonth).toEpochDay();
        long epochDayNow = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().toEpochDay();

        return (epochDay < epochDayNow);
    }


}


