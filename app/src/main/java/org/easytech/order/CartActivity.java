package org.easytech.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.EscPosPrinter;

import java.util.DoubleSummaryStatistics;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Cart cart;  // Αντικείμενο Cart
    private Button printButton;
    private TextView totalPriceTextView;
    private int tableNumber;
    DBHelper dbHelper;
    Double sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dbHelper = new DBHelper(this);
        /// Ανάκτηση του καλαθιού από το Intent
        cart = getIntent().getParcelableExtra("cart");

        tableNumber = getIntent().getIntExtra("tableNumber", -1);

        // Εδώ αρχικοποιούμε το cart εάν δεν έχει ήδη περαστεί
        if (cart == null) {
            cart = new Cart();  // Δημιουργία νέου cart αντικειμένου
        }
        // Αρχικοποιούμε το RecyclerView για το καλάθι
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Ανανεώστε το συνολικό κόστος ή άλλες λεπτομέρειες
        cartAdapter = new CartAdapter(cart.getCartItems(), this::updateCartSummary);
        cartRecyclerView.setAdapter(cartAdapter);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Κουμπί για εκτύπωση
        printButton = findViewById(R.id.printButton);
        printButton.setOnClickListener(v -> handleSaveAndPrint());

        // Ενημέρωση συνολικού κόστους στην αρχή
        updateCartSummary();
    }

    private void updateCartSummary() {
        double totalPrice = 0;
        for (Product product : cart.getCartItems()) {
            totalPrice += product.getProd_price() * product.getQuantity();
        }
        totalPriceTextView.setText(String.format("Σύνολο: %.2f €", totalPrice));
        sum = totalPrice;
    }


    // Μέθοδος για την αποστολή της παραγγελίας στον εκτυπωτή
    private void handleSaveAndPrint() {
        // 1. Αποθήκευση της παραγγελίας στην τοπική βάση
        int orderId = saveOrderLocally();

        if (orderId > 0) {
            // 2. Εκτύπωση της παραγγελίας
            printOrder(orderId);
            boolean printSuccess = true;

            if (printSuccess) {
                // 3. Εμφάνιση επιβεβαίωσης και καθαρισμός καλαθιού
                Toast.makeText(this, "Η παραγγελία αποθηκεύτηκε και εκτυπώθηκε.", Toast.LENGTH_SHORT).show();
                cart.getInstance(tableNumber).clearCart();
                cartAdapter.notifyDataSetChanged();

                // 4. Εκκίνηση συγχρονισμού με τον server
                syncOrderWithServer(orderId);
            } else {
                Toast.makeText(this, "Αποθήκευση έγινε, αλλά απέτυχε η εκτύπωση.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Αποτυχία αποθήκευσης της παραγγελίας.", Toast.LENGTH_SHORT).show();
        }
    }
    private int saveOrderLocally() {
        DBHelper dbHelper = new DBHelper(this); // Αρχικοποίηση του DBHelper

        // Δημιουργία της παραγγελίας
        Order order = new Order();
        order.setTableId(tableNumber); // Αναφορά στο τραπέζι
        order.setOrderTotal(sum); // Υπολογισμός συνόλου παραγγελίας
        order.setTimestamp(String.valueOf(System.currentTimeMillis())); // Χρόνος καταχώρησης
        order.setSynced(false); // Ορισμός ότι η παραγγελία δεν έχει συγχρονιστεί

        // Εισαγωγή της παραγγελίας στον πίνακα Orders
        int orderId = dbHelper.insertOrder(order);
        if (orderId > 0) { // Αν η εισαγωγή είναι επιτυχής
            // Εισαγωγή των λεπτομερειών της παραγγελίας
            for (Product product : cart.getInstance(tableNumber).getCartItems()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orderId); // Σύνδεση με την παραγγελία
                orderDetail.setProductId(product.getProd_id()); // ID προϊόντος
                orderDetail.setQuantity(product.getQuantity()); // Ποσότητα προϊόντος
                orderDetail.setPrice(product.getProd_price()); // Τιμή προϊόντος
                dbHelper.insertOrderDetail(orderDetail); // Εισαγωγή στον πίνακα OrderDetails
            }
        }
        return orderId; // Επιστροφή του ID της παραγγελίας ή 0 αν αποτύχει
    }

    private void printOrder(int orderId) {
        String printerIp = "192.168.1.97";
        int printerPort = 9100;

        EscPosPrinterHelper printerHelper = new EscPosPrinterHelper(printerIp, printerPort);

        printerHelper.printOrderAsync(orderId, cart.getCartItems(), new EscPosPrinterHelper.PrintCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Η εκτύπωση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Σφάλμα στην εκτύπωση: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }



    private void syncOrderWithServer(int orderId) {
        // Συγχρονισμός με τον server (χρησιμοποιούμε την υπάρχουσα μέθοδο της SQLServerHelper)
        SQLServerHelper sqlServerHelper = new SQLServerHelper(this);
        boolean syncSuccess = sqlServerHelper.syncOrderWithServer();

        if (syncSuccess) {
            // Ενημέρωση τοπικής βάσης ότι η παραγγελία συγχρονίστηκε
            dbHelper.markOrderAsSynced(orderId);
            Toast.makeText(this, "Η παραγγελία συγχρονίστηκε με τον server.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Αποτυχία συγχρονισμού με τον server.", Toast.LENGTH_SHORT).show();
        }
    }

}


