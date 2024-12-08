package org.easytech.order;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;


public class Cart implements Parcelable {
    private static Cart instance;
    private final List<Product> cartItems;
    private int tableId;

    // Constructor
    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    public static Cart getInstance(int tableId) {
        if (instance == null || instance.tableId != tableId) {
            instance = new Cart(tableId);  // Δημιουργία νέου καλαθιού για το νέο τραπέζι
        }
        return instance;
    }

    private Cart(int tableId) {
        this.cartItems = new ArrayList<>();
        this.tableId = tableId;
    }
    // Getters and Setters
    public List<Product> getCartItems() {
        return cartItems;
    }

    // Προσθήκη προϊόντος
    public void addItem(Product product) {
        for (Product item : cartItems) {
            if (item.getProd_id() == product.getProd_id()) {
                // Αν το προϊόν υπάρχει ήδη, αυξάνουμε την ποσότητα
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        // Αν δεν υπάρχει, το προσθέτουμε
        product.setQuantity(1); // Ορίζουμε αρχική ποσότητα 1
        cartItems.add(product);
    }

    // Έλεγχος αν το προϊόν υπάρχει
    public boolean containsProduct(Product product) {
        for (Product item : cartItems) {
            if (item.getProd_id() == product.getProd_id()) {
                return true;
            }
        }
        return false;
    }

    // Αύξηση ποσότητας προϊόντος
    public void increaseQuantity(Product product) {
        for (Product item : cartItems) {
            if (item.getProd_id() == product.getProd_id()) {
                item.quantityAdd();
                return;
            }
        }
    }

    public void removeItem(Product product) {
        cartItems.remove(product);
    }

    // Parcelable implementation
    protected Cart(Parcel in) {
        cartItems = in.createTypedArrayList(Product.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cartItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };


}


