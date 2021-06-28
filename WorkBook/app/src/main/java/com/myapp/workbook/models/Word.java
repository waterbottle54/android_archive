package com.myapp.workbook.models;

public class Word {

    private final int id;
    // 철자
    private final String spelling;
    // 뜻
    private final String meaning;

    public Word(int id, String spelling, String meaning) {
        this.id = id;
        this.spelling = spelling;
        this.meaning = meaning;
    }

    public Word(String spelling, String meaning) {
        this.id = -1;
        this.spelling = spelling;
        this.meaning = meaning;
    }

    public int getId() {
        return id;
    }

    public String getSpelling() {
        return spelling;
    }

    public String getMeaning() {
        return meaning;
    }

}
