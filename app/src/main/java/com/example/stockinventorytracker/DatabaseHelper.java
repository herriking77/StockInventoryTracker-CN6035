package com.example.stockinventorytracker;

// Name: PAN YAOXIANG, SID: S1041399

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "stock_inventory.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "sku TEXT NOT NULL UNIQUE," +
                "stock INTEGER NOT NULL CHECK(stock >= 0)," +
                "low_threshold INTEGER NOT NULL CHECK(low_threshold >= 0))");

        db.execSQL("CREATE TABLE adjustments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER NOT NULL," +
                "type TEXT NOT NULL CHECK(type IN ('ADD','REMOVE'))," +
                "quantity INTEGER NOT NULL CHECK(quantity > 0)," +
                "previous_stock INTEGER NOT NULL," +
                "new_stock INTEGER NOT NULL," +
                "employee TEXT NOT NULL," +
                "reason TEXT NOT NULL," +
                "timestamp INTEGER NOT NULL," +
                "FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE)");

        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS adjustments");
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        long iphone = insertProduct(db, "iPhone 16 Pro", "ELEC-001", 18, 10);
        long macbook = insertProduct(db, "MacBook Air", "ELEC-002", 7, 8);
        long airpods = insertProduct(db, "AirPods Pro", "ELEC-003", 46, 12);
        long ipad = insertProduct(db, "iPad Pro", "ELEC-004", 20, 10);
        long watch = insertProduct(db, "Apple Watch Series 9", "ELEC-005", 8, 8);
        long samsung = insertProduct(db, "Samsung S24", "ELEC-006", 23, 10);
        long sony = insertProduct(db, "Sony WH-1000XM5", "ELEC-007", 12, 8);
        long dell = insertProduct(db, "Dell XPS 13", "ELEC-008", 10, 8);

        applySeedAdjustment(db, iphone, true, 8, "John Tan", "New delivery", 27);
        applySeedAdjustment(db, iphone, false, 4, "Mary Lee", "Customer order", 21);
        applySeedAdjustment(db, iphone, true, 10, "David Lim", "Warehouse transfer", 14);
        applySeedAdjustment(db, iphone, false, 7, "John Tan", "Customer order", 6);
        applySeedAdjustment(db, macbook, true, 5, "David Lim", "New delivery", 13);
        applySeedAdjustment(db, airpods, false, 3, "Mary Lee", "Damaged items", 12);
        applySeedAdjustment(db, ipad, false, 2, "John Tan", "Customer order", 11);
        applySeedAdjustment(db, watch, false, 1, "David Lim", "Damaged items", 10);
        applySeedAdjustment(db, samsung, true, 8, "Mary Lee", "New delivery", 9);
        applySeedAdjustment(db, sony, false, 3, "John Tan", "Customer order", 8);
        applySeedAdjustment(db, dell, true, 4, "David Lim", "Stock count correction", 7);
    }

    private long insertProduct(SQLiteDatabase db, String name, String sku, int stock, int threshold) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("sku", sku);
        values.put("stock", stock);
        values.put("low_threshold", threshold);
        return db.insertOrThrow("products", null, values);
    }

    private void applySeedAdjustment(SQLiteDatabase db, long productId, boolean add, int quantity,
                                     String employee, String reason, int daysAgo) {
        Product product = getProduct(db, productId);
        if (product == null) return;
        int newStock = add ? product.stock + quantity : product.stock - quantity;
        long timestamp = System.currentTimeMillis() - (daysAgo * 24L * 60L * 60L * 1000L);
        updateProductStock(db, productId, newStock);
        insertAdjustment(db, productId, add ? "ADD" : "REMOVE", quantity,
                product.stock, newStock, employee, reason, timestamp);
    }

    public List<Product> getProducts(String search) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id, name, sku, stock, low_threshold FROM products";
        String[] args = null;
        if (search != null && !search.trim().isEmpty()) {
            sql += " WHERE name LIKE ? OR sku LIKE ?";
            String term = "%" + search.trim() + "%";
            args = new String[]{term, term};
        }
        sql += " ORDER BY name COLLATE NOCASE";
        try (Cursor cursor = db.rawQuery(sql, args)) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }
        return products;
    }

    public Product getProduct(long id) {
        return getProduct(getReadableDatabase(), id);
    }

    private Product getProduct(SQLiteDatabase db, long id) {
        try (Cursor cursor = db.rawQuery(
                "SELECT id, name, sku, stock, low_threshold FROM products WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            return cursor.moveToFirst() ? readProduct(cursor) : null;
        }
    }

    private Product readProduct(Cursor cursor) {
        return new Product(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getInt(4));
    }

    public int getProductCount() {
        return getCount("SELECT COUNT(*) FROM products", null);
    }

    public int getLowStockCount() {
        return getCount("SELECT COUNT(*) FROM products WHERE stock <= low_threshold", null);
    }

    public int getAdjustmentCountToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return getCount("SELECT COUNT(*) FROM adjustments WHERE timestamp >= ?",
                new String[]{String.valueOf(calendar.getTimeInMillis())});
    }

    private int getCount(String sql, String[] args) {
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, args)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public AdjustmentResult adjustStock(long productId, boolean add, int quantity,
                                        String employee, String reason) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Product product = getProduct(db, productId);
            if (product == null) {
                return new AdjustmentResult(false, "Selected product was not found.", 0, 0);
            }
            if (quantity <= 0) {
                return new AdjustmentResult(false, "Quantity must be greater than zero.", product.stock, product.stock);
            }
            int newStock = add ? product.stock + quantity : product.stock - quantity;
            if (newStock < 0) {
                return new AdjustmentResult(false, "Removal cannot exceed the current stock.", product.stock, product.stock);
            }

            updateProductStock(db, productId, newStock);
            insertAdjustment(db, productId, add ? "ADD" : "REMOVE", quantity,
                    product.stock, newStock, employee.trim(), reason.trim(), System.currentTimeMillis());
            db.setTransactionSuccessful();
            return new AdjustmentResult(true, "Stock updated and adjustment logged.", product.stock, newStock);
        } catch (RuntimeException ex) {
            return new AdjustmentResult(false, "The stock update could not be saved.", 0, 0);
        } finally {
            db.endTransaction();
        }
    }

    private void updateProductStock(SQLiteDatabase db, long productId, int newStock) {
        ContentValues values = new ContentValues();
        values.put("stock", newStock);
        int count = db.update("products", values, "id = ?", new String[]{String.valueOf(productId)});
        if (count != 1) throw new IllegalStateException("Product update failed");
    }

    private void insertAdjustment(SQLiteDatabase db, long productId, String type, int quantity,
                                  int previousStock, int newStock, String employee,
                                  String reason, long timestamp) {
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("type", type);
        values.put("quantity", quantity);
        values.put("previous_stock", previousStock);
        values.put("new_stock", newStock);
        values.put("employee", employee);
        values.put("reason", reason == null || reason.trim().isEmpty() ? "Not specified" : reason.trim());
        values.put("timestamp", timestamp);
        long id = db.insertOrThrow("adjustments", null, values);
        if (id == -1) throw new IllegalStateException("Adjustment log insert failed");
    }

    public List<Adjustment> getAdjustments(long productId, String type) {
        List<Adjustment> rows = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.product_id, p.name, a.type, a.quantity, a.previous_stock, " +
                        "a.new_stock, a.employee, a.reason, a.timestamp " +
                        "FROM adjustments a JOIN products p ON p.id = a.product_id WHERE 1=1");
        List<String> args = new ArrayList<>();
        if (productId > 0) {
            sql.append(" AND a.product_id = ?");
            args.add(String.valueOf(productId));
        }
        if (type != null && !type.equals("ALL")) {
            sql.append(" AND a.type = ?");
            args.add(type);
        }
        sql.append(" ORDER BY a.timestamp DESC, a.id DESC");

        try (Cursor cursor = getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                rows.add(new Adjustment(
                        cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7),
                        cursor.getString(8), cursor.getLong(9)));
            }
        }
        return rows;
    }

    public List<MonthOption> getMonthOptions() {
        Map<String, MonthOption> options = new LinkedHashMap<>();
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        SimpleDateFormat labelFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        Calendar current = Calendar.getInstance();
        String currentKey = keyFormat.format(current.getTime());
        options.put(currentKey, new MonthOption(current.get(Calendar.YEAR), current.get(Calendar.MONTH), labelFormat.format(current.getTime())));

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT DISTINCT timestamp FROM adjustments ORDER BY timestamp DESC", null)) {
            while (cursor.moveToNext()) {
                Date date = new Date(cursor.getLong(0));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                String key = keyFormat.format(date);
                if (!options.containsKey(key)) {
                    options.put(key, new MonthOption(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), labelFormat.format(date)));
                }
            }
        }
        return new ArrayList<>(options.values());
    }

    public GraphData getGraphData(long productId, int year, int month) {
        Product product = getProduct(productId);
        if (product == null) {
            return new GraphData(Collections.singletonList(1), Collections.singletonList(0), 0, 0, 0, 0);
        }

        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(year, month, 1, 0, 0, 0);
        long startMs = start.getTimeInMillis();

        Calendar end = (Calendar) start.clone();
        int daysInMonth = end.getActualMaximum(Calendar.DAY_OF_MONTH);
        end.set(Calendar.DAY_OF_MONTH, daysInMonth);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        long endMs = end.getTimeInMillis();

        int closingStock = product.stock;
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT type, quantity FROM adjustments WHERE product_id = ? AND timestamp > ? ORDER BY timestamp DESC, id DESC",
                new String[]{String.valueOf(productId), String.valueOf(endMs)})) {
            while (cursor.moveToNext()) {
                closingStock = reverseAdjustment(closingStock, cursor.getString(0), cursor.getInt(1));
            }
        }

        int openingStock = closingStock;
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT type, quantity FROM adjustments WHERE product_id = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp DESC, id DESC",
                new String[]{String.valueOf(productId), String.valueOf(startMs), String.valueOf(endMs)})) {
            while (cursor.moveToNext()) {
                openingStock = reverseAdjustment(openingStock, cursor.getString(0), cursor.getInt(1));
            }
        }

        Map<Integer, List<int[]>> dailyChanges = new HashMap<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT type, quantity, timestamp FROM adjustments WHERE product_id = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp, id",
                new String[]{String.valueOf(productId), String.valueOf(startMs), String.valueOf(endMs)})) {
            while (cursor.moveToNext()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cursor.getLong(2));
                int day = c.get(Calendar.DAY_OF_MONTH);
                int direction = "ADD".equals(cursor.getString(0)) ? 1 : -1;
                dailyChanges.computeIfAbsent(day, key -> new ArrayList<>()).add(new int[]{direction, cursor.getInt(1)});
            }
        }

        List<Integer> days = new ArrayList<>();
        List<Integer> levels = new ArrayList<>();
        int stock = openingStock;
        int highest = openingStock;
        int lowest = openingStock;
        for (int day = 1; day <= daysInMonth; day++) {
            List<int[]> changes = dailyChanges.get(day);
            if (changes != null) {
                for (int[] change : changes) stock += change[0] * change[1];
            }
            days.add(day);
            levels.add(stock);
            highest = Math.max(highest, stock);
            lowest = Math.min(lowest, stock);
        }
        return new GraphData(days, levels, openingStock, highest, lowest, closingStock);
    }

    private int reverseAdjustment(int stockAfter, String type, int quantity) {
        return "ADD".equals(type) ? stockAfter - quantity : stockAfter + quantity;
    }

    public String formatTimestamp(long timestamp) {
        return new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(timestamp));
    }
}
