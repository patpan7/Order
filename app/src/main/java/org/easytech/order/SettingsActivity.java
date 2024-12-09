package org.easytech.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.InflateException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {
    Button btnSync, btnSave;
    Connection con;
    String str;
    ProgressBar progressBar; // Ορισμός μεταβλητής για το ProgressBar
    EditText tvServer;
    EditText textTablesCol, textCategoriesCol, textProductsCol;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPreferences";
    private static final String SERVER_KEY = "server_address";
    private static final String TABLE_COL = "tables_col";
    private static final String CATEGORY_COL = "categories_col";
    private static final String PRODUCT_COL = "products_col";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tvServer = findViewById(R.id.tvServer);
        btnSave = findViewById(R.id.btnSave);
        btnSync = findViewById(R.id.btnSync);
        progressBar = findViewById(R.id.progressBar); // Αρχικοποίηση του ProgressBar

        textTablesCol = findViewById(R.id.textTablesCol);
        textCategoriesCol = findViewById(R.id.textCategoriesCol);
        textProductsCol = findViewById(R.id.textProductsCol);

        loadSettings();

        btnSave.setOnClickListener(v -> saveServerAddress());
        btnSync.setOnClickListener(view -> {
            syncData();
        });
    }
    private void syncData() {
        progressBar.setVisibility(View.VISIBLE);
        btnSync.setEnabled(false);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                SQLServerHelper sqlServerHelper = new SQLServerHelper(this);
                con = sqlServerHelper.CONN();
                if (con == null) {
                    str = "Error";
                } else {
                    str = "Connected";
                    //SQLServerHelper sqlServerHelper = new SQLServerHelper();
                    boolean success = sqlServerHelper.syncToLocalDB(this);
                    if (success)
                        str = str + " sync";
                }
            } catch (Exception e) {
                e.printStackTrace();
                str = "An error occurred during sync: " + e.getMessage();
                //Toast.makeText(this,e+"",Toast.LENGTH_SHORT).show();
            }


            runOnUiThread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InflateException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        progressBar.setVisibility(View.GONE);
                        btnSync.setEnabled(true);
                        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                    }

            );
        });
    }

    private void loadSettings() {
        // Λήψη της διεύθυνσης του server από τις SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serverAddress = sharedPreferences.getString(SERVER_KEY, "");
        tvServer.setText(serverAddress); // Εμφάνιση της διεύθυνσης στο EditText αν υπάρχει


        try {
            int tablesCol = sharedPreferences.getInt(TABLE_COL, 0);
            int categoriesCol = sharedPreferences.getInt(CATEGORY_COL, 0);
            int productsCol = sharedPreferences.getInt(PRODUCT_COL, 0);
            if (tablesCol <= 0 && categoriesCol <= 0 && productsCol <= 0) {
                throw new NumberFormatException();
            }
            // Αποθήκευση της τιμής αν όλα είναι έγκυρα.
            textTablesCol.setText(String.valueOf(tablesCol));
            textCategoriesCol.setText(String.valueOf(categoriesCol));
            textProductsCol.setText(String.valueOf(productsCol));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number for tables columns", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void saveServerAddress() {
        String serverAddress = tvServer.getText().toString().trim();
        int tablesCol = Integer.parseInt(textTablesCol.getText().toString());
        int categoriesCol = Integer.parseInt(textCategoriesCol.getText().toString());
        int productsCol = Integer.parseInt(textProductsCol.getText().toString());
        if (serverAddress.isEmpty() || tablesCol <=0 ) {
            Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Αποθήκευση της διεύθυνσης του server στις SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVER_KEY, serverAddress);
        editor.putInt(TABLE_COL, tablesCol);
        editor.putInt(CATEGORY_COL, categoriesCol);
        editor.putInt(PRODUCT_COL, productsCol);
        editor.apply();

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }
}
