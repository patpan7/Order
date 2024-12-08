package org.easytech.order;

public class Category {
    private int cat_id;
    private String cat_name;
    private int cat_status;

    public Category(int cat_id, String cat_name, int cat_status) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.cat_status = cat_status;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public int getCat_status() {
        return cat_status;
    }

    public void setCat_status(int cat_status) {
        this.cat_status = cat_status;
    }
}
