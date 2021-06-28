package com.holy.exercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ExerciseFragment mExerciseFragment;
    private RecordsFragment mRecordsFragment;
    private HospitalFragment mHospitalFragment;
    private ForumFragment mForumFragment;
    private Fragment mCurrentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // 프래그먼트 생성
        mExerciseFragment = new ExerciseFragment();
        mRecordsFragment = new RecordsFragment();
        mForumFragment = new ForumFragment();
        mHospitalFragment = new HospitalFragment();
        showExerciseFragment();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_exercise:
                showExerciseFragment();
                break;
            case R.id.item_records:
                showRecordsFragment();
                break;
            case R.id.item_forum:
                showForumFragment();
                break;
            case R.id.item_hospital:
                showHospitalFragment();
                break;
        }
        return true;
    }

    private void showExerciseFragment() {

        mCurrentFragment = mExerciseFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .commit();
    }

    private void showRecordsFragment() {

        mCurrentFragment = mRecordsFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .commit();
    }

    private void showForumFragment() {

        mCurrentFragment = mForumFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .commit();
    }

    private void showHospitalFragment() {

        mCurrentFragment = mHospitalFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .commit();
    }

}


