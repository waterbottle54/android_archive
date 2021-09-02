package com.davidjo.remedialexercise.data.plan;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Plan.class}, version = 5, exportSchema = false)
public abstract class PlanDatabase extends RoomDatabase {

    public abstract PlanDao planDao();

}
