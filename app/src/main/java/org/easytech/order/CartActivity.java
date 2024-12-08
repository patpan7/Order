package org.easytech.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Cart cart;  // Αντικείμενο Cart
    private Button printButton;
    private TextView totalPriceTextView;
    private int tableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        printButton.setOnClickListener(v -> sendOrderToPrinter());

        // Ενημέρωση συνολικού κόστους στην αρχή
        updateCartSummary();
    }

    private void updateCartSummary() {
        double totalPrice = 0;
        for (Product product : cart.getCartItems()) {
            totalPrice += product.getProd_price() * product.getQuantity();
        }
        totalPriceTextView.setText(String.format("Σύνολο: %.2f €", totalPrice));
    }


    // Μέθοδος για την αποστολή της παραγγελίας στον εκτυπωτή
    private void sendOrderToPrinter() {
        // Στην πραγματικότητα, εδώ θα πρέπει να καλέσετε τον κώδικα για την εκτύπωση της παραγγελίας.
        // Ανάλογα με το τι υλικό και βιβλιοθήκες χρησιμοποιείτε για την εκτύπωση, θα πρέπει να το ενσωματώσετε εδώ.

        Toast.makeText(this, "Η παραγγελία στάλθηκε για εκτύπωση.", Toast.LENGTH_SHORT).show();
    }
}


