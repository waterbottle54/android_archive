package com.holy.watchdog.models;


public class Criminal {

    private final int id;
    private final String nickname;


    public Criminal(String nickname) {
        this.id = -1;
        this.nickname = nickname;
    }

    public Criminal(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

}
