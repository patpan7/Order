package org.easytech.order;

public class Users {
    private int user_id;
    private String user_name;
    private String user_pass;
    private int is_active;

    public Users(int user_id, String user_name, String user_pass) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_pass = user_pass;
    }

    public Users(int user_id, String user_name, String user_pass, int is_active) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_pass = user_pass;
        this.is_active = is_active;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    @Override
    public String toString() {
        return user_name;
    }
}
