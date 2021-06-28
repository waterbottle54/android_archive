package com.holy.exercise;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.holy.exercise.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static final String ADMIN_PASSWORD = "admin2021";

    private boolean mIsAdmin;

    private List<Exercise> mExerciseUpperList;
    private List<Exercise> mExerciseMiddleList;
    private List<Exercise> mExerciseLowerList;


    public boolean isAdmin() {
        return mIsAdmin;
    }

    public boolean loginAdmin(String password) {

        // 관리자 로그인
        if (password.equals(ADMIN_PASSWORD)) {
            mIsAdmin = true;
            return true;
        } else {
            return false;
        }
    }

    public List<Exercise> getUpperExerciseList() {
        if (mExerciseUpperList == null) {
            mExerciseUpperList = loadExercisesFromResource(
                    getResources(), R.array.exerciseUpperData, Exercise.TYPE_UPPER);
        }
        return mExerciseUpperList;
    }

    public List<Exercise> getMiddleExerciseList() {
        if (mExerciseMiddleList == null) {
            mExerciseMiddleList = loadExercisesFromResource(
                    getResources(), R.array.exerciseMiddleData, Exercise.TYPE_MIDDLE);
        }
        return mExerciseMiddleList;
    }

    public List<Exercise> getLowerExerciseList() {
        if (mExerciseLowerList == null) {
            mExerciseLowerList = loadExercisesFromResource(
                    getResources(), R.array.exerciseLowerData, Exercise.TYPE_LOWER);
        }
        return mExerciseLowerList;
    }

    @SuppressLint("ResourceType")
    private static List<Exercise> loadExercisesFromResource(Resources res, int resId, int type) {

        TypedArray exerciseData = res.obtainTypedArray(resId);
        String[] exerciseTitles = res.getStringArray(exerciseData.getResourceId(0, 0));
        String[] exerciseDescriptions = res.getStringArray(exerciseData.getResourceId(1, 0));
        TypedArray exerciseImgResources = res.obtainTypedArray(exerciseData.getResourceId(2, 0));

        List<Exercise> exerciseList = new ArrayList<>();
        for (int i = 0; i < exerciseTitles.length; i++) {
            Exercise exercise = new Exercise(
                    exerciseTitles[i],
                    exerciseDescriptions[i],
                    exerciseImgResources.getResourceId(i, 0),
                    type);
            exerciseList.add(exercise);
        }
        exerciseData.recycle();
        exerciseImgResources.recycle();

        return exerciseList;
    }

}
