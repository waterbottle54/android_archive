package com.holy.blueplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 100;

    private List<AudioInfo> mAudioInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 외부 저장소 읽기 권한 요청 : 오디오 정보 조회를 위해
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            // 권한이 있을 시 : 오디오 리스트 뷰 초기화
            buildAudioList();
        }

    }

    // 권한 요청 응답 처리

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 승인 시 : 오디오 리스트 뷰 초기화
                buildAudioList();
            } else {
                // 권한 거부 : 오디오 조회 불가
                Toast.makeText(this, "오디오를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 오디오 리스트 뷰 초기화 : 저장소 읽기 권한 필요

    private void buildAudioList() {

        ListView audioListView = findViewById(R.id.list_audio);
        mAudioInfoList = ((MyApplication)getApplication()).getAudioList();
        AudioAdapter audioAdapter = new AudioAdapter(mAudioInfoList);
        audioListView.setAdapter(audioAdapter);

        // 아이템 클릭 리스너 설정 : PlayMusic 액티비티 시작
        audioAdapter.setOnItemClickListener(this::startPlayMusicActivity);
    }

    // PlayMusic 액티비티 시작 : 선택된 오디오 정보의 포지션 전달

    private void startPlayMusicActivity(int position) {

        Intent intent = new Intent(this, PlayMusicActivity.class);
        intent.putExtra(PlayMusicActivity.EXTRA_AUDIO_POSITION, position);
        startActivity(intent);
    }

}