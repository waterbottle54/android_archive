package com.holy.exercise.models;

import java.io.Serializable;

public class Exercise implements Serializable {

    public static final int TYPE_UPPER = 1;
    public static final int TYPE_MIDDLE = 2;
    public static final int TYPE_LOWER = 3;

    private final String title;
    private final String description;
    private final int imgRes;
    private final int type;

    public Exercise(String title, String description, int imgRes, int type) {
        this.title = title;
        this.description = description;
        this.imgRes = imgRes;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImgRes() {
        return imgRes;
    }

    public int getType() {
        return type;
    }
}
