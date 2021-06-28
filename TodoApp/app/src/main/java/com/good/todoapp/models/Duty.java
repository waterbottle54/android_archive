package com.good.todoapp.models;

import java.time.LocalDate;

// 할 일을 표현하는 간단한 클래스

public class Duty {

    // 데이터베이스 상에서의 고유한 ID
    private int id;
    // 이름
    private String name;
    // 완료 여부
    private boolean isCompleted;
    // 마감 날짜
    private LocalDate date;
    // 중요도
    private boolean isImportant;

    // 생성자
    public Duty(int id, String name, boolean isCompleted, LocalDate date, boolean isImportant) {
        init(id, name, isCompleted, date, isImportant);
    }

    public Duty(String name, boolean isCompleted, LocalDate date, boolean isImportant) {
        init(-1, name, isCompleted, date, isImportant);
    }

    // 생성자 헬퍼 함수
    private void init(int id, String name, boolean isCompleted, LocalDate date, boolean isImportant) {
        this.id = id;
        this.name = name;
        this.isCompleted = isCompleted;
        this.date = date;
        this.isImportant = isImportant;
    }

    // 접근자 (액세서)

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

}
