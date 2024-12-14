package org.easytech.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentProductAdapter extends RecyclerView.Adapter<PaymentProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;

    public PaymentProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productNameTextView.setText(product.getProd_name());
        holder.productQuantityTextView.setText("x" + product.getQuantity());
        holder.productPriceTextView.setText(String.format("%.2f €", product.getProd_price()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, productQuantityTextView, productPriceTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
        }
    }
}
