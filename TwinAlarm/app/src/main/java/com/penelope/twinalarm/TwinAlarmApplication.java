package com.penelope.twinalarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class TwinAlarmApplication extends Application {

    public static final String CHANNEL_NAME = "Twin Alarm";
    public static final String CHANNEL_ALARM = "com.penelope.twinalarm.notification_alarm";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    // notification 채널 생성

    private void createNotificationChannel() {

        // Time-over notification channel
        NotificationChannel alarmChannel = new NotificationChannel(
                CHANNEL_ALARM,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(alarmChannel);
    }
}
