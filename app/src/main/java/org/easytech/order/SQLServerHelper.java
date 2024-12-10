package org.easytech.order;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLServerHelper {

    private static final String PREFS_NAME = "AppPreferences";
    private static final String SERVER_KEY = "server_address";
    private static final String CLASSES = "net.sourceforge.jtds.jdbc.Driver";
    private static final String DB_NAME = "Orders";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "admin";

    private final Context context;

    public SQLServerHelper(Context context) {
        this.context = context;
    }

    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;

        try {
            // Εύρεση της διεύθυνσης από τις SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String serverAddress = preferences.getString(SERVER_KEY, "");  // Default "" if not found

            if (serverAddress.isEmpty()) {
                throw new RuntimeException("Server address not configured.");
            }

            // Δημιουργία της σύνδεσης με βάση τη μορφή της διεύθυνσης
            String conUrl;
            if (serverAddress.contains(":")) {
                // Μορφή ip:port
                String[] parts = serverAddress.split(":");
                String ip = parts[0];
                String port = parts[1];
                conUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + DB_NAME;
            } else if (serverAddress.contains("\\")) {
                // Μορφή ip\instance
                String[] parts = serverAddress.split("\\\\");
                String ip = parts[0];
                String instance = parts[1];
                conUrl = "jdbc:jtds:sqlserver://" + ip + "/" + DB_NAME + ";instance=" + instance;
            } else {
                // Προεπιλεγμένη μορφή με ip και προεπιλεγμένη θύρα
                conUrl = "jdbc:jtds:sqlserver://" + serverAddress + ":1433/" + DB_NAME;
            }

            // Δημιουργία σύνδεσης
            Class.forName(CLASSES);
            conn = DriverManager.getConnection(conUrl, USERNAME, PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Connection failed: " + e.getMessage(), e);
        }

        return conn;
    }

    public boolean syncToLocalDB(Context context) {
        List<Table> tables = getTablesFromServer();
        List<Category> categories = getCategoriesFromServer();
        List<Product> products = getProductsFromServer();
        if (tables != null && !tables.isEmpty() && categories != null && !categories.isEmpty() && products != null && !products.isEmpty()) {
            DBHelper dbHelper = new DBHelper(context);
            dbHelper.addTables(tables);
            dbHelper.addCategories(categories);
            dbHelper.addProducts(products);
            return true; // Επιστρέφει true αν η συγχρονισμός ήταν επιτυχής
        }
        return false; // Επιστρέφει false αν δεν υπάρχουν πελάτες
    }

    private List<Table> getTablesFromServer() {
        List<Table> tables = new ArrayList<>();

        try (Connection con = CONN();
             Statement stmt = con.createStatement()) {

            String SQL = "SELECT * FROM [Orders].[dbo].[tables]";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                Table table = new Table(
                        rs.getInt("table_id"),
                        rs.getString("table_name"),
                        rs.getInt("status")
                );
                Log.e("Table", table.getTable_name());
                tables.add(table);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return tables;
    }

    private List<Category> getCategoriesFromServer() {
        List<Category> categories = new ArrayList<>();

        try (Connection con = CONN();
             Statement stmt = con.createStatement()) {

            String SQL = "SELECT * FROM [Orders].[dbo].[categories]";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("cat_id"),
                        rs.getString("cat_name"),
                        rs.getInt("cat_status")
                );
                Log.e("Category ", category.getCat_name());
                categories.add(category);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return categories;
    }

    public List<Product> getProductsFromServer() {
        List<Product> products = new ArrayList<>();

        try (Connection con = CONN();
             Statement stmt = con.createStatement()) {

            String SQL = "SELECT * FROM [Orders].[dbo].[products]";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("prod_id"),
                        rs.getString("prod_name"),
                        rs.getDouble("prod_price"),
                        rs.getInt("prod_cat"),
                        rs.getInt("prod_status")
                );
                Log.e("Product ", product.getProd_name());
                products.add(product);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return products;
    }

    public boolean syncOrderWithServer() {
        DBHelper dbHelper = new DBHelper(context);
        SQLServerHelper sqlServerHelper = new SQLServerHelper(context);

        List<Order> unsyncedOrders = dbHelper.getUnsyncedOrders();

        if (unsyncedOrders.isEmpty()) {
            Log.i("Sync", "Όλες οι παραγγελίες είναι συγχρονισμένες.");
            return true;
        }

        try (Connection con = sqlServerHelper.CONN();
             PreparedStatement orderStmt = con.prepareStatement(
                     "INSERT INTO Orders (table_id, total_amount, timestamp) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement detailStmt = con.prepareStatement(
                     "INSERT INTO OrderDetails (order_id, prod_id, quantity, price) VALUES (?, ?, ?, ?)")) {

            for (Order order : unsyncedOrders) {
                orderStmt.setInt(1, order.getTableId());
                orderStmt.setDouble(2, order.getOrderTotal());
                orderStmt.setString(3, order.getTimestamp());
                orderStmt.executeUpdate();

                ResultSet rs = orderStmt.getGeneratedKeys();
                int serverOrderId = 0;
                if (rs.next()) {
                    serverOrderId = rs.getInt(1);
                }

                List<OrderDetail> orderDetails = dbHelper.getOrderDetails(order.getOrderId());
                for (OrderDetail detail : orderDetails) {
                    detailStmt.setInt(1, serverOrderId);
                    detailStmt.setInt(2, detail.getProductId());
                    detailStmt.setInt(3, detail.getQuantity());
                    detailStmt.setDouble(4, detail.getPrice());
                    detailStmt.executeUpdate();
                }

                dbHelper.markOrderAsSynced(order.getOrderId());
            }
        } catch (SQLException e) {
            Log.e("Sync", "Σφάλμα συγχρονισμού: " + e.getMessage());
            return false;
        }

        Log.i("Sync", "Ο συγχρονισμός ολοκληρώθηκε επιτυχώς.");
        return true;
    }
}

