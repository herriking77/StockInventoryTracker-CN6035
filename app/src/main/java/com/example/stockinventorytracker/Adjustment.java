package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399
public class Adjustment {
    public final long id;
    public final long productId;
    public final String productName;
    public final String type;
    public final int quantity;
    public final int previousStock;
    public final int newStock;
    public final String employee;
    public final String reason;
    public final long timestamp;

    public Adjustment(long id, long productId, String productName, String type, int quantity,
                      int previousStock, int newStock, String employee, String reason, long timestamp) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.employee = employee;
        this.reason = reason;
        this.timestamp = timestamp;
    }
}
