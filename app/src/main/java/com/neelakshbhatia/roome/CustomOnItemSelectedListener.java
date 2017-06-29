package com.neelakshbhatia.roome;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import static com.neelakshbhatia.roome.NotificationBuilderActivity.*;

public class CustomOnItemSelectedListener implements OnItemSelectedListener {
    private String selected;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        selected = (String) String.valueOf(parent.getItemAtPosition(pos));
        if (selected.equals("Reminder")){
           reminderList.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            reminder_text_view.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
        }
        else{
            reminderList.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            reminder_text_view.setVisibility(View.GONE);
            description.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}