package com.myapp.mapnplayers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class AudioFragment extends Fragment implements
        View.OnClickListener, SensorEventListener {

    // 음악 리소스 리스트
    public static final int[] MUSIC_RES_LIST = {
            R.raw.somewhere_in_my_memory, R.raw.fur_elise, R.raw.korean_anthem
    };

    // 음악 제목 리소스 리스트
    public static final int[] MUSIC_TITLE_RES_LIST = {
            R.string.somewhere_in_my_memory, R.string.fur_elise, R.string.korean_anthem
    };

    // 이미지 리소스 리스트
    public static final int[] IMAGE_RES_LIST = {
            R.drawable.xmas, R.drawable.elise, R.drawable.flag,
    };

    // 미디어 플레이어
    private MediaPlayer mPlayer;

    // 현재 음악
    private int mPlayIndex = 0;

    // 음악 타이틀, 길이 텍스트뷰
    private TextView mTitleText;
    private TextView mDurationText;
    private ImageView mTitleImage;

    // 센서 매니저, 근접 센서
    private SensorManager mSensorManager;
    private Sensor mProximitySensor;


    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_audio, container, false);

        // 오디오 버튼에 리스너를 설정한다
        Button playButton = v.findViewById(R.id.btn_play);
        Button stopButton = v.findViewById(R.id.btn_stop);
        Button nextButton = v.findViewById(R.id.btn_next);
        playButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        // 음악 타이틀, 길이 텍스트뷰 초기화
        mTitleText = v.findViewById(R.id.txt_title);
        mDurationText = v.findViewById(R.id.txt_duration);
        mTitleImage = v.findViewById(R.id.img_title);
        showTitleAndDuration();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 근접 센서 획득
        if (getContext() != null) {
            mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (mProximitySensor == null) {
                Toast.makeText(getContext(), "기기에 근접센서가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // 근접 센서 리스너 설정
        if (mProximitySensor != null) {
            mSensorManager.registerListener(
                    this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // 미디어 플레이어 정지
        stopPlayer();

        // 근접 센서 리스너 제거
        if (mProximitySensor != null) {
            mSensorManager.unregisterListener(this, mProximitySensor);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_play:
                startPlayer();
                break;
            case R.id.btn_stop:
                stopPlayer();
                break;
            case R.id.btn_next:
                playNext();
                break;
        }
    }

    // 미디어 플레이어 재생 시작

    private void startPlayer() {

        // 미디어 플레이어 생성 및 리스너 설정
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(getContext(), MUSIC_RES_LIST[mPlayIndex]);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        // 재생 시작
        mPlayer.start();

        // 음악 제목 밑 길이 표시
        showTitleAndDuration();
    }

    // 미디어 플레이어 정지지

    private void stopPlayer() {

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    // 다음 음악 재생

    private void playNext() {

        // 현재 음악 종료
        stopPlayer();

        // 인덱스 이동
        mPlayIndex++;
        if (mPlayIndex == MUSIC_RES_LIST.length) {
            mPlayIndex = 0;
        }

        // 다음 음악 재생
        startPlayer();
    }

    // 음악 제목 밑 길이 표시

    private void showTitleAndDuration() {

        String strTitle = getString(MUSIC_TITLE_RES_LIST[mPlayIndex]);
        mTitleText.setText(String.format(Locale.getDefault(), "Music: %s", strTitle));
        mTitleImage.setImageResource(IMAGE_RES_LIST[mPlayIndex]);

        if (mPlayer != null) {
            int durationInSecond = mPlayer.getDuration() / 1000;
            String strDuration = String.format(Locale.getDefault(), "%02d : %02d",
                    durationInSecond / 60, durationInSecond % 60);

            mDurationText.setText(String.format(Locale.getDefault(),
                    "Duration  %s", strDuration));
        }
    }


    // 센서 리스너 메소드

    @Override
    public void onSensorChanged(SensorEvent event) {

        // 근접 거리를 구한다.
        float distanceInCMs = event.values[0];

        // 근접 거리가 충분히 가까운 경우 비디오를 재생한다.
        if (distanceInCMs < 1.0f) {
            if (mPlayer == null) {
                startPlayer();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

}