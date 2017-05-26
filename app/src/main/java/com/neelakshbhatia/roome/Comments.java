package com.neelakshbhatia.roome;

/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class Comments {
    private String name;
    private String message;
    private String date;

    public Comments() {
    }

    public Comments(String name, String message, String date) {
        this.name = name;
        this.message = message;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
