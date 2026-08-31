package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class AdjustStockActivity extends BaseActivity {
    private DatabaseHelper database;
    private List<Product> products;
    private Spinner productSpinner;
    private ArrayAdapter<Product> productAdapter;
    private Spinner reasonSpinner;
    private TextView currentStockText;
    private TextView currentSkuText;
    private TextView lastUpdateText;
    private EditText quantityInput;
    private EditText employeeInput;
    private RadioButton addRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_stock);
        setupBottomNavigation();

        database = new DatabaseHelper(this);
        productSpinner = findViewById(R.id.productSpinner);
        reasonSpinner = findViewById(R.id.reasonSpinner);
        currentStockText = findViewById(R.id.currentStockText);
        currentSkuText = findViewById(R.id.currentSkuText);
        lastUpdateText = findViewById(R.id.lastUpdateText);
        quantityInput = findViewById(R.id.quantityInput);
        employeeInput = findViewById(R.id.employeeInput);
        addRadio = findViewById(R.id.addRadio);
        Button updateButton = findViewById(R.id.updateStockButton);

        loadProductSpinner();
        loadReasonSpinner();

        long selectedId = getIntent().getLongExtra("product_id", -1);
        if (selectedId != -1) selectProduct(selectedId);

        productSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateCurrentStock));
        updateButton.setOnClickListener(v -> saveAdjustment());
    }

    private void loadProductSpinner() {
        products = database.getProducts("");
        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);
    }

    private void loadReasonSpinner() {
        List<String> reasons = Arrays.asList(
                "New delivery", "Customer order", "Damaged items", "Customer return",
                "Warehouse transfer", "Stock count correction", "Other / Not specified");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonSpinner.setAdapter(adapter);
    }

    private void selectProduct(long productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).id == productId) {
                productSpinner.setSelection(i);
                break;
            }
        }
    }

    private void updateCurrentStock() {
        Product selected = (Product) productSpinner.getSelectedItem();
        currentStockText.setText(selected == null ? "0 units" : selected.stock + " units");
        currentSkuText.setText(selected == null ? "SKU" : selected.sku);
    }

    private void saveAdjustment() {
        Product selected = (Product) productSpinner.getSelectedItem();
        String quantityText = quantityInput.getText().toString().trim();
        String employee = employeeInput.getText().toString().trim();
        String reason = String.valueOf(reasonSpinner.getSelectedItem());

        if (selected == null) {
            showMessage("Please select a product.");
            return;
        }
        if (TextUtils.isEmpty(quantityText)) {
            showMessage("Quantity cannot be empty.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showMessage("Quantity must be a whole number.");
            return;
        }
        if (quantity <= 0) {
            showMessage("Quantity must be greater than zero.");
            return;
        }
        if (TextUtils.isEmpty(employee)) {
            showMessage("Employee name cannot be empty.");
            return;
        }
        if (!addRadio.isChecked() && quantity > selected.stock) {
            showMessage("Removal cannot exceed the current stock.");
            return;
        }

        AdjustmentResult result = database.adjustStock(selected.id, addRadio.isChecked(), quantity, employee, reason);
        if (!result.success) {
            showMessage(result.message);
            return;
        }

        products.clear();
        products.addAll(database.getProducts(""));
        productAdapter.notifyDataSetChanged();
        selectProduct(selected.id);
        Product refreshed = database.getProduct(selected.id);
        currentStockText.setText(refreshed == null ? "0 units" : refreshed.stock + " units");
        currentSkuText.setText(refreshed == null ? "SKU" : refreshed.sku);
        quantityInput.setText("");

        String action = addRadio.isChecked() ? "Added" : "Removed";
        String summary = action + " " + quantity + " units\n" +
                "Stock: " + result.previousStock + " → " + result.newStock + "\n" +
                "Employee: " + employee;
        lastUpdateText.setText(summary);
        lastUpdateText.setVisibility(View.VISIBLE);

        new AlertDialog.Builder(this)
                .setTitle("Stock Update Successful")
                .setMessage(summary + "\nReason: " + reason)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
