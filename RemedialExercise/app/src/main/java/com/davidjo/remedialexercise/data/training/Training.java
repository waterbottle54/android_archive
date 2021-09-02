package com.davidjo.remedialexercise.data.training;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.davidjo.remedialexercise.data.BodyPart;

@Entity(tableName = "training_table")
public class Training {

    @PrimaryKey(autoGenerate = true)
    private final int id;
    private final int planId;
    private final int minutes;
    private final long createdTime;
    private final BodyPart bodyPart;

    public Training(int id, int planId, int minutes, long createdTime, BodyPart bodyPart) {
        this.id = id;
        this.planId = planId;
        this.minutes = minutes;
        this.createdTime = createdTime;
        this.bodyPart = bodyPart;
    }

    @Ignore
    public Training(int planId, int minutes, long createdTime, BodyPart bodyPart) {
        this.id = 0;
        this.planId = planId;
        this.minutes = minutes;
        this.createdTime = createdTime;
        this.bodyPart = bodyPart;
    }

    public int getId() {
        return id;
    }

    public int getPlanId() {
        return planId;
    }

    public int getMinutes() {
        return minutes;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

}
