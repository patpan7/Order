package org.easytech.order;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int prod_id;
    private String prod_name;
    private double prod_price;
    private int prod_cat;
    private int prod_status;
    private int quantity;

    public Product(int prod_id, String prod_name, double prod_price, int prod_cat, int prod_status) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_price = prod_price;
        this.prod_cat = prod_cat;
        this.prod_status = prod_status;
        this.quantity = 0;
    }

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public double getProd_price() {
        return prod_price;
    }

    public void setProd_price(double prod_price) {
        this.prod_price = prod_price;
    }

    public int getProd_cat() {
        return prod_cat;
    }

    public void setProd_cat(int prod_cat) {
        this.prod_cat = prod_cat;
    }

    public int getProd_status() {
        return prod_status;
    }

    public void setProd_status(int prod_status) {
        this.prod_status = prod_status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        prod_id = in.readInt();
        prod_name = in.readString();
        prod_price = in.readDouble();
        prod_cat = in.readInt();
        prod_status = in.readInt();
        quantity = in.readInt(); // Διαβάζουμε το πεδίο quantity
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(prod_id);
        dest.writeString(prod_name);
        dest.writeDouble(prod_price);
        dest.writeInt(prod_cat);
        dest.writeInt(prod_status);
        dest.writeInt(quantity); // Γράφουμε το πεδίο quantity
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public void quantityAdd() {
        this.quantity++;
    }
    public void quantityReduce() {
        if (this.quantity > 0)
            this.quantity--;
    }
}

