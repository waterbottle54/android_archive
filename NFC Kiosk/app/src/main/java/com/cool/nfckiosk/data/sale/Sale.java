package com.cool.nfckiosk.data.sale;

public class Sale {

    private String id;
    private String adminId;
    private int price;
    private long created;

    public Sale() {
    }

    public Sale(String adminId, int price) {
        this.adminId = adminId;
        this.price = price;
        this.created = System.currentTimeMillis();
        this.id = adminId + "#" + created;
    }

    public String getId() {
        return id;
    }

    public String getAdminId() {
        return adminId;
    }

    public int getPrice() {
        return price;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCreated(long created) {
        this.created = created;
    }

}
