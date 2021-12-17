package com.penelope.twinalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.preference.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 알람 번호 확인
        int alarmIndex = intent.getIntExtra("alarmIndex", 1);

        // 해당 알람 활성화 여부 확인
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (!pref.getBoolean("alarm" + alarmIndex, true)) {
            return;
        }

        // 노티피케이션 작성
        Notification notification;

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Icon smallIcon = Icon.createWithResource(context, R.drawable.ic_alarm);

        notification = new Notification.Builder(context,
                TwinAlarmApplication.CHANNEL_ALARM)
                .setContentIntent(pendingIntent)
                .setSmallIcon(smallIcon)
                .setContentText("알람이 실행됩니다")
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build();

        // 노티피케이션 띄우기
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(100, notification);
    }

}