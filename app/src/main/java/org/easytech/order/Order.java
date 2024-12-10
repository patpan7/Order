package org.easytech.order;

public class Order {
    private int order_id;
    private int table_id;
    private double order_total;
    private String timestamp;
    private boolean isPrinted;
    private boolean isSynced;

    // Getters and Setters
    public int getOrderId() {
        return order_id;
    }

    public void setOrderId(int orderId) {
        this.order_id = orderId;
    }

    public int getTableId() {
        return table_id;
    }

    public void setTableId(int tableId) {
        this.table_id = tableId;
    }

    public double getOrderTotal() {
        return order_total;
    }

    public void setOrderTotal(double orderTotal) {
        this.order_total = orderTotal;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPrinted() {
        return isPrinted;
    }

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    // Optional: toString για debugging
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + order_id +
                ", tableId=" + table_id +
                ", orderTotal=" + order_total +
                ", timestamp='" + timestamp + '\'' +
                ", isPrinted=" + isPrinted +
                '}';
    }
}
