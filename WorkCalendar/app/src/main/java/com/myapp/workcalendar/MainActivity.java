package com.myapp.workcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    // 달력 프래그먼트
    private CalendarFragment mCalendarFragment;
    // 월별 프래그먼트
    private MonthlyFragment mMonthlyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 앱이 첫 실행될 때 표지를 잠깐 띄웠다가 fade out 한다.
        if (savedInstanceState == null) {
            fadeOutCover();
        }

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 탭 레이아웃에 리스너 설정
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);

        // 프래그먼트 생성 : 달력 프래그먼트 삽입
        mCalendarFragment = new CalendarFragment();
        mMonthlyFragment = new MonthlyFragment();
        showCalendarFragment();
    }

    // 표지 레이아웃에 fade out 애니메이션 적용

    private void fadeOutCover() {

        View cover = findViewById(R.id.cover);
        cover.setVisibility(View.VISIBLE);

        // fade out 애니메이션 로드 : 자체 정의한 fade out 리소스를 사용한다.
        // 리스너를 설정하여 애니메이션이 끝날 때 표지 레이아웃을 GONE 상태로 만든다.
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                cover.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        // 3초간 보여주다가, fade out 애니메이션 적용
        cover.postDelayed(new Runnable() {
            @Override
            public void run() {
                cover.startAnimation(fadeOut);
            }
        }, 3000);
    }

    // 탭 선택 처리

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {
            case 0:
                // 달력 프래그먼트 삽입
                showCalendarFragment();
                break;
            case 1:
                // 월별 프래그먼트 삽입
                showMonthlyFragment();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    // 달력 프래그먼트 삽입

    private void showCalendarFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, mCalendarFragment)
                .commit();
    }

    // 월별 프래그먼트 삽입

    private void showMonthlyFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, mMonthlyFragment)
                .commit();

    }

    // 옵션 메뉴 생성

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    // 옵션 메뉴 클릭 처리

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.btn_wage) {
            // 최저시급안내 대화상자 띄우기
            showMinimumWageDialog();
            return true;
        }

        return false;
    }

    // 최저시급안내 대화상자 띄우기

    private void showMinimumWageDialog() {

        new AlertDialog.Builder(this)
                .setTitle("최저시급 안내")
                .setMessage("2020년의 최저시급은 8,590원이며\n\n2021년의 최저시급은 8,720원입니다.")
                .setPositiveButton("네", null)
                .show();
    }
}