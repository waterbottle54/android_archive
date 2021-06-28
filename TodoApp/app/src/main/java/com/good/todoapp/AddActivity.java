package com.good.todoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.good.todoapp.models.Duty;
import com.good.todoapp.helpers.DatabaseHelper;

import java.time.LocalDate;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_DATE_ACTIVITY = 100;
    public static final String EXTRA_YEAR = "date_year";
    public static final String EXTRA_MONTH = "date_month";
    public static final String EXTRA_DAY_OF_MONTH = "date_day_of_month";

    private EditText mDutyNameEdit;
    private RadioGroup mDutyImportanceRadio;
    private TextView mDutyDateText;
    private LocalDate mSelectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 모든 자식 뷰를 초기화한다.
        mDutyNameEdit = findViewById(R.id.edit_duty_name);
        mDutyImportanceRadio = findViewById(R.id.radio_duty_importance);
        mDutyDateText = findViewById(R.id.txt_duty_date);

        Button selectDateButton = findViewById(R.id.btn_select_duty_date);
        Button addButton = findViewById(R.id.btn_add_duty);
        Button cancelButton = findViewById(R.id.btn_cancel);
        selectDateButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DATE_ACTIVITY && resultCode == RESULT_OK) {

            // 날짜(Date) 액티비티가 데이터를 리턴한 경우,
            // 선택된 날짜 데이터를 data 에서 구한다.
            int year = data.getIntExtra(EXTRA_YEAR, 0);
            int month = data.getIntExtra(EXTRA_MONTH, 0);
            int dayOfMonth = data.getIntExtra(EXTRA_DAY_OF_MONTH, 0);
            mSelectedDate = LocalDate.of(year, month + 1, dayOfMonth);

            // 날짜 데이터를 이용해 날짜 문자열을 만들기
            String strDate = String.format(Locale.getDefault(),
                    "%d년 %d월 %d일", year, month + 1, dayOfMonth);

            // 날짜 텍스트뷰 갱신
            mDutyDateText.setText(strDate);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_select_duty_date:
                // 날짜 액티비티 시작
                startDateActivity();
                break;
            case R.id.btn_add_duty:
                // 현재 작성한 할일을 데이터베이스에 저장
                saveDuty();
                break;
            case R.id.btn_cancel:
                // 이 액티비티를 캔슬하고 끝낸다.
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private void startDateActivity() {

        // 날짜 액티비티 시작하기.
        Intent intent = new Intent(this, DateActivity.class);
        startActivityForResult(intent, REQUEST_DATE_ACTIVITY);
    }

    private void saveDuty() {

        // 유저가 입력한 데이터를 받는다.
        String strDutyName = mDutyNameEdit.getText().toString().trim();
        boolean isSpecial = (mDutyImportanceRadio.getCheckedRadioButtonId() == R.id.btn_special_duty);

        // 할일 이름이 공백이면 경고를 띄운다.
        if (strDutyName.isEmpty()) {
            Toast.makeText(this, "할 일의 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 선택된 날짜가 없으면 경고를 띄운다.
        if (mSelectedDate == null) {
            Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 할일 객체를 만들고, 데이터베이스에 추가한다.
        Duty duty = new Duty(strDutyName, false, mSelectedDate, isSpecial);
        DatabaseHelper.getInstance(this).addDuty(duty);

        // 액티비티를 끝낸다.
        setResult(RESULT_OK);
        finish();
    }

}