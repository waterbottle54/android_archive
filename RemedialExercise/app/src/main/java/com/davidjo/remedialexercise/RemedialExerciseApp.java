package com.davidjo.remedialexercise;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.naver.maps.map.a.g;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class RemedialExerciseApp extends Application {

    public static final String CHANNEL_NAME = "Training Service";
    public static final String CHANNEL_FOREGROUND = "com.davidjo.remedialexercise.training_service";
    public static final String CHANNEL_TIME_OVER = "com.davidjo.remedialexercise.time_over";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        // Foreground notification channel
        NotificationChannel foregroundChannel = new NotificationChannel(
                CHANNEL_FOREGROUND,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
        );

        // Time-over notification channel
        NotificationChannel timeOverChannel = new NotificationChannel(
                CHANNEL_TIME_OVER,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(foregroundChannel);
        manager.createNotificationChannel(timeOverChannel);
    }

}
