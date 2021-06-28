package com.holy.blueplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;


public class PlayMusicActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_AUDIO_POSITION = "com.myapp.musictube.audio_position";

    // 서비스, 바인딩 정보
    private PlayerService mService;
    private boolean mBound = false;
    private ServiceConnection mConnection;

    // 타이머 핸들러 : 100ms 마다 호출
    private Handler mTimerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        // 버튼 리스너 설정
        ImageButton playButton = findViewById(R.id.ibtn_play_music);
        ImageButton prevButton = findViewById(R.id.ibtn_prev_music);
        ImageButton nextButton = findViewById(R.id.ibtn_next_music);
        playButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        // 재생 서비스 시작 (기존 서비스 있으면 중지)
        int position = getIntent().getIntExtra(EXTRA_AUDIO_POSITION, 0);
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra(PlayerService.EXTRA_AUDIO_POSITION, position);
        startService(serviceIntent);

        // 재생 서비스와의 커넥션 정의
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // 바인딩 시작
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                // 바인딩 중지
                mBound = false;
            }
        };

        // 타이머 핸들러 정의
        mTimerHandler = new Handler(Looper.getMainLooper(), msg -> {

            // 오디어 정보를 갱신하여 표시
            if (mBound) {
                updateAudioViews();
            }

            mTimerHandler.sendEmptyMessageDelayed(0, 100);
            return true;
        });
    }

    @Override
    protected void onDestroy() {

        // 재생 서비스 중지
        Intent serviceIntent = new Intent(this, PlayerService.class);
        stopService(serviceIntent);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 서비스 바인딩 시작
        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

        // 타이머 스레드 시작
        mTimerHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onStop() {

        // 타이머 중지
        mTimerHandler.removeMessages(0);

        // 서비스 바인딩 중지
        unbindService(mConnection);

        super.onStop();
    }

    // 버튼 클릭 처리

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        // 바인딩 확인
        if (!mBound) {
            return;
        }

        switch (v.getId()) {
            case R.id.ibtn_play_music:
                mService.playOrPause();
                break;
            case R.id.ibtn_prev_music:
                mService.prev();
                break;
            case R.id.ibtn_next_music:
                mService.next();
                break;
        }
    }


    // 음악 정보를 레이아웃에 표시

    @SuppressLint("SetTextI18n")
    private void updateAudioViews() {

        AudioInfo audioInfo = mService.getCurrentAudio();
        if (audioInfo == null) {
            return;
        }

        // 앨범 아트, 타이틀 표시
        ImageView albumArtImage = findViewById(R.id.img_album_art);
        TextView titleText = findViewById(R.id.txt_music_title);

        albumArtImage.setImageURI(audioInfo.getAlbumArtUri());
        titleText.setText(audioInfo.getTitle() + ".mp3");

        // 프로그레스 바 표시
        ProgressBar progressBar = findViewById(R.id.progress_music);

        int currentInSecond = mService.getCurrentPosition() / 1000;
        int durationInSecond = mService.getDuration() / 1000;

        progressBar.setProgress(100 * currentInSecond / durationInSecond);

        // 현재 진행 시간 / 총 시간 표시
        String strCurrent = String.format(Locale.getDefault(), "%02d : %02d",
                currentInSecond / 60, currentInSecond % 60);

        String strDuration = String.format(Locale.getDefault(), "%02d : %02d",
                durationInSecond / 60, durationInSecond % 60);

        TextView progressText = findViewById(R.id.txt_music_progress);
        progressText.setText(String.format(Locale.getDefault(),
                "%s / %s", strCurrent, strDuration));

        // 재생 버튼 표시
        ImageButton playButton = findViewById(R.id.ibtn_play_music);
        playButton.setBackgroundResource(mService.isPlaying() ?
                R.drawable.ic_pause : R.drawable.ic_play);
    }

}