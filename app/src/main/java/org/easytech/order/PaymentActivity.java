package org.easytech.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private Button btnCash, btnCard;
    private LinearLayout paymentMethodsLayout;
    private int tableId;
    private DBHelper dbHelper;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        paymentMethodsLayout = findViewById(R.id.paymentMethodsLayout);

        dbHelper = new DBHelper(this);

        // Ανάκτηση tableId από Intent
        tableId = getIntent().getIntExtra("tableid", -1);
        if (tableId == -1) {
            Toast.makeText(this, "Σφάλμα: Δεν υπάρχει ID τραπεζιού!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        List<Product> products = new ArrayList<>();
        List<Order> orders = dbHelper.getOrdersForTable(tableId);
        for (Order order : orders) {
            products.addAll(dbHelper.getOrderItems(order.getOrderId()));
        }

        // Ρύθμιση RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(new PaymentProductAdapter(this, products));

        // Ανάκτηση δυναμικών τρόπων πληρωμής
        loadPaymentMethods();

    }

    private void loadPaymentMethods() {
        List<String> paymentMethods = dbHelper.getPaymentMethods();

        for (String method : paymentMethods) {
            Button methodButton = new Button(this);
            methodButton.setText(method);
            methodButton.setOnClickListener(v -> processPayment(method));
            paymentMethodsLayout.addView(methodButton);
        }
    }

    private void processPayment(String paymentType) {
        // Ενέργεια εξόφλησης
        dbHelper.markTableAsPaid(tableId, paymentType);
        dbHelper.tableSetStatus(tableId, 1);
        dbHelper.orderSetStatus(tableId,0);
        Toast.makeText(this, "Η πληρωμή ολοκληρώθηκε: " + paymentType, Toast.LENGTH_SHORT).show();
        finish(); // Κλείσιμο του Activity

    }
}
