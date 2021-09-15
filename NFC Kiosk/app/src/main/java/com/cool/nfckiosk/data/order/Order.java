package com.cool.nfckiosk.data.order;

import com.cool.nfckiosk.data.menu.Menu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order implements Serializable {

    private String id;
    private String adminId;
    private int tableNumber;
    private Map<String, Integer> contents;
    private int price;
    private String message;
    private long created;

    public Order() {
    }

    public Order(String adminId, int tableNumber, Map<String, Integer> contents, int price, String message) {
        this.adminId = adminId;
        this.tableNumber = tableNumber;
        this.contents = contents;
        this.price = price;
        this.message = message;
        this.created = System.currentTimeMillis();
        this.id = adminId + "#" + tableNumber + "#" + created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setContents(Map<String, Integer> contents) {
        this.contents = contents;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getAdminId() {
        return adminId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Map<String, Integer> getContents() {
        return contents;
    }

    public int getPrice() {
        return price;
    }

    public String getMessage() {
        return message;
    }

    public long getCreated() {
        return created;
    }


    public void add(Menu menu, int count) {
        price += menu.getPrice() * count;
        contents.merge(menu.getId(), count, Integer::sum);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return tableNumber == order.tableNumber && price == order.price && created == order.created && id.equals(order.id) && adminId.equals(order.adminId) && contents.equals(order.contents) && Objects.equals(message, order.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, adminId, tableNumber, contents, price, message, created);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", adminId='" + adminId + '\'' +
                ", tableNumber=" + tableNumber +
                ", contents=" + contents +
                ", price=" + price +
                ", message='" + message + '\'' +
                ", created=" + created +
                '}';
    }
}
