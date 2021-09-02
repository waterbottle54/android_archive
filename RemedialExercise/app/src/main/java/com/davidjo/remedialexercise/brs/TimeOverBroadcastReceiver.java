package com.davidjo.remedialexercise.brs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.RemedialExerciseApp;
import com.davidjo.remedialexercise.services.TrainingService;
import com.davidjo.remedialexercise.ui.MainActivity;

public class TimeOverBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Notification notification;

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        notification = new Notification.Builder(context, RemedialExerciseApp.CHANNEL_TIME_OVER)
                .setContentIntent(pendingIntent)
                .setContentText("재활운동 완료")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentText("재활운동 1회 완료하셨습니다")
                .setTicker("재활운동 1회 완료하셨습니다")
                .setOnlyAlertOnce(false)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TrainingService.NOTIFICATION_FOREGROUND + 1, notification);
    }
}
