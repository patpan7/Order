package org.easytech.order;

public class Order {
    private int order_id;
    private int table_id;
    private double order_total;
    private String timestamp;
    private int isSynced;
    private int isOnTable;
    private int printStatus;

    public Order(int order_id, int table_id, double order_total, String timestamp, int printStatus) {
        this.order_id = order_id;
        this.table_id = table_id;
        this.order_total = order_total;
        this.timestamp = timestamp;
        this.printStatus = printStatus;
    }
    public Order() {
    }

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

    public int isSynced() {
        return isSynced;
    }

    public void setSynced(int synced) {
        isSynced = synced;
    }

    public int getIsOnTable() {
        return isOnTable;
    }

    public void setIsOnTable(int isOnTable) {
        this.isOnTable = isOnTable;
    }

    // Optional: toString για debugging
    @Override
    public String toString() {
        return "Order{" +
                "orderId = " + order_id +
                ", tableId = " + table_id +
                ", orderTotal = " + order_total +
                ", timestamp = '" + timestamp + '\'' +
                ", isOnTable = " + isOnTable +
                '}';
    }

    public void setPrintStatus(int i) {
        this.printStatus = i;
    }
}
