package com.good.memoapp.models;

public class Memo {

    // 메모 번호
    private int id;
    // 메모 제목
    private String title;
    // 메모 내용
    private String content;

    // 빈 생성자
    public Memo() {
    }

    // 기본 생성자
    public Memo(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // 기본 생성자 (내용만)
    public Memo(String title, String content) {
        this.id = -1;
        this.title = title;
        this.content = content;
    }

    // 접근자
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
