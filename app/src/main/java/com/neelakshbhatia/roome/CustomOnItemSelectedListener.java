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


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}