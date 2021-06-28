package com.myapp.mapnplayers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 타이틀 아이콘 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 홈 프래그먼트를 보여준다.
        showHome();
    }

    // 옵션 메뉴 생성

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // 옵션 메뉴 클릭 처리

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_home:
                showHome();
                return true;
            case R.id.btn_map:
                showMap();
                return true;
            case R.id.btn_audio:
                showAudio();
                return true;
            case R.id.btn_video:
                showVideo();
                return true;
        }
        return false;
    }

    // 각 프래그먼트를 보여준다.

    private void showHome() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, new HomeFragment())
                .commit();

        setTitle(R.string.home);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
    }

    private void showMap() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, new MyMapFragment())
                .commit();

        setTitle(R.string.current_map);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_map);
    }

    private void showAudio() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, new AudioFragment())
                .commit();

        setTitle(R.string.audio_player);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_audio);
    }

    private void showVideo() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, new VideoFragment())
                .commit();

        setTitle(R.string.video_player);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_video);
    }

}