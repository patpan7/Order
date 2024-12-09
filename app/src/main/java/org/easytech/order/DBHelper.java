package org.easytech.order;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Orders", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CATEGORIES = "CREATE TABLE IF NOT EXISTS categories (\n" +
                "    cat_id INTEGER PRIMARY KEY,\n" +
                "    cat_name TEXT,\n" +
                "    cat_status INTEGER)";
        db.execSQL(CREATE_TABLE_CATEGORIES);

        String CREATE_TABLE_PRODUCTS = "CREATE TABLE IF NOT EXISTS products (" +
                "    prod_id INTEGER PRIMARY KEY,\n" +
                "    prod_name TEXT,\n" +
                "    prod_price FLOAT,\n" +
                "    prod_cat INTEGER,\n" +
                "    prod_status INTEGER)";
        db.execSQL(CREATE_TABLE_PRODUCTS);

        String CREATE_TABLE_TABLES = "CREATE TABLE IF NOT EXISTS tables (\n" +
                "    table_id INTEGER PRIMARY KEY,\n" +
                "    table_name TEXT,\n" +
                "    status INTEGER)";
        db.execSQL(CREATE_TABLE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS categories");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS tables");
        onCreate(db);
    }

    public void addTables(List<Table> tables) {
        for (Table table : tables) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("table_id", table.getTable_id());
            values.put("table_name", table.getTable_name());
            values.put("status", table.getStatus());

            int rows = db.update("tables", values, "table_id = ?", new String[]{String.valueOf(table.getTable_id())});
            if (rows == 0) {
                db.insert("tables", null, values);
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

            int rows = db.update("categories", values, "cat_id = ?", new String[]{String.valueOf(category.getCat_id())});
            if (rows == 0) {
                db.insert("categories", null, values);
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

            int rows = db.update("products", values, "prod_id = ?", new String[]{String.valueOf(product.getProd_id())});
            if (rows == 0) {
                db.insert("products", null, values);
            }
        }
    }

    public List<Table> getTables() {
        List<Table> tables = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tables order by table_id", null);

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
        Cursor cursor = db.rawQuery("SELECT * FROM categories", null);

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
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE prod_cat = " + category, null);

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
}
