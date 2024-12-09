package org.easytech.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerViewCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList = null;
    private Button checkoutButton;
    private Cart cart;
    private int tableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        SettingsHelper settingsHelper = new SettingsHelper(this);

        int tablesCol = settingsHelper.getCols("categories_col");
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, tablesCol));

        // Αποκτήστε τον αριθμό τραπεζιού από το intent
        tableNumber = getIntent().getIntExtra("tableNumber", -1);
        setTitle("Κατηγορίες για " + tableNumber);

        cart = Cart.getInstance(tableNumber);

        DBHelper dbHelper = new DBHelper(this);
        categoryList = dbHelper.getCategories();

        adapter = new CategoryAdapter(categoryList, this);
        recyclerViewCategories.setAdapter(adapter);

        checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> openCart());
    }

    @Override
    public void onCategoryClick(int position) {
        // Μετάβαση στην επόμενη οθόνη (π.χ. Activity για προϊόντα)
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("category", categoryList.get(position).getCat_id());
        intent.putExtra("tableNumber", tableNumber);
        startActivity(intent);
    }

    // Μέθοδος για να ανοίξουμε το καλάθι
    private void openCart() {
        Intent intent = new Intent(CategoryActivity.this, CartActivity.class);
        intent.putExtra("cart", cart);  // Περάστε το καλάθι στην επόμενη δραστηριότητα
        intent.putExtra("tableNumber", tableNumber); // Προσθέστε το αριθμό του τραπέζιου στο intent
        startActivity(intent);
    }
}
