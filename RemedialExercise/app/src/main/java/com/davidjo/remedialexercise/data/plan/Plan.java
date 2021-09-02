package com.davidjo.remedialexercise.data.plan;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.davidjo.remedialexercise.data.BodyPart;

import java.io.Serializable;

@Entity(tableName = "plan_table")
public class Plan implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private final int id;
    private long startTime;
    private long endTime;
    private int repetitions;
    private int minutesPerRepetition;
    private BodyPart bodyPart;
    private boolean closed;

    public Plan(int id, long startTime, long endTime, int repetitions, int minutesPerRepetition, BodyPart bodyPart, boolean closed) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repetitions = repetitions;
        this.minutesPerRepetition = minutesPerRepetition;
        this.bodyPart = bodyPart;
        this.closed = closed;
    }

    @Ignore
    public Plan(long startTime, long endTime,
                int repetitions, int minutesPerRepetition,
                BodyPart bodyPart, boolean closed
    ) {
        this.id = 0;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repetitions = repetitions;
        this.minutesPerRepetition = minutesPerRepetition;
        this.bodyPart = bodyPart;
        this.closed = closed;
    }

    public int getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public int getMinutesPerRepetition() {
        return minutesPerRepetition;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setMinutesPerRepetition(int minutesPerRepetition) {
        this.minutesPerRepetition = minutesPerRepetition;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
