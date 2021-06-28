package com.holy.exercise.models;

import java.time.LocalDate;

public class Record {

    private int id;
    private String title;
    private LocalDate date;
    private int seconds;

    public Record(int id, String title, LocalDate date, int seconds) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.seconds = seconds;
    }

    public Record(String title, LocalDate date, int seconds) {
        this.id = -1;
        this.title = title;
        this.date = date;
        this.seconds = seconds;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getSeconds() {
        return seconds;
    }

}
