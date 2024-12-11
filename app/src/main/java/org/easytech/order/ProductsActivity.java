package org.easytech.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener, CartAdapter.OnCartItemChangeListener {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter adapter;
    private List<Product> productList = null;
    private Cart cart;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private int tableid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        SettingsHelper settingsHelper = new SettingsHelper(this);

        int tablesCol = settingsHelper.getCols("products_col");
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, tablesCol));

        // Αποκτήστε την κατηγορία από το intent
        int category = getIntent().getIntExtra("category",0);
        setTitle("Προϊόντα για " + category);

        tableid = getIntent().getIntExtra("tableid", -1);

        cart = Cart.getInstance(tableid);
        // Δημιουργία λίστας προϊόντων για την επιλεγμένη κατηγορία
        DBHelper dbHelper = new DBHelper(this);
        productList = dbHelper.getProducts(category);


        adapter = new ProductAdapter(this, productList, cart);
        recyclerViewProducts.setAdapter(adapter);

        // Βρίσκουμε το κουμπί και προσθέτουμε το listener
        checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> openCart());
    }
    @Override
    public void onProductClick(Product product) {
        if (cart.containsProduct(product)) {
            cart.increaseQuantity(product); // Αύξηση ποσότητας
        } else {
            cart.addItem(product); // Προσθήκη νέου προϊόντος
        }

        if (cartAdapter == null) {
            cartAdapter = new CartAdapter(cart.getCartItems(), this); // `this` είναι ο OnCartItemChangeListener
            recyclerViewProducts.setAdapter(cartAdapter);
        }

        cartAdapter.notifyDataSetChanged();

        Toast.makeText(this, product.getProd_name() + " προστέθηκε στο καλάθι.", Toast.LENGTH_SHORT).show();
    }


    private void updateCartSummary() {
        // Ενημέρωση UI για το συνολικό κόστος
    }



    // Μέθοδος για να ανοίξουμε το καλάθι
    private void openCart() {
        Intent intent = new Intent(ProductsActivity.this, CartActivity.class);
        intent.putExtra("cart", cart);  // Περάστε το καλάθι στην επόμενη δραστηριότητα
        intent.putExtra("tableid", tableid); // Προσθέστε το αριθμό του τραπέζιου στο intent
        startActivity(intent);
    }

    @Override
    public void onCartUpdated() {
        
    }
}
