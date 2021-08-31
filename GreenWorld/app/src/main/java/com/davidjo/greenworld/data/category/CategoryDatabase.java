package com.davidjo.greenworld.data.category;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.davidjo.greenworld.R;

import javax.inject.Inject;
import javax.inject.Provider;

@Database(entities = {Category.class}, version = 11, exportSchema = false)
public abstract class CategoryDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    public static class Callback extends RoomDatabase.Callback {

        private final Provider<CategoryDatabase> database;

        @Inject
        public Callback(Provider<CategoryDatabase> database) {
            this.database = database;
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            CategoryDao dao = database.get().categoryDao();

            new Thread(() -> {
                dao.insert(new Category("대중교통 이용", 5, R.drawable.ic_bus, "https://cdn.aitimes.kr/news/photo/201909/14292_15060_3225.jpg", true));
                dao.insert(new Category("텀블러 사용", 5, R.drawable.ic_cup, "https://cdn.pixabay.com/photo/2016/01/15/05/58/tumbler-1141198_960_720.jpg", true));
                dao.insert(new Category("걷기", 5, R.drawable.ic_walk, "https://cdn.pixabay.com/photo/2015/09/13/04/24/feet-937698_960_720.jpg", true));
                dao.insert(new Category("식물 심기", 5, R.drawable.ic_forest, "https://cdn.pixabay.com/photo/2014/02/27/16/10/tree-276014_960_720.jpg", true));
            }).start();
        }
    }

}
