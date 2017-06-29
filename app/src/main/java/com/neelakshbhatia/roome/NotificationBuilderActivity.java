package com.neelakshbhatia.roome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;

import static java.sql.Types.DATE;
import static java.sql.Types.NULL;


public class NotificationBuilderActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    public static Button addButton;
    public static ListView reminderList;
    public static ArrayList<String> m_ReminderArray;

    private RecyclerView recyclerView;
    public static int count;


    private ArrayList<String> arrayList;
    private ArrayAdapter<String> listAdapter;

    public static TextView title;
    public static TextView description;
    public static TextView reminder_text_view;
    public static Spinner card_options_spinner;

    private DailyNotificationsActivity notificationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        card_options_spinner = (Spinner) findViewById(R.id.card_options_spinner);
        card_options_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        addButton = (Button) findViewById(R.id.addButton);
        reminderList = (ListView) findViewById(R.id.reminder_listView);
        reminder_text_view = (TextView) findViewById(R.id.reminder_text_view);

        title = (EditText) findViewById(R.id.title_editText);
        description = (EditText) findViewById(R.id.description_editText);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        //Get recyclerView and adapter from other activity instance
        recyclerView = notificationActivity.getRecyclerView();
        arrayList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(NotificationBuilderActivity.this, R.layout.reminder_list_item,
                arrayList);
        reminderList.setAdapter(listAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("count",String.valueOf(count));
                if (count<6) {
                    addButton.setEnabled(true);
                    String result = String.valueOf(reminder_text_view.getText());
                    reminder_text_view.setText("");
                    arrayList.add(result);
                    listAdapter.notifyDataSetChanged();
                    count++;
                }
                if (count ==6){
                    addButton.setEnabled(false);
                }
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        reminderList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    count--;
                                    addButton.setEnabled(true);
                                    arrayList.remove(position);
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        });
        reminderList.setOnTouchListener(touchListener);
    }

    private Card createCard(String type, String title, String message, ArrayList<String> reminderArray){
            Card name = new Card();
            name.setType(type);
            name.setTitle(title);
            name.setDate(String.valueOf(DATE));
            name.setMessage(message);
            name.setReminderArray(reminderArray);
            return name;
    }

    public String convertTypeToEmoji (String x){
        if (x.equals("Reminder")){
            return new String(Character.toChars(0x2705));
        }
        else if (x.equals("Poll")){
            return new String(Character.toChars(0x2754));
        }
        else{
            return new String(Character.toChars(0x2709));
        }
    }

    //CANT STORE LIST_VIEW iN FIREBASE REFACTOR TO STORE ARRAY!
    @Override
    public void onPause() {
        super.onPause();
        count = 0;
        String m_TextType = String.valueOf(card_options_spinner.getSelectedItem());
        String m_TextTitle = title.getText().toString();
        String m_TextMessage = description.getText().toString();
        ArrayList <String> m_ReminderArray = arrayList;
        Card a = createCard(m_TextType,m_TextTitle, m_TextMessage, m_ReminderArray);
        if (!a.getTitle().equals("")) {
            adapter = notificationActivity.getAdapter();
            mRef.child(mAuth.getCurrentUser().getUid()).child(m_TextTitle).setValue(a);
            adapter.notifyDataSetChanged();
        }
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.transition.enter_from_left, R.transition.exit_to_right);
    }

    // It's cleaner to animate the start of new activities the same way.
    // Override startActivity(), and call *overridePendingTransition*
    // right after the super, so every single activity transaction will be animated:

}
