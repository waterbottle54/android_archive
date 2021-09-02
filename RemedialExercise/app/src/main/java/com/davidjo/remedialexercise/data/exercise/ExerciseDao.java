package com.davidjo.remedialexercise.data.exercise;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.davidjo.remedialexercise.data.BodyPart;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM exercise_table WHERE bodyPart == :target")
    LiveData<List<Exercise>> getExercises(BodyPart target);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exercise exercise);

}
