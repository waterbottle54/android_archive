package com.holy.blueplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.List;


public class PlayerService extends Service {

    public static final int NOTIFICATION_PLAY = 1;
    public static final String EXTRA_AUDIO_POSITION = "com.myapp.musictube.audio_position";
    public static final String ACTION_PLAY_AUDIO = "com.myapp.musictube.play_audio";
    public static final String ACTION_PREV_AUDIO = "com.myapp.musictube.prev_audio";
    public static final String ACTION_NEXT_AUDIO = "com.myapp.musictube.next_audio";

    // 현재 재생중인 오디오, 포지션
    private AudioInfo mAudioInfo;
    private int mAudioPosition;

    // 미디어 플레이어
    private MediaPlayer mPlayer;

    // 노티피케이션 메니저
    private NotificationManager mNotificationManager;

    // 컨트롤 버튼 BR
    private BroadcastReceiver mPlayBR;
    private BroadcastReceiver mPrevBR;
    private BroadcastReceiver mNextBR;

    @Override
    public void onCreate() {
        super.onCreate();

        // 컨트롤 BR 등록 : 노티피케이션 버튼 클릭 처리
        mPlayBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                playOrPause();
            }
        };
        mNextBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                next();
            }
        };
        mPrevBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                prev();
            }
        };

        registerReceiver(mPlayBR, new IntentFilter(ACTION_PLAY_AUDIO));
        registerReceiver(mNextBR, new IntentFilter(ACTION_NEXT_AUDIO));
        registerReceiver(mPrevBR, new IntentFilter(ACTION_PREV_AUDIO));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 전송된 오디오 정보 불러오고 플레이하기
        mAudioPosition = intent.getIntExtra(EXTRA_AUDIO_POSITION, 0);
        mAudioInfo = loadAudio();
        startPlayer();

        // 노티피케이션 생성
        createNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    // 노티피케이션 생성

    private void createNotification() {

        // 리모트뷰 생성
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.control_view);
        notificationView.setImageViewUri(R.id.img_album_art, mAudioInfo.getAlbumArtUri());
        notificationView.setTextViewText(R.id.txt_music_title, mAudioInfo.getTitle());

        if (mPlayer != null && mPlayer.isPlaying()) {
            notificationView.setImageViewResource(R.id.ibtn_play_music, R.drawable.ic_pause);
        }

        // 리모트 뷰 버튼 리스너 등록
        Intent playIntent = new Intent(ACTION_PLAY_AUDIO);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.ibtn_play_music, playPendingIntent);

        Intent nextIntent = new Intent(ACTION_NEXT_AUDIO);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.ibtn_next_music, nextPendingIntent);

        Intent prevIntent = new Intent(ACTION_PREV_AUDIO);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.ibtn_prev_music, prevPendingIntent);

        // 노티피케이션 생성
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(mAudioInfo.getTitle())
                .setSmallIcon(R.drawable.ic_play)
                .setCustomBigContentView(notificationView)
                .build();
        mNotificationManager.notify(NOTIFICATION_PLAY, notification);
    }

    @Override
    public void onDestroy() {
        stopPlayer();
        mNotificationManager.cancel(NOTIFICATION_PLAY);
        unregisterReceiver(mPlayBR);
        unregisterReceiver(mNextBR);
        unregisterReceiver(mPrevBR);
        super.onDestroy();
    }

    // 오디오 로드

    private AudioInfo loadAudio() {

        List<AudioInfo> audioInfoList = ((MyApplication)getApplication()).getAudioList();
        if (audioInfoList.isEmpty()) {
            return null;
        }

        if (mAudioPosition < 0 || mAudioPosition >= audioInfoList.size()) {
            return null;
        }

        return audioInfoList.get(mAudioPosition);
    }

    private AudioInfo loadNextAudio() {

        List<AudioInfo> audioInfoList = ((MyApplication)getApplication()).getAudioList();
        if (audioInfoList.isEmpty()) {
            return null;
        }

        mAudioPosition++;
        if (mAudioPosition >= audioInfoList.size()) {
            mAudioPosition = 0;
        }

        return audioInfoList.get(mAudioPosition);
    }

    private AudioInfo loadPrevAudio() {

        List<AudioInfo> audioInfoList = ((MyApplication)getApplication()).getAudioList();
        if (audioInfoList.isEmpty()) {
            return null;
        }

        mAudioPosition--;
        if (mAudioPosition < 0) {
            mAudioPosition = audioInfoList.size() - 1;
        }

        return audioInfoList.get(mAudioPosition);
    }

    // 오디오, 프로그레스 정보

    public AudioInfo getCurrentAudio() {
        return mAudioInfo;
    }

    public int getCurrentPosition() {

        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {

        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public boolean isPlaying() {

        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    // 오디오 조작

    public void playOrPause() {

        if (mPlayer == null || !mPlayer.isPlaying()) {
            startPlayer();
        } else {
            pausePlayer();
        }
        createNotification();
    }

    public void next() {

        mAudioInfo = loadNextAudio();
        if (mAudioInfo != null) {
            stopPlayer();
            startPlayer();
            createNotification();
        }
    }

    public void prev() {

        mAudioInfo = loadPrevAudio();
        if (mAudioInfo != null) {
            stopPlayer();
            startPlayer();
            createNotification();
        }
    }

    // 미디어 플레이어 재생 시작

    private void startPlayer() {

        // 미디어 플레이어 생성 및 리스너 설정
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this, mAudioInfo.getUri());
            mPlayer.setOnCompletionListener(mp -> stopPlayer());
        }

        // 재생 시작
        mPlayer.start();
    }

    // 미디어 플레이어 일시정지

    private void pausePlayer() {

        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    // 미디어 플레이어 정지

    private void stopPlayer() {

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    // 바인더

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

}
