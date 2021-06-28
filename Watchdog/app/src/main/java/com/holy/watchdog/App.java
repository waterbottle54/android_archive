package com.holy.watchdog;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_NAME = "Watchdog channel";
    public static final String CHANNEL_ID = "com.holy.watchdog.notification_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        // create notification channel.
        createNotificationChannel();
    }

    // create notification channel.

    private void createNotificationChannel() {

        // n Oreo 이상 운영체제이면 노티피케이션 채널을 만든다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
