package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399
public class MonthOption {
    public final int year;
    public final int month;
    private final String label;

    public MonthOption(int year, int month, String label) {
        this.year = year;
        this.month = month;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
