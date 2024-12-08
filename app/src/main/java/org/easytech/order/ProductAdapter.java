package org.easytech.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products; // Λίστα προϊόντων
    private Context context;
    private Cart cart;

    public ProductAdapter(Context context, List<Product> products, Cart cart) {
        this.context = context;
        this.products = products;
        this.cart = cart;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productNameTextView.setText(product.getProd_name());
        holder.productPriceTextView.setText(String.format("%.2f €", product.getProd_price()));

        holder.itemView.setOnClickListener(v -> {
            if (cart.containsProduct(product)) {
                cart.increaseQuantity(product); // Αυξάνουμε την ποσότητα αν υπάρχει
                Toast.makeText(context, product.getProd_name() + " αυξήθηκε στο καλάθι.", Toast.LENGTH_SHORT).show();
            } else {
                cart.addItem(product); // Προσθέτουμε το προϊόν αν δεν υπάρχει
                Toast.makeText(context, product.getProd_name() + " προστέθηκε στο καλάθι.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
        }
    }
}

