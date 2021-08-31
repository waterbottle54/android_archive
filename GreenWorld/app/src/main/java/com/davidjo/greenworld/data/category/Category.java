package com.davidjo.greenworld.data.category;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "category_table")
public class Category implements Serializable {

    @NonNull
    @PrimaryKey
    public final String name;       // 카테고리명
    public final int score;         // 1회 시행 당 점수
    public final int icon;          // 작은 아이콘
    public final String imageUrl;   // 큰 이미지 url
    public final boolean basic;     // 기본 카테고리 여부
    public boolean visible;         // 표시되는지 숨겨져있는지 여부

    public Category(@NonNull String name, int score, int icon, String imageUrl, boolean basic) {
        this.name = name;
        this.score = score;
        this.icon = icon;
        this.imageUrl = imageUrl;
        this.basic = basic;
        this.visible = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return score == category.score && icon == category.icon && basic == category.basic && visible == category.visible && name.equals(category.name) && Objects.equals(imageUrl, category.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score, icon, imageUrl, basic, visible);
    }
}
