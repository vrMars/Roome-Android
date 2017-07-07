package com.neelakshbhatia.roome.Objects;

import java.util.ArrayList;

/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class Card {
    private String type;
    private String title;
    private String message;
    private String date;
    private ArrayList<CheckedReminderList> reminderArray;

    public Card() {
    }

    public Card(String type, String title, String message, String date, ArrayList<CheckedReminderList> reminderArray) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.date = date;
        this.reminderArray = reminderArray;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public ArrayList<CheckedReminderList> getReminderArray() {
        return reminderArray;
    }

    public void setReminderArray(ArrayList<CheckedReminderList> reminderArray) {
        this.reminderArray = reminderArray;
    }
}
