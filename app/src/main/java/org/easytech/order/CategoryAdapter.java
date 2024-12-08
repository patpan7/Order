package org.easytech.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> categoryList, CategoryActivity listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int position);
    }

//    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
//        this.categoryList = categoryList;
//        this.listener = listener;
//    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.categoryName.setText(categoryList.get(position).getCat_name());

        // Πατώντας σε κάθε κατηγορία θα καλείται η μέθοδος του listener
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}
