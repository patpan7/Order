package org.easytech.order;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartItems;  // Λίστα τύπου Product
    private OnCartItemChangeListener listener;

    // Constructor για το adapter
    public CartAdapter(List<Product> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }


    // Δημιουργία του ViewHolder
    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    // Σύνδεση των δεδομένων στο ViewHolder
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Product product = cartItems.get(position);
        holder.nameTextView.setText(product.getProd_name());
        holder.productQuantityTextView.setText("Ποσότητα: " + product.getQuantity());
        holder.priceTextView.setText(String.format("€%.2f", product.getProd_price()));

        // Αύξηση ποσότητας
        holder.increaseQuantityButton.setOnClickListener(v -> {
            product.quantityAdd();
            notifyDataSetChanged();
            listener.onCartUpdated();
        });

        // Μείωση ποσότητας
        holder.decreaseQuantityButton.setOnClickListener(v -> {
            product.quantityReduce();
            if (product.getQuantity() == 0) {
                cartItems.remove(position);
            }
            notifyDataSetChanged();
            listener.onCartUpdated();
        });

        // Διαγραφή αντικειμένου
        holder.removeItemButton.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyDataSetChanged();
            listener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // ViewHolder για τα στοιχεία του καλαθιού
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, productQuantityTextView;
        Button increaseQuantityButton, decreaseQuantityButton, removeItemButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.cartItemName);
            productQuantityTextView = itemView.findViewById(R.id.cartItemQuantity);
            priceTextView = itemView.findViewById(R.id.cartItemPrice);
            increaseQuantityButton = itemView.findViewById(R.id.btnIncrease);
            decreaseQuantityButton = itemView.findViewById(R.id.btnDecrease);
            removeItemButton = itemView.findViewById(R.id.btnRemove);
        }
    }

    // Interface για ενημέρωση του activity
    public interface OnCartItemChangeListener {
        void onCartUpdated();
    }
}
