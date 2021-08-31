package com.davidjo.greenworld.data.category;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    // 기본 DB 접근 메소드들

    @Query("SELECT * FROM category_table")
    LiveData<List<Category>> getCategories();

    @Query("SELECT * FROM category_table WHERE name == :categoryName")
    Category findCategory(String categoryName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Update
    void update(Category category);

}
