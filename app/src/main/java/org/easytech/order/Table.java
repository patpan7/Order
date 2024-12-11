package org.easytech.order;

import android.util.Log;

public class Table {
    private int table_id;
    private String table_name;;
    private int status;

    public Table(int table_id, String table_name, int status) {
        this.table_id = table_id;
        this.table_name = table_name;
        this.status = status;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String print(){
        return ("table{" +
                "tableid = " + table_id +
                ", tablename = " + table_name +
                ", status = " + status +
                "}");
    }
}
