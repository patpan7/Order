package org.easytech.order;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;
    private OnTableClickListener listener;
    private Context context;
    DBHelper dbHelper;

    // Δήλωση για το AlertDialog ώστε να μπορούμε να το κλείσουμε αργότερα
    private AlertDialog dialog;

    public interface OnTableClickListener {
        void onTableClick(int position);
    }

    public TableAdapter(List<Table> tableList, OnTableClickListener listener, Context context) {
        this.tableList = tableList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.tableNumber.setText(tableList.get(position).getTable_name());

        int table_id = tableList.get(position).getTable_id();
        dbHelper = new DBHelper(context);

        // Ενημέρωση χρώματος ανάλογα με το αν υπάρχει παραγγελία
        if (tableList.get(position).getStatus() == 2) {
            holder.tableNumber.setBackgroundColor(Color.RED); // Χρώμα για τραπέζια με παραγγελία
        } else {
            holder.tableNumber.setBackgroundColor(Color.GRAY); // Χρώμα για ελεύθερα τραπέζια
        }

        // Πατώντας σε κάθε τραπέζι θα καλείται η μέθοδος του listener
        holder.itemView.setOnClickListener(v -> {
            int tableStatus = tableList.get(position).getStatus();
            if (tableStatus == 2) {
                showOptionsPopup(position); // Εμφάνιση popup για status 2
            } else {
                listener.onTableClick(position);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTableList(List<Table> newTableList) {
        this.tableList.clear();
        this.tableList.addAll(newTableList); // Αντικατάσταση με τα νέα δεδομένα
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tableNumber = itemView.findViewById(R.id.tableNumber);
        }
    }

    @SuppressLint("MissingInflatedId")
    private void showOptionsPopup(int position) {
        // Φόρτωσε το custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Δημιουργία του AlertDialog με το custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);

        // Βρες τα κουμπιά και ορίστε τους listeners τους
        Button viewOrderButton = popupView.findViewById(R.id.viewOrderButton);
        Button newOrderButton = popupView.findViewById(R.id.newOrderButton);
        Button checkoutButton = popupView.findViewById(R.id.checkoutButton);

        viewOrderButton.setOnClickListener(v -> {
            showOrders(position);
        });

        newOrderButton.setOnClickListener(v -> {
            startNewOrderActivity(position);
        });

        checkoutButton.setOnClickListener(v -> {
            handleTableCheckout(position);
        });

        // Δημιουργία και εμφάνιση του AlertDialog
        dialog = builder.create();
        dialog.show();
    }

    private void showOrders(int position) {
        int tableId = tableList.get(position).getTable_id();
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra("table_id", tableId);
        context.startActivity(intent);
    }

    private void startNewOrderActivity(int position) {
        listener.onTableClick(position);
    }

    private void handleTableCheckout(int position) {
        dbHelper = new DBHelper(context);
        int table_id = tableList.get(position).getTable_id();
        boolean success = dbHelper.tableSetStatus(table_id, 1); // Επιστροφή στο status 1 (διαθέσιμο)
        dbHelper.orderSetStatus(table_id,0);
        if (success) {
            Toast.makeText(context, "Το τραπέζι εξοφλήθηκε!", Toast.LENGTH_SHORT).show();

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            // Ανανέωση λίστας
            if (context instanceof TablesActivity) {
                ((TablesActivity) context).refreshTableList();
            }

            // Εκτύπωση λίστας για έλεγχο
            for (Table table : tableList) {
                Log.e("Table Status", table.print());
            }
        } else {
            Toast.makeText(context, "Αποτυχία εξόφλησης.", Toast.LENGTH_SHORT).show();
        }
    }


}
