package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.os.Bundle;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupBottomNavigation();
    }
}
