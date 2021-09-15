package com.cool.nfckiosk.data.menu;

import java.util.Objects;

public class Menu {

    private String id;
    private String userId;
    private String name;
    private int price;
    private String imageUrl;

    public Menu() {
    }

    public Menu(String userId, String name, int price, String imageUrl) {
        this.id = userId + "#" + name;
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return price == menu.price && id.equals(menu.id) && userId.equals(menu.userId) && name.equals(menu.name) && Objects.equals(imageUrl, menu.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, price, imageUrl);
    }
}
