package com.good.todoapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.good.todoapp.App;
import com.good.todoapp.MainActivity;
import com.good.todoapp.R;

public class MaximService extends Service {

    public static final int NOTIFICATION_ID = 100;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 인텐트에서 주어진 텍스트를 이용하여 노티피케이션을 만든다.
        
        String text = intent.getStringExtra(MainActivity.EXTRA_SERVICE_TEXT);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        // Builder 를 이용하여 노티피케이션 생성, 초기화.

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_note_blue)
                .setContentIntent(pendingIntent)
                .build();

        // 생성된 노티피케이션으로 포어그라운드 서비스를 시작한다.

        startForeground(NOTIFICATION_ID, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
