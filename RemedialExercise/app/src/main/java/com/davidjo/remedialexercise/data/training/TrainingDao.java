package com.davidjo.remedialexercise.data.training;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TrainingDao {

    @Query("SELECT * FROM training_table")
    LiveData<List<Training>> getTrainings();

    @Query("SELECT * FROM training_table WHERE planId == :planId")
    LiveData<List<Training>> getTrainings(int planId);

    @Query("SELECT * FROM training_table WHERE planId == :planId AND createdTime >= :startTime AND createdTime <= :endTime")
    LiveData<List<Training>> getTrainings(int planId, long startTime, long endTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Training training);

    @Update
    void update(Training training);

    @Delete
    void delete(Training training);

}
