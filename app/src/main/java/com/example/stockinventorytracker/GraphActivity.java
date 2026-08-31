package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class GraphActivity extends BaseActivity {
    private DatabaseHelper database;
    private Spinner productSpinner;
    private Spinner monthSpinner;
    private StockChartView chartView;
    private TextView openingText;
    private TextView highestText;
    private TextView lowestText;
    private TextView closingText;
    private TextView noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setupBottomNavigation();

        database = new DatabaseHelper(this);
        productSpinner = findViewById(R.id.graphProductSpinner);
        monthSpinner = findViewById(R.id.graphMonthSpinner);
        chartView = findViewById(R.id.stockChartView);
        openingText = findViewById(R.id.openingStockText);
        highestText = findViewById(R.id.highestStockText);
        lowestText = findViewById(R.id.lowestStockText);
        closingText = findViewById(R.id.closingStockText);
        noteText = findViewById(R.id.graphNoteText);

        List<Product> products = database.getProducts("");
        ArrayAdapter<Product> productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        List<MonthOption> months = database.getMonthOptions();
        ArrayAdapter<MonthOption> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        productSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::loadGraph));
        monthSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::loadGraph));
    }

    private void loadGraph() {
        Product product = (Product) productSpinner.getSelectedItem();
        MonthOption month = (MonthOption) monthSpinner.getSelectedItem();
        if (product == null || month == null) return;

        GraphData data = database.getGraphData(product.id, month.year, month.month);
        chartView.setData(data.days, data.stockLevels);
        openingText.setText(String.valueOf(data.openingStock));
        highestText.setText(String.valueOf(data.highestStock));
        lowestText.setText(String.valueOf(data.lowestStock));
        closingText.setText(String.valueOf(data.closingStock));
        noteText.setText("End-of-day stock level for " + product.name + " in " + month + ".");
    }
}
