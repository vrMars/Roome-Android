package com.neelakshbhatia.roome;

/**
 * Created by neelakshbhatia on 2017-06-30.
 */

public class CheckedReminderList {
    public String text="";
    public Boolean check=false;

    public CheckedReminderList(){
    }

    public CheckedReminderList(String text, Boolean check){
        this.text=text;
        this.check=check;
    }

    public String getReminderText(){
        return text;
    }
    public Boolean getReminderCheck(){
        return check;
    }
    public void setReminderCheck(Boolean check){
        this.check=check;
    }
    public void setReminderText(String text){
        this.text=text;
    }
}
