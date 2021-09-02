package com.davidjo.remedialexercise.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.RemedialExerciseApp;
import com.davidjo.remedialexercise.brs.TimeOverBroadcastReceiver;
import com.davidjo.remedialexercise.data.plan.Plan;
import com.davidjo.remedialexercise.data.training.Training;
import com.davidjo.remedialexercise.data.training.TrainingDao;
import com.davidjo.remedialexercise.data.training.TrainingDatabase;
import com.davidjo.remedialexercise.ui.MainActivity;

public class TrainingService extends LifecycleService {

    public static final int NOTIFICATION_FOREGROUND = 100;

    public static final String EXTRA_PLAN = "com.davidjo.remedialexercise.plan";

    public static final String BR_FINISHED = "com.davidjo.remedialexercise.br_finished";

    public static boolean isServiceStarted = false;

    private TrainingThread thread;
    private boolean isRunning;

    private Plan plan;
    private long milliseconds;
    private final MutableLiveData<Integer> seconds = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isPaused = new MutableLiveData<>(false);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        isServiceStarted = true;
        startForeground(NOTIFICATION_FOREGROUND, buildNotification());

        if (intent != null) {
            plan = (Plan) intent.getSerializableExtra(EXTRA_PLAN);

            milliseconds = (long) plan.getMinutesPerRepetition() * 60000;
            seconds.setValue((int) milliseconds / 1000);
        }

        startCountdownTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceStarted = false;
    }

    private Notification buildNotification() {

        Notification notification;

        String contentText = "재활운동 진행 중";

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        notification = new Notification.Builder(this, RemedialExerciseApp.CHANNEL_FOREGROUND)
                .setContentIntent(pendingIntent)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .build();

        return notification;
    }

    public LiveData<Integer> getSeconds() {
        return seconds;
    }

    public LiveData<Boolean> isPaused() {
        return isPaused;
    }

    private void startCountdownTimer() {
        killCountdownTimer();
        thread = new TrainingThread();
        isRunning = true;
        isPaused.setValue(false);
        thread.start();
    }

    private void killCountdownTimer() {
        if (thread != null) {
            isRunning = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopCountdownTimer() {
        if (thread != null) {
            killCountdownTimer();
            sendBroadcast(new Intent(BR_FINISHED));
            stopSelf();
            isServiceStarted = false;
        }
    }

    public void pauseCountdownTimer() {
        if (thread != null) {
            isPaused.setValue(true);
        }
    }

    public void resumeCountdownTimer() {
        if (thread != null) {
            isPaused.setValue(false);
        }
    }

    public void toggleCountdownTimer() {
        if (!isRunning || isPaused.getValue() == null) {
            return;
        }
        if (isPaused.getValue()) {
            resumeCountdownTimer();
        } else {
            pauseCountdownTimer();
        }
    }

    private void sendTimeOverBroadcast() {
        Intent intent = new Intent(this, TimeOverBroadcastReceiver.class);
        sendBroadcast(intent);
    }

    private void insertTraining() {

        TrainingDao trainingDao = TrainingDatabase.getInstance(getApplication()).trainingDao();
        trainingDao.insert(new Training(
                plan.getId(),
                plan.getMinutesPerRepetition(),
                System.currentTimeMillis(),
                plan.getBodyPart()));

        Log.d("TAG", "insertTraining: ");
    }


    private class TrainingThread extends Thread {

        private long lastMillis = -1;
        private long accumulatedMillis = 0;

        @Override
        public void run() {
            super.run();

            while (isRunning) {

                long currentMillis = System.currentTimeMillis();
                if (isPaused.getValue() == null || isPaused.getValue() || lastMillis == -1) {
                    lastMillis = currentMillis;
                    continue;
                }

                long offsetMillis = currentMillis - lastMillis;
                milliseconds -= offsetMillis;
                accumulatedMillis += offsetMillis;
                lastMillis = currentMillis;

                if (accumulatedMillis >= 1000) {
                    seconds.postValue((int) milliseconds / 1000);
                    accumulatedMillis = 0;
                }

                if (milliseconds <= 0) {
                    milliseconds = 0;
                    isRunning = false;
                    insertTraining();
                    sendTimeOverBroadcast();
                    sendBroadcast(new Intent(BR_FINISHED));
                }
            }

            stopSelf();
            isServiceStarted = false;
        }
    }


    // Binder

    public class TrainingBinder extends Binder {
        public TrainingService getService() {
            return TrainingService.this;
        }
    }

    private final TrainingBinder binder = new TrainingBinder();

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return binder;
    }
}