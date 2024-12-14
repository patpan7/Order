package org.easytech.order;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter adapter;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        // Φόρτωση των παραγγελιών του τραπεζιού
        DBHelper dbHelper = new DBHelper(this);
        int tableId = getIntent().getIntExtra("table_id", -1);
        Log.d("OrderDetailsActivity", "tableId: " + tableId);
        orders = dbHelper.getOrdersForTable(tableId);
        Log.d("OrderDetailsActivity", "Orders: " + orders.size());
        adapter = new OrderAdapter(this, orders);
        recyclerViewOrders.setAdapter(adapter);
    }
}
