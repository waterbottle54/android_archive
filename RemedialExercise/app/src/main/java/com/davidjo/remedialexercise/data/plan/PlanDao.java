package com.davidjo.remedialexercise.data.plan;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlanDao {

    @Query("SELECT * FROM plan_table")
    LiveData<List<Plan>> getPlans();

    @Query("SELECT * FROM plan_table WHERE closed == 0")
    LiveData<Plan> getOngoingPlan();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Plan plan);

    @Update
    void update(Plan plan);

    @Query("UPDATE plan_table SET closed = 1 WHERE id == :id")
    void close(int id);

    @Delete
    void delete(Plan plan);

}
