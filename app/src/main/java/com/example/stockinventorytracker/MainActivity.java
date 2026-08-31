package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends BaseActivity {
    private DatabaseHelper database;
    private LinearLayout productContainer;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation();

        database = new DatabaseHelper(this);
        productContainer = findViewById(R.id.productContainer);
        searchInput = findViewById(R.id.searchInput);
        Button openAdjust = findViewById(R.id.openAdjustButton);

        openAdjust.setOnClickListener(v -> startActivity(new Intent(this, AdjustStockActivity.class)));
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refreshProducts(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSummary();
        refreshProducts(searchInput == null ? "" : searchInput.getText().toString());
    }

    private void refreshSummary() {
        ((TextView) findViewById(R.id.totalProductsText)).setText(String.valueOf(database.getProductCount()));
        ((TextView) findViewById(R.id.lowStockText)).setText(String.valueOf(database.getLowStockCount()));
        ((TextView) findViewById(R.id.adjustmentsText)).setText(String.valueOf(database.getAdjustmentCountToday()));
    }

    private void refreshProducts(String search) {
        if (productContainer == null) return;
        productContainer.removeAllViews();
        List<Product> products = database.getProducts(search);

        if (products.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No matching products found.");
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(12), dp(28), dp(12), dp(28));
            productContainer.addView(empty);
            return;
        }

        for (Product product : products) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(16), dp(14), dp(16), dp(14));
            row.setBackgroundResource(R.drawable.bg_card);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, dp(10));
            row.setLayoutParams(rowParams);

            TextView name = new TextView(this);
            name.setText(product.name + "\n" + product.sku);
            name.setTextSize(15);
            name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setTextColor(getColor(R.color.text_primary));
            name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView stock = new TextView(this);
            stock.setText(product.stock + " units");
            stock.setTextSize(17);
            stock.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            stock.setTextColor(getColor(product.stock <= product.lowThreshold ? R.color.red_stock : R.color.green_primary));

            row.addView(name);
            row.addView(stock);
            row.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdjustStockActivity.class);
                intent.putExtra("product_id", product.id);
                startActivity(intent);
            });
            productContainer.addView(row);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
