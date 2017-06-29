package com.neelakshbhatia.roome;

import android.widget.ListView;

/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class Card {
    private String type;
    private String title;
    private String message;
    private String date;
    private ListView remindersListView;

    public Card() {
    }

    public Card(String type, String title, String message, String date,ListView reminderListView) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.date = date;
        this.remindersListView = reminderListView;
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

    public ListView getListView() {
        return remindersListView;
    }

    public void setListView(ListView remindersListView) {
        this.remindersListView = remindersListView;
    }
}
