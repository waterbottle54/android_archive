package com.holy.exercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.exercise.helpers.SQLiteHelper;
import com.holy.exercise.models.Exercise;
import com.holy.exercise.models.Record;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Locale;


public class ExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_EXERCISE = "com.holy.exercise.exercise";
    public static final int TIMER_STOP = 1;
    public static final int TIMER_RUNNING = 2;
    public static final int TIMER_PAUSE = 3;
    public static final int TIMER_MAX_MILLISECONDS = 3599999;

    private Exercise mExercise;

    private int mTimerMilliseconds = 0;
    private int mTimerStatus = TIMER_STOP;
    private TimerThread mTimerThread;
    private TimerHandler mTimerHandler;
    private int mTimerStartMilliseconds;
    private int mRepetition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // 전송된 운동 정보 획득
        mExercise = (Exercise) getIntent().getSerializableExtra(EXTRA_EXERCISE);
        if (mExercise == null) {
            Toast.makeText(this, "데이터를 조회할 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI 초기화
        updateExerciseUI();
        updateRepetitionUI();

        // 클릭 리스너 설정
        ImageButton initTimeIButton = findViewById(R.id.ibtnInitTime);
        ImageButton plus10SecIButton = findViewById(R.id.ibtnPlus10Sec);
        ImageButton plus30SecIButton = findViewById(R.id.ibtnPlus30Sec);
        TextView startText = findViewById(R.id.txtStart);
        TextView pauseText = findViewById(R.id.txtPause);
        TextView stopText = findViewById(R.id.txtStop);
        initTimeIButton.setOnClickListener(this);
        plus10SecIButton.setOnClickListener(this);
        plus30SecIButton.setOnClickListener(this);
        startText.setOnClickListener(this);
        pauseText.setOnClickListener(this);
        stopText.setOnClickListener(this);

        mTimerHandler = new TimerHandler(this);
    }

    // 운동 정보 UI 초기화

    private void updateExerciseUI() {

        TextView titleText = findViewById(R.id.txtExerciseTitle);
        TextView typeText = findViewById(R.id.txtExerciseType);
        TextView descriptionText = findViewById(R.id.txtExerciseDescription);
        ImageView imageView = findViewById(R.id.imgExercise);

        titleText.setText(mExercise.getTitle());
        descriptionText.setText(mExercise.getDescription());
        imageView.setImageResource(mExercise.getImgRes());

        String strType = "";
        switch (mExercise.getType()) {
            case Exercise.TYPE_UPPER: strType = "상부"; break;
            case Exercise.TYPE_MIDDLE: strType = "중부"; break;
            case Exercise.TYPE_LOWER: strType = "하부"; break;
        }
        typeText.setText(strType);
    }

    // 클릭 처리

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.ibtnInitTime) {
            initSeconds();
        } else if (id == R.id.ibtnPlus10Sec) {
            increaseRemainingSeconds(10);
        } else if (id == R.id.ibtnPlus30Sec) {
            increaseRemainingSeconds(30);
        } else if (id == R.id.txtStart) {
            startTimer();
        } else if (id == R.id.txtPause) {
            pauseResumeTimer();
        } else if (id == R.id.txtStop) {
            stopTimer();
        }
    }

    // 타이머 조작

    private void initSeconds() {
        mTimerMilliseconds = 0;
        updateTimerUI();
    }

    private void increaseRemainingSeconds(int sec) {

        mTimerMilliseconds += sec * 1000;
        if (mTimerMilliseconds > TIMER_MAX_MILLISECONDS) {
            mTimerMilliseconds = TIMER_MAX_MILLISECONDS;
        }
        updateTimerUI();
    }

    private void startTimer() {
        if (mTimerStatus == TIMER_STOP && mTimerMilliseconds > 0) {
            mTimerStatus = TIMER_RUNNING;
            mTimerThread = new TimerThread();
            mTimerThread.setDaemon(true);
            mTimerThread.start();
            mTimerStartMilliseconds = mTimerMilliseconds;
            updateTimerUI();
        }
    }

    private void pauseResumeTimer() {
        if (mTimerStatus == TIMER_RUNNING) {
            mTimerStatus = TIMER_PAUSE;
            updateTimerUI();
        } else if (mTimerStatus == TIMER_PAUSE) {
            mTimerStatus = TIMER_RUNNING;
            updateTimerUI();
        }
    }

    private void stopTimer() {
        if (mTimerStatus != TIMER_STOP) {
            mTimerMilliseconds = 0;
            mTimerStatus = TIMER_STOP;
            updateTimerUI();
            try {
                mTimerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 타이머 UI 업데이트

    private void updateTimerUI() {

        TextView timeText = findViewById(R.id.txtTimeRemaining);
        int seconds = mTimerMilliseconds / 1000;
        String strTime = String.format(Locale.getDefault(),
                "%02d:%02d", seconds / 60, seconds % 60);
        timeText.setText(strTime);

        TextView startText = findViewById(R.id.txtStart);
        TextView pauseText = findViewById(R.id.txtPause);
        TextView stopText = findViewById(R.id.txtStop);

        if (mTimerMilliseconds == 0) {
            startText.setVisibility(View.VISIBLE);
            pauseText.setVisibility(View.INVISIBLE);
            stopText.setVisibility(View.INVISIBLE);
            startText.setTextColor(Color.GRAY);
        } else {
            if (mTimerStatus == TIMER_STOP) {
                startText.setTextColor(Color.BLUE);
            } else if (mTimerStatus == TIMER_RUNNING) {
                pauseText.setVisibility(View.VISIBLE);
                stopText.setVisibility(View.VISIBLE);
                startText.setVisibility(View.INVISIBLE);
                pauseText.setText("일시정지");
                pauseText.setTextColor(Color.BLACK);
            } else if (mTimerStatus == TIMER_PAUSE) {
                pauseText.setText("계속");
                pauseText.setTextColor(Color.BLUE);
            }
        }
    }

    // 반복횟수 증가

    private void increaseRepetition() {

        mRepetition++;
        updateRepetitionUI();

        // DB 업데이트
        Record record = new Record(
                mExercise.getTitle(),
                LocalDate.now(),
                mTimerStartMilliseconds/1000);
        SQLiteHelper.getInstance(this).addRecord(record);
    }

    // 반복 횟수 UI 업데이트

    private void updateRepetitionUI() {

        TextView repetitionText = findViewById(R.id.txtRepetition);
        String strRepetition = String.format(Locale.getDefault(), "현재 %d회", mRepetition);
        repetitionText.setText(strRepetition);
    }


    // 타이머 스레드
    private class TimerThread extends Thread {
        @Override
        public void run() {
            while (mTimerStatus != TIMER_STOP) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mTimerStatus == TIMER_RUNNING) {
                    mTimerMilliseconds -= 100;
                    mTimerHandler.sendEmptyMessage(0);
                }
            }
        }
    }

    // 타이머 핸들러

    @SuppressWarnings("deprecation")
    private static class TimerHandler extends Handler {

        WeakReference<ExerciseActivity> reference;

        public TimerHandler(ExerciseActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            ExerciseActivity activity = reference.get();
            if (activity == null) {
                return;
            }

            if (activity.mTimerMilliseconds == 0) {
                activity.stopTimer();
                activity.increaseRepetition();
            } else {
                activity.updateTimerUI();
            }
        }
    }

}


