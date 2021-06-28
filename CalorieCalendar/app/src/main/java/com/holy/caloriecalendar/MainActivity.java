package com.holy.caloriecalendar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.holy.caloriecalendar.helpers.SQLiteHelper;
import com.holy.caloriecalendar.models.Food;
import com.holy.caloriecalendar.models.Intake;

import java.time.LocalDateTime;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    // SearchableActivity 에서 반환된 음식 검색 결과에 대한 키값
    public static final String EXTRA_SEARCHED_FOOD_ID = "com.holy.caloriecalendar.searched_food_id";

    private CalendarFragment mCalendarFragment;     // 달력 프래그먼트 (달력 탭 선택 시 보여짐)
    private FoodFragment mFoodFragment;             // 식품정보 프래그먼트 (식품정보 탭 선택시 보여짐)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 탭 레이아웃에 리스너 설정
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);

        // 선택된 섭취정보 추가
        if (getIntent().hasExtra(EXTRA_SEARCHED_FOOD_ID)) {
            addSelectedIntake();
        }

        // 프래그먼트 생성 : 달력 프래그먼트 삽입
        mCalendarFragment = new CalendarFragment();
        mFoodFragment = new FoodFragment();
        showCalendarFragment();
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
                // 식품정보 프래그먼트 삽입
                showFoodFragment();
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

    // 알람 프래그먼트 삽입

    private void showFoodFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, mFoodFragment)
                .commit();

    }

    // 선택된 섭취정보 추가

    private void addSelectedIntake() {

        // 식품이 선택된 경우 아이디 전달
        if (getIntent().hasExtra(EXTRA_SEARCHED_FOOD_ID)) {

            int searchedFoodId = getIntent().getIntExtra(EXTRA_SEARCHED_FOOD_ID, 0);
            Food food = SQLiteHelper.getInstance(this).getFood(searchedFoodId);
            LocalDateTime date = LocalDateTime.now();
            Intake intake = new Intake(food.getKcals(), food.getTitle(),
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                    date.getHour(), date.getMinute());
            SQLiteHelper.getInstance(this).addIntake(intake);
        }
    }

}