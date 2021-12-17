package com.penelope.twinalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView alarm1TextView;
    private TextView alarm2TextView;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 초기화
        root = findViewById(R.id.root);
        alarm1TextView = findViewById(R.id.textViewAlarm1);
        alarm2TextView = findViewById(R.id.textViewAlarm2);

        Button alarm1SettingButton = findViewById(R.id.buttonAlarm1Setting);
        Button alarm2SettingButton = findViewById(R.id.buttonAlarm2Setting);
        Button alarm1OnButton = findViewById(R.id.buttonAlarm1On);
        Button alarm2OnButton = findViewById(R.id.buttonAlarm2On);
        Button alarm1OffButton = findViewById(R.id.buttonAlarm1Off);
        Button alarm2OffButton = findViewById(R.id.buttonAlarm2Off);

        alarm1SettingButton.setOnClickListener(this);
        alarm2SettingButton.setOnClickListener(this);
        alarm1OnButton.setOnClickListener(this);
        alarm2OnButton.setOnClickListener(this);
        alarm1OffButton.setOnClickListener(this);
        alarm2OffButton.setOnClickListener(this);

        updateUI();
    }

    private void updateUI() {

        // 알람 시간 / 활성화 여부에 따라 TextView 업데이트

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String setting1 = pref.getString("setting1", null);
        String setting2 = pref.getString("setting2", null);
        boolean alarm1 = pref.getBoolean("alarm1", true);
        boolean alarm2 = pref.getBoolean("alarm2", true);

        if (setting1 == null) {
            alarm1TextView.setText("알람이 설정되어 있지 않습니다");
        } else {
            String strAlarm1 = setting1 + "(" + (alarm1 ? "ON" : "OFF") + ")";
            alarm1TextView.setText(strAlarm1);
        }

        if (setting2 == null) {
            alarm2TextView.setText("알람이 설정되어 있지 않습니다");
        } else {
            String strAlarm2 = setting2 + "(" + (alarm2 ? "ON" : "OFF") + ")";
            alarm2TextView.setText(strAlarm2);
        }
    }

    // 버튼 클릭 처리

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.buttonAlarm1Setting) {
            promptAlarmTime(1);
        } else if (id == R.id.buttonAlarm2Setting) {
            promptAlarmTime(2);
        } else if (id == R.id.buttonAlarm1On) {
            enableAlarm(1, true);
        } else if (id == R.id.buttonAlarm1Off) {
            enableAlarm(1, false);
        } else if (id == R.id.buttonAlarm2On) {
            enableAlarm(2, true);
        } else if (id == R.id.buttonAlarm2Off) {
            enableAlarm(2, false);
        }
    }

    private void promptAlarmTime(int alarmIndex) {

        // 현재 시각 획득
        LocalDateTime ldt = LocalDateTime.now();

        // 시간 대화상자 설정
        TimePickerDialog dialog = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            setAlarmTime(alarmIndex, hour, minute);
            String strSnack = String.format(Locale.getDefault(),
                    "%s %d시 %d분에 알람이 설정되었습니다",
                    hour >= 12 ? "오후" : "오전",
                    hour % 12 == 0 ? 12 : hour % 12,
                    minute
            );
            Snackbar.make(root, strSnack, Snackbar.LENGTH_SHORT).show();
        }, ldt.getHour(), ldt.getMinute(), false);

        dialog.show();
    }

    private void setAlarmTime(int alarmIndex, int hour, int minute) {

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 현재 epoch millis 획득
        LocalDateTime ldt = LocalDateTime.now().withHour(hour).withMinute(minute);
        long millis = 1000 * ldt.toEpochSecond(ZoneOffset.ofHours(9));

        // AlarmReceiver 를 타겟으로 한 Broadcast Intent 작성
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarmIndex", alarmIndex);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, alarmIndex, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 매일 반복 알람 설정
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, millis,
                AlarmManager., pendingIntent);

        // 프레퍼런스에 알람 설정 (시간) 기록
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        String strSetting = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        editor.putString("setting" + alarmIndex, strSetting).apply();

        updateUI();
    }

    private void enableAlarm(int alarmIndex, boolean enabled) {

        // 프레퍼런스에 알람 활성화 여부 변경
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("alarm" + alarmIndex, enabled).apply();

        Snackbar.make(root,
                "알람" + alarmIndex + " " + (enabled ? "ON" : "OFF"),
                Snackbar.LENGTH_SHORT)
                .show();

        updateUI();
    }

}




