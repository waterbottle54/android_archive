package com.holy.exercise.models;

import java.time.LocalDate;
import java.util.Locale;

public class Post {

    private String id;
    private String title;
    private String writer;
    private String password;
    private String contents;
    private String date;


    public Post() { }

    public Post(String id, String title, String writer, String password, String contents, String date) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.password = password;
        this.contents = contents;
        this.date = date;
    }

    public Post(String title, String writer, String password, String contents, String date) {
        this.id = String.format(Locale.getDefault(),
                "%s#%s", writer, date.toString());
        this.title = title;
        this.writer = writer;
        this.password = password;
        this.contents = contents;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getWriter() {
        return writer;
    }

    public String getPassword() {
        return password;
    }

    public String getContents() {
        return contents;
    }

    public String getDate() {
        return date;
    }

}
