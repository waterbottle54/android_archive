package com.davidjo.remedialexercise.data.training;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.davidjo.remedialexercise.data.exercise.ExerciseDatabase;

@Database(entities = {Training.class}, version = 2, exportSchema = false)
public abstract class TrainingDatabase extends RoomDatabase {

    public abstract TrainingDao trainingDao();

    private static TrainingDatabase instance;

    public static TrainingDatabase getInstance(Application app) {

        if (instance == null) {
            instance =  Room.databaseBuilder(app, TrainingDatabase.class, "training_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

}
