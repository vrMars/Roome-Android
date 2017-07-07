package com.neelakshbhatia.roome.Objects;

/**
 * Created by neelakshbhatia on 2017-06-30.
 */

public class CheckedReminderList {
    public String xtext="pop";
    public Boolean check=false;

    public CheckedReminderList(){
    }

    public CheckedReminderList(String text, Boolean check){
        this.xtext=text;
        this.check=check;
    }

    public String getReminderText(){
        return xtext;
    }
    public Boolean getReminderCheck(){
        return check;
    }
    public void setReminderCheck(Boolean check){
        this.check=check;
    }
    public void setReminderText(String text){
        this.xtext = text;
    }
}
