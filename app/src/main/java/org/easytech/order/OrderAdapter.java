package org.easytech.order;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders; // Η λίστα των παραγγελιών

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderIdTextView.setText("Παραγγελία #" + order.getOrderId());
        holder.orderTotalTextView.setText(String.format("%.2f €", order.getOrderTotal()));

        holder.btnReprint.setOnClickListener(v -> {
            // Ενέργεια επανεκτύπωσης
            reprintOrder(order.getOrderId(),order.getTableId());
        });

        // Επεκτείνει ή κρύβει τα είδη της παραγγελίας
        holder.itemView.setOnClickListener(v -> {
            boolean expanded = order.isExpanded();
            order.setExpanded(!expanded);
            notifyItemChanged(position);
        });

        // Προβολή ειδών παραγγελίας αν είναι επεκταμένη
        if (order.isExpanded()) {
            holder.itemsTextView.setVisibility(View.VISIBLE);
            holder.btnReprint.setVisibility(View.VISIBLE);
            DBHelper dbhelper = new DBHelper(context);
            List<Product> products = dbhelper.getOrderItems(order.getOrderId());
            Log.d("OrderAdapter", "Products for order " + order.getOrderId() + ": " + products.size());
            StringBuilder items = new StringBuilder();
            for (Product item : products) {
                items.append(item.getProd_name())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" - ")
                        .append(String.format("%.2f €", item.getProd_price()))
                        .append("\n");
            }
            holder.itemsTextView.setText(items.toString());
        } else {
            holder.itemsTextView.setVisibility(View.GONE);
            holder.btnReprint.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderTotalTextView;
        TextView itemsTextView;
        Button btnReprint;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderId);
            orderTotalTextView = itemView.findViewById(R.id.orderTotal);
            itemsTextView = itemView.findViewById(R.id.orderItems);
            btnReprint = itemView.findViewById(R.id.btnReprint);
        }
    }
    private void reprintOrder(int orderId, int tableId) {
        EscPosPrinterHelper printerHelper = new EscPosPrinterHelper();
        DBHelper dbHelper = new DBHelper(context);
        String tableName = dbHelper.getTableName(tableId);
        List<Product> orderItems = dbHelper.getOrderItems(orderId);
        printerHelper.printOrderAsync(orderId, orderItems,tableName, new EscPosPrinterHelper.PrintCallback() {
            @Override
            public void onSuccess() {
                Log.e("OrderAdapter", "Order printed successfully");
            }

            @Override
            public void onError(Exception e) {
                Log.e("OrderAdapter", "Error printing order: " + e.getMessage());
            }
        });
    }
}
