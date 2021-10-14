package com.holy.deliveryapp.models;

import java.util.Date;
import java.util.List;

public class Order {

    private final String localName;
    private final List<String> orderList;
    private final int totalPrice;
    private final Date date;
    private final String userId;

    public Order() {
        localName = null;
        orderList = null;
        totalPrice = 0;
        date = null;
        userId = null;
    }

    public Order(String localName, List<String> orderList, int totalPrice, Date date, String userId) {
        this.localName = localName;
        this.orderList = orderList;
        this.totalPrice = totalPrice;
        this.date = date;
        this.userId = userId;
    }

    public String getLocalName() {
        return localName;
    }

    public List<String> getOrderList() {
        return orderList;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public Date getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

}
