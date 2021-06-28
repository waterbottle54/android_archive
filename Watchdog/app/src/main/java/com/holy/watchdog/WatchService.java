package com.holy.watchdog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.holy.watchdog.models.Criminal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WatchService extends Service {

    public static final int NOTIFICATION_DETECTED = 100;

    private List<Criminal> mCriminalList;
    private NotificationManager mNotificationManager;
    private boolean mQuit;


    @Override
    public void onCreate() {

        super.onCreate();
        mCriminalList = new ArrayList<>();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mQuit = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("title")
                .setContentText("text")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
        startForeground(2001,notification);

        // start watch thread
        mQuit = false;
        new WatchThread(this).start();

        return START_NOT_STICKY;
    }

    public void setCriminalList(List<Criminal> criminalList) {

        mCriminalList = criminalList;
    }

    // Notify that criminal has been detected

    private void notifyCriminalDetected(String nickname, Uri uri) {

        String strMessage = String.format(Locale.getDefault(),
                "범죄자 %s의 활동이 감지되었습니다.", nickname);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("범죄자 감지")
                .setContentText(strMessage)
                .setSmallIcon(R.drawable.ic_person)
                .setTicker(strMessage)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(NOTIFICATION_DETECTED, notification);
    }


    // Binder

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public WatchService getService() {
            return WatchService.this;
        }
    }


    // Watch Thread

    static class WatchThread extends Thread {

        public static final String URL_TARGET = "https://www.ilbe.com/list/jjal";
        public static final String URL_PREFIX = "https://www.ilbe.com";
        WeakReference<WatchService> reference;
        List<String> detectedTimeList;

        public WatchThread(WatchService watchService) {

            reference = new WeakReference<>(watchService);
            detectedTimeList = new ArrayList<>();
        }

        @Override
        public void run() {

            while (!reference.get().mQuit) {
                try {
                    // Read all posts
                    Document doc = Jsoup.connect(URL_TARGET).get();
                    Elements postList = doc.select("ul.board-body li");

                    for (Element post : postList) {

                        // Read each post's information
                        String writer = post.select("span.global-nick a").text();
                        String date = post.select("span.date").text();
                        String href = post.select("span.title a").attr("href");
                        String url = URL_PREFIX + href;

                        // Check if any of criminals wrote the post
                        for (Criminal criminal : reference.get().mCriminalList) {
                            // If so, notify to the user
                            if (writer.equals(criminal.getNickname())
                                    && !detectedTimeList.contains(date)) {
                                reference.get().notifyCriminalDetected(
                                        criminal.getNickname(),
                                        Uri.parse(url));
                                detectedTimeList.add(date);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
