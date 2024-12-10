package org.easytech.order;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private DBHelper dbHelper;

    public OrderManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Δημιουργία νέας παραγγελίας
    public long createOrder(int tableId, double orderTotal, String notes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("table_id", tableId);
        values.put("order_total", orderTotal);
        values.put("notes", notes);
        return db.insert("orders", null, values);
    }

    // Εισαγωγή λεπτομερειών παραγγελίας
    public void addOrderDetail(long orderId, int productId, int quantity, double price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("product_id", productId);
        values.put("quantity", quantity);
        values.put("price", price);
        db.insert("order_details", null, values);
    }

    // Ανάγνωση παραγγελιών
    @SuppressLint("Range")
    public List<Order> getOrders() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Order> orders = new ArrayList<>();
        Cursor cursor = db.query("orders", null, null, null, null, null, "timestamp DESC");
        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
            order.setTableId(cursor.getInt(cursor.getColumnIndex("table_id")));
            order.setOrderTotal(cursor.getDouble(cursor.getColumnIndex("order_total")));
            order.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            orders.add(order);
        }
        cursor.close();
        return orders;
    }

    // Ανάγνωση λεπτομερειών παραγγελίας
    @SuppressLint("Range")
    public List<OrderDetail> getOrderDetails(long orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<OrderDetail> details = new ArrayList<>();
        Cursor cursor = db.query("order_details", null, "order_id = ?",
                new String[]{String.valueOf(orderId)}, null, null, null);
        while (cursor.moveToNext()) {
            OrderDetail detail = new OrderDetail();
            detail.setDetailId(cursor.getLong(cursor.getColumnIndex("detail_id")));
            detail.setOrderId(cursor.getLong(cursor.getColumnIndex("order_id")));
            detail.setProductId(cursor.getInt(cursor.getColumnIndex("product_id")));
            detail.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
            detail.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            details.add(detail);
        }
        cursor.close();
        return details;
    }

    // Ενημέρωση κατάστασης εκτύπωσης παραγγελίας
    public void markOrderAsPrinted(long orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_printed", 1);
        db.update("orders", values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }
}
