package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    private DatabaseHelper database;
    private LinearLayout historyContainer;
    private TextView historyCountText;
    private Spinner productSpinner;
    private Spinner typeSpinner;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupBottomNavigation();

        database = new DatabaseHelper(this);
        historyContainer = findViewById(R.id.historyContainer);
        historyCountText = findViewById(R.id.historyCountText);
        productSpinner = findViewById(R.id.historyProductSpinner);
        typeSpinner = findViewById(R.id.historyTypeSpinner);
        Button refresh = findViewById(R.id.refreshHistoryButton);

        setupFilters();
        refresh.setOnClickListener(v -> loadHistory());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void setupFilters() {
        products = database.getProducts("");
        List<String> productNames = new ArrayList<>();
        productNames.add("All Products");
        for (Product product : products) productNames.add(product.name);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNames);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        List<String> typeLabels = Arrays.asList("All Adjustments", "Add Stock", "Remove Stock");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeLabels);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    private void loadHistory() {
        historyContainer.removeAllViews();
        long productId = productSpinner.getSelectedItemPosition() == 0 ? -1 : products.get(productSpinner.getSelectedItemPosition() - 1).id;
        String type = typeSpinner.getSelectedItemPosition() == 1 ? "ADD" : typeSpinner.getSelectedItemPosition() == 2 ? "REMOVE" : "ALL";
        List<Adjustment> rows = database.getAdjustments(productId, type);
        historyCountText.setText(rows.size() + (rows.size() == 1 ? " record" : " records"));

        if (rows.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No adjustment records match the selected filters.");
            empty.setGravity(Gravity.CENTER);
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setPadding(dp(12), dp(30), dp(12), dp(30));
            historyContainer.addView(empty);
            return;
        }

        for (Adjustment row : rows) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card);
            card.setPadding(dp(16), dp(14), dp(16), dp(14));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dp(10));
            card.setLayoutParams(params);

            TextView title = new TextView(this);
            String sign = "ADD".equals(row.type) ? "+" : "-";
            title.setText(row.productName + "   " + sign + row.quantity + " units");
            title.setTextSize(16);
            title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            title.setTextColor(getColor("ADD".equals(row.type) ? R.color.green_primary : R.color.red_stock));

            TextView details = new TextView(this);
            details.setText(database.formatTimestamp(row.timestamp) + "\n" +
                    "Stock: " + row.previousStock + " → " + row.newStock + "\n" +
                    "Employee: " + row.employee + "\nReason: " + row.reason);
            details.setTextColor(getColor(R.color.text_primary));
            details.setLineSpacing(0, 1.15f);
            details.setPadding(0, dp(6), 0, 0);

            card.addView(title);
            card.addView(details);
            historyContainer.addView(card);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
