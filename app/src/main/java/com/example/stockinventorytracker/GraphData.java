package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import java.util.List;

public class GraphData {
    public final List<Integer> days;
    public final List<Integer> stockLevels;
    public final int openingStock;
    public final int highestStock;
    public final int lowestStock;
    public final int closingStock;

    public GraphData(List<Integer> days, List<Integer> stockLevels, int openingStock,
                     int highestStock, int lowestStock, int closingStock) {
        this.days = days;
        this.stockLevels = stockLevels;
        this.openingStock = openingStock;
        this.highestStock = highestStock;
        this.lowestStock = lowestStock;
        this.closingStock = closingStock;
    }
}
