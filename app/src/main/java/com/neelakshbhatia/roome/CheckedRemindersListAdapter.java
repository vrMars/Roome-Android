package com.neelakshbhatia.roome;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

/**
 * Created by neelakshbhatia on 2017-06-30.
 */

public class CheckedRemindersListAdapter extends ArrayAdapter<CheckedReminderList> {

    public CheckedRemindersListAdapter(Context context, ArrayList<CheckedReminderList> x) {
        super(context, 0, x);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CheckedReminderList remindersList = getItem(position);

        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_list_item, parent, false);
        }
            // Lookup view for data population
            CheckedTextView reminderListItem = (CheckedTextView) convertView.findViewById(R.id.reminder_list_item1);
            // Populate the data into the template view using the data object
            reminderListItem.setText(remindersList.getReminderText());
            reminderListItem.setChecked(remindersList.getReminderCheck());
            Log.d("Tag",String.valueOf(remindersList.getReminderCheck()));
            return convertView;
        }
    }

