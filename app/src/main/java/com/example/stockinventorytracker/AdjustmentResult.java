package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399
public class AdjustmentResult {
    public final boolean success;
    public final String message;
    public final int previousStock;
    public final int newStock;

    public AdjustmentResult(boolean success, String message, int previousStock, int newStock) {
        this.success = success;
        this.message = message;
        this.previousStock = previousStock;
        this.newStock = newStock;
    }
}
