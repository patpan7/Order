package org.easytech.order;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    String TABLE_CATEGORIES = "categories";
    String TABLE_PRODUCTS = "products";
    String TABLE_TABLES = "tables";
    String TABLE_ORDERS = "orders";
    String TABLE_ORDERDETAILS = "order_details";
    String TABLE_PRINTQUEUE = "print_queue";

    public DBHelper(Context context) {
        super(context, "Orders", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " (\n" +
                "    cat_id INTEGER PRIMARY KEY,\n" +
                        "    cat_name TEXT,\n" +
                        "    cat_status INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                "    prod_id INTEGER PRIMARY KEY,\n" +
                "    prod_name TEXT,\n" +
                "    prod_price FLOAT,\n" +
                "    prod_cat INTEGER,\n" +
                "    prod_status INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TABLES + " (\n" +
                "    table_id INTEGER PRIMARY KEY,\n" +
                "    table_name TEXT,\n" +
                "    status INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "table_id INTEGER, " +
                "order_total REAL, " +
                "timestamp TEXT, " +
                "print_status INTEGER DEFAULT 0, " +
                "is_ontable  INTEGER DEFAULT 1,"+
                "is_synced  INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ORDERDETAILS + "(" +
                "detail_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "is_synced INTEGER DEFAULT 0, " +
                "FOREIGN KEY(order_id) REFERENCES orders(order_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TABLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERDETAILS);
        onCreate(db);
    }

    public void addTables(List<Table> tables) {
        for (Table table : tables) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("table_id", table.getTable_id());
            values.put("table_name", table.getTable_name());
            values.put("status", table.getStatus());

            int rows = db.update(TABLE_TABLES, values, "table_id = ?", new String[]{String.valueOf(table.getTable_id())});
            if (rows == 0) {
                db.insert(TABLE_TABLES, null, values);
            }
        }
    }

    public void addCategories(List<Category> categories) {
        for (Category category : categories) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("cat_id", category.getCat_id());
            values.put("cat_name", category.getCat_name());
            values.put("cat_status", category.getCat_status());

            int rows = db.update(TABLE_CATEGORIES, values, "cat_id = ?", new String[]{String.valueOf(category.getCat_id())});
            if (rows == 0) {
                db.insert(TABLE_CATEGORIES, null, values);
            }
        }
    }

    public void addProducts(List<Product> products) {
        for (Product product : products) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("prod_id", product.getProd_id());
            values.put("prod_name", product.getProd_name());
            values.put("prod_price", product.getProd_price());
            values.put("prod_cat", product.getProd_cat());
            values.put("prod_status", product.getProd_status());

            int rows = db.update(TABLE_PRODUCTS, values, "prod_id = ?", new String[]{String.valueOf(product.getProd_id())});
            if (rows == 0) {
                db.insert(TABLE_PRODUCTS, null, values);
            }
        }
    }

    public List<Table> getTables() {
        List<Table> tables = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_TABLES +" order by table_id", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Table table = new Table(
                        cursor.getInt(cursor.getColumnIndex("table_id")),
                        cursor.getString(cursor.getColumnIndex("table_name")),
                        cursor.getInt(cursor.getColumnIndex("status"))
                );
                tables.add(table);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tables;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Category category = new Category(
                        cursor.getInt(cursor.getColumnIndex("cat_id")),
                        cursor.getString(cursor.getColumnIndex("cat_name")),
                        cursor.getInt(cursor.getColumnIndex("cat_status"))
                );
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public List<Product> getProducts(int category) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE prod_cat = " + category, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Product product = new Product(
                        cursor.getInt(cursor.getColumnIndex("prod_id")),
                        cursor.getString(cursor.getColumnIndex("prod_name")),
                        cursor.getDouble(cursor.getColumnIndex("prod_price")),
                        cursor.getInt(cursor.getColumnIndex("prod_cat")),
                        cursor.getInt(cursor.getColumnIndex("prod_status"))
                );
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    // Δημιουργία παραγγελίας
    public int insertOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("table_id", order.getTableId());
        values.put("order_total", order.getOrderTotal());
        values.put("timestamp", order.getTimestamp());

        long id = db.insert(TABLE_ORDERS, null, values);
        return (int) id; // Επιστρέφει το ID της παραγγελίας
    }

    // Προσθήκη λεπτομέρειας παραγγελίας
    public void insertOrderDetail(OrderDetail orderDetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderDetail.getOrderId());
        values.put("product_id", orderDetail.getProductId());
        values.put("quantity", orderDetail.getQuantity());
        values.put("price", orderDetail.getPrice());

        db.insert(TABLE_ORDERDETAILS, null, values);
    }


    // Λήψη παραγγελιών
    @SuppressLint("Range")
    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                order.setTableId(cursor.getInt(cursor.getColumnIndex("table_id")));
                order.setOrderTotal(cursor.getDouble(cursor.getColumnIndex("order_total")));
                order.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
                order.setIsOnTable(cursor.getInt(cursor.getColumnIndex("is_ontable")));
                orders.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return orders;
    }

    // Λήψη λεπτομερειών παραγγελίας
    @SuppressLint("Range")
    public List<OrderDetail> getOrderDetails(long orderId) {
        List<OrderDetail> details = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERDETAILS + " WHERE order_id = ?",
                new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            do {
                OrderDetail detail = new OrderDetail();
                detail.setDetailId(cursor.getLong(cursor.getColumnIndex("detail_id")));
                detail.setOrderId(cursor.getLong(cursor.getColumnIndex("order_id")));
                detail.setProductId(cursor.getInt(cursor.getColumnIndex("product_id")));
                detail.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                detail.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                details.add(detail);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return details;
    }

    @SuppressLint("Range")
    public List<Order> getUnsyncedOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE is_synced = 0";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                order.setTableId(cursor.getInt(cursor.getColumnIndex("table_id")));
                order.setOrderTotal(cursor.getDouble(cursor.getColumnIndex("order_total")));
                order.setTimestamp(String.valueOf(cursor.getLong(cursor.getColumnIndex("timestamp"))));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    @SuppressLint("Range")
    public List<OrderDetail> getUnsyncedOrderDetails() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ORDERDETAILS + " WHERE is_synced = 0";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                OrderDetail detail = new OrderDetail();
                detail.setDetailId(cursor.getInt(cursor.getColumnIndex("detail_id")));
                detail.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                detail.setProductId(cursor.getInt(cursor.getColumnIndex("product_id")));
                detail.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                detail.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                orderDetails.add(detail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderDetails;
    }


    @SuppressLint("Range")
    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ORDERDETAILS + " WHERE order_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            do {
                OrderDetail detail = new OrderDetail();
                detail.setDetailId(cursor.getInt(cursor.getColumnIndex("detail_id")));
                detail.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                detail.setProductId(cursor.getInt(cursor.getColumnIndex("product_id")));
                detail.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                detail.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                orderDetails.add(detail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderDetails;
    }

    public void markOrderAsSynced(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_synced", 1); // 1 = Συγχρονισμένη

        db.update(TABLE_ORDERS, values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }

    public void markOrderDetailsAsSynced(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_synced", 1); // 1 = Συγχρονισμένη

        db.update(TABLE_ORDERDETAILS, values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }


    public boolean tableSetStatus(int tableid, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);  // Ενημέρωση του status

        // Ενημέρωση της κατάστασης του τραπεζιού
        int rowsAffected = db.update(TABLE_TABLES, values, "table_id = ?", new String[]{String.valueOf(tableid)});
        db.close();

        return rowsAffected > 0;
    }


    public void updatePrintStatus(int orderId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("print_status", status);
        db.update(TABLE_ORDERS, values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }

    public List<Order> getPendingPrintOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Orders", null, "print_status = 0", null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
            int tableId = cursor.getInt(cursor.getColumnIndexOrThrow("table_id"));
            double orderTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("order_total"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            orders.add(new Order(id, tableId, orderTotal, timestamp, 0)); // Δημιουργία αντικειμένου Order
        }
        cursor.close();
        return orders;
    }

    @SuppressLint("Range")
    public List<Product> getUnprintedOrderItems(int orderId) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.prod_id, p.prod_name, od.quantity, od.price " +
                "FROM order_details od " +
                "JOIN products p ON od.product_id = p.prod_id " +
                "JOIN orders o ON od.order_id = o.order_id " +
                "WHERE o.print_status = 0 AND o.order_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
               int prodId = cursor.getInt(cursor.getColumnIndex("prod_id"));
                String prodName = cursor.getString(cursor.getColumnIndex("prod_name"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndex("price"));

                Product product = new Product(prodId, prodName, price, quantity);
                productList.add(product);
            }
            cursor.close();
        }

        return productList;
    }


    public void orderSetStatus(int tableId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_ontable", status);
        db.update(TABLE_ORDERS, values, "table_id = ?", new String[]{String.valueOf(tableId)});
    }

    @SuppressLint("Range")
    public String getTableName(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tableName = null;

        // Κάνουμε JOIN στον πίνακα orders με τον πίνακα tables για να πάρουμε το table_name
        String query = "SELECT t.table_name FROM "+TABLE_TABLES+" t " +
                "JOIN "+TABLE_ORDERS+" o ON t.table_id = o.table_id " +
                "WHERE o.order_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor != null && cursor.moveToFirst()) {
            tableName = cursor.getString(cursor.getColumnIndex("table_name"));
            cursor.close();
        }

        return tableName; // Επιστρέφει το όνομα του τραπεζιού ή null αν δεν βρεθεί η παραγγελία
    }
}
