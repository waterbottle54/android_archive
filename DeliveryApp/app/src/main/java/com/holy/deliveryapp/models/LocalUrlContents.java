package com.holy.deliveryapp.models;

import java.util.List;

public class LocalUrlContents {

    private final String status;
    private final List<String> timeList;
    private final List<Menu> menuList;

    public LocalUrlContents(String status, List<String> timeList, List<Menu> menuList) {
        this.status = status;
        this.timeList = timeList;
        this.menuList = menuList;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }
}
