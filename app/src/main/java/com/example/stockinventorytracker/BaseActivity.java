package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected void setupBottomNavigation() {
        Button stock = findViewById(R.id.navStock);
        Button history = findViewById(R.id.navHistory);
        Button graph = findViewById(R.id.navGraph);
        Button about = findViewById(R.id.navAbout);

        if (stock != null) stock.setOnClickListener(v -> open(MainActivity.class));
        if (history != null) history.setOnClickListener(v -> open(HistoryActivity.class));
        if (graph != null) graph.setOnClickListener(v -> open(GraphActivity.class));
        if (about != null) about.setOnClickListener(v -> open(AboutActivity.class));
    }

    private void open(Class<?> target) {
        if (!getClass().equals(target)) {
            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
