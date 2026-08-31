package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399
public class Product {
    public final long id;
    public final String name;
    public final String sku;
    public final int stock;
    public final int lowThreshold;

    public Product(long id, String name, String sku, int stock, int lowThreshold) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.stock = stock;
        this.lowThreshold = lowThreshold;
    }

    @Override
    public String toString() {
        return name;
    }
}
