package com.holy.blueplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    // 노티피케이션 채널 ID
    public static final String CHANNEL_ID = "exampleServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        // 노티피케이션 채널을 만든다.
        createNotificationChannel();
    }

    // 노티피케이션 채널을 만든다.

    private void createNotificationChannel() {

        // n Oreo 이상 운영체제이면 노티피케이션 채널을 만든다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    // 미디어 스토어의 오디오 정보 불러오기 : READ_EXTERNAL_STORAGE 퍼미션 필요

    public List<AudioInfo> getAudioList() {

        List<AudioInfo> audioInfoList = new ArrayList<>();

        // 미디어 스토어 콘텐트 URI 획득 : Q 와 Q 미만 버전이 다르다.
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        // 컨텐트 리졸버를 이용하여 오디오 정보를 순차적으로 조회한다
        try (Cursor cursor = getContentResolver().query(
                collection, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    // 오디오 아이디, 앨범 아이디, 타이틀을 조회한다.
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

                    // 오디오 URI 구성
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                    // 앨범아트 URI 구성
                    Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                    Uri albumArtUriAppended = ContentUris.withAppendedId(albumArtUri, albumId);

                    // 오디오 정보를 리스트에 추가
                    audioInfoList.add(new AudioInfo(contentUri, title, albumArtUriAppended));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audioInfoList;
    }

    // 특정 오디오 정보 불러오기

    public AudioInfo getAudio(Uri uri) {

        AudioInfo audioInfo = null;

        // 컨텐트 리졸버를 이용하여 오디오 정보를 조회한다
        try (Cursor cursor = getContentResolver().query(
                uri, null, null, null, null)) {
            if (cursor.moveToFirst()) {

                // 오디오 앨범 아이디, 타이틀을 조회한다.
                int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

                // 앨범아트 URI 결정
                Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUriAppended = ContentUris.withAppendedId(albumArtUri, albumId);

                // 오디오 정보를 리스트에 추가
                audioInfo = new AudioInfo(uri, title, albumArtUriAppended);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audioInfo;
    }

}
