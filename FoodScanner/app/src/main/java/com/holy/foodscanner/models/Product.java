package com.holy.foodscanner.models;

public class Product {

    private final String name;
    private final String type;
    private final String manufacturer;
    private final String shelfLife;

    public Product(String name, String type, String manufacturer, String shelfLife) {
        this.name = name;
        this.type = type;
        this.manufacturer = manufacturer;
        this.shelfLife = shelfLife;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getShelfLife() {
        return shelfLife;
    }

}
