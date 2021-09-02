package com.davidjo.remedialexercise.di;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.davidjo.remedialexercise.data.exercise.ExerciseDao;
import com.davidjo.remedialexercise.data.exercise.ExerciseDatabase;
import com.davidjo.remedialexercise.data.plan.PlanDao;
import com.davidjo.remedialexercise.data.plan.PlanDatabase;
import com.davidjo.remedialexercise.data.training.TrainingDao;
import com.davidjo.remedialexercise.data.training.TrainingDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public ExerciseDatabase provideExerciseDatabase(Application app, ExerciseDatabase.Callback callback) {
        return Room.databaseBuilder(app, ExerciseDatabase.class, "exercise_database")
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build();
    }

    @Provides
    public ExerciseDao provideExerciseDao(ExerciseDatabase db) {
        return db.exerciseDao();
    }

    @Provides
    @Singleton
    public TrainingDatabase provideTrainingDatabase(Application app) {
        return TrainingDatabase.getInstance(app);
    }

    @Provides
    public TrainingDao provideTrainingDao(TrainingDatabase db) {
        return db.trainingDao();
    }

    @Provides
    @Singleton
    public PlanDatabase providePlanDatabase(Application app) {
        return Room.databaseBuilder(app, PlanDatabase.class, "plan_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public PlanDao providePlanDao(PlanDatabase db) {
        return db.planDao();
    }

}
