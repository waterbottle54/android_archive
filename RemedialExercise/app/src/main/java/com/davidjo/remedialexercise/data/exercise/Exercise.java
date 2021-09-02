package com.davidjo.remedialexercise.data.exercise;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.davidjo.remedialexercise.data.BodyPart;

@Entity(tableName = "exercise_table")
public class Exercise {

    @PrimaryKey
    @NonNull
    private final String name;
    private final String description;
    private final String provider;
    private final String youtubeVideoId;
    private final BodyPart bodyPart;

    public Exercise(@NonNull String name, String description, String provider, String youtubeVideoId, BodyPart bodyPart) {
        this.name = name;
        this.description = description;
        this.provider = provider;
        this.youtubeVideoId = youtubeVideoId;
        this.bodyPart = bodyPart;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProvider() {
        return provider;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

}
