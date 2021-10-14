package com.holy.deliveryapp.models;

public class UserData {

    private String uid = null;
    private String name = null;
    private String phone = null;
    private String address = null;
    private int balance = 0;

    public UserData() {
    }

    public UserData(String uid, String name, String phone, String address) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.balance = 0;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public int getBalance() {
        return balance;
    }
}
