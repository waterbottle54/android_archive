package com.davidjo.greenworld.data.action;

import java.util.Locale;

public class Action {

    private String id;          // DB 에 저장되는 고유키
    private String userId;      // 작성한 유저 아이디
    private String actionName;  // 행동 이름. 카테고리와 연계된 경우 카테고리 이름
    private int repetitions;    // 반복 횟수
    private long created;       // 작성 시간

    public Action() { }

    public Action(String id, String userId, String actionName, int repetitions, long created) {
        this.id = id;
        this.userId = userId;
        this.actionName = actionName;
        this.repetitions = repetitions;
        this.created = created;
    }

    public Action(String id, String userId, String actionName, int repetitions) {
        this.id = id;
        this.userId = userId;
        this.actionName = actionName;
        this.repetitions = repetitions;
        this.created = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getActionName() {
        return actionName;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setCreated(long created) {
        this.created = created;
    }


    public static String generateId(String userId) {
        return String.format(Locale.getDefault(), "%s#%d", userId, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Action{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", actionName='" + actionName + '\'' +
                ", repetitions=" + repetitions +
                ", created=" + created +
                '}';
    }
}
