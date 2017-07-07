package com.neelakshbhatia.roome.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.neelakshbhatia.roome.Objects.Card;
import com.neelakshbhatia.roome.Objects.CheckedReminderList;
import com.neelakshbhatia.roome.Adapters.CheckedRemindersListAdapter;
import com.neelakshbhatia.roome.Adapters.MessageAdapter;
import com.neelakshbhatia.roome.R;
import com.neelakshbhatia.roome.Helpers.SwipeDismissListViewTouchListener;

import java.util.ArrayList;

import static java.sql.Types.DATE;


public class NotificationBuilderActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    public static Button addButton;
    public static ListView reminderList;
    public static Card x;
    private CardView cardWidget;
    private Intent activityIntent;
    private String myType;


    private RecyclerView recyclerView;
    public static int count;


    private ArrayList<CheckedReminderList> arrayList;
    private ArrayAdapter<CheckedReminderList> listAdapter;

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
        activityIntent = getIntent();
        myType = activityIntent.getStringExtra("type");

        addButton = (Button) findViewById(R.id.addButton);
        reminderList = (ListView) findViewById(R.id.reminder_listView);
        reminder_text_view = (TextView) findViewById(R.id.reminder_text_view);

        title = (EditText) findViewById(R.id.title_editText);
        description = (EditText) findViewById(R.id.description_editText);


        if (myType.equals("Reminder")){
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

        cardWidget = (CardView) findViewById(R.id.card_view_notif);
        if (myType.equals("Reminder")){
            cardWidget.setCardBackgroundColor(Color.parseColor("#b71c1c"));
        }
        else if (myType.equals("Poll")){
            cardWidget.setCardBackgroundColor(Color.parseColor("#546e7a"));
        }
        else{
            cardWidget.setCardBackgroundColor(Color.parseColor("#1565c0"));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        //Get recyclerView and adapter from other activity instance
        recyclerView = notificationActivity.getRecyclerView();
        arrayList = new ArrayList<CheckedReminderList>();

        final CheckedRemindersListAdapter listAdapter = new CheckedRemindersListAdapter(this, arrayList);
        reminderList.setAdapter(listAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("count",String.valueOf(count));
                if (count<6) {
                    addButton.setEnabled(true);
                    String result = String.valueOf(reminder_text_view.getText());
                    reminder_text_view.setText("");
                    arrayList.add(new CheckedReminderList(result,false));
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

    private Card createCard(String type, String title, String message, ArrayList<CheckedReminderList> reminderArray){
            Card name = new Card();
            name.setType(type);
            name.setTitle(title);
            name.setDate(String.valueOf(DATE));
            name.setMessage(message);
            name.setReminderArray(reminderArray);
            return name;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                count = 0;
                String m_TextType = myType;
                String m_TextTitle = title.getText().toString();
                String m_TextMessage = description.getText().toString();
                ArrayList <CheckedReminderList> m_ReminderArray = arrayList;
                x= createCard(m_TextType,m_TextTitle, m_TextMessage, m_ReminderArray);
                if (!x.getTitle().equals("")) {
                    adapter = notificationActivity.getAdapter();
                    mRef.child(mAuth.getCurrentUser().getUid()).child(m_TextTitle).setValue(x);
                    adapter.notifyDataSetChanged();
                }
                Intent startMain = new Intent (this, DailyNotificationsActivity.class);
                startActivity(startMain);
                onLeaveThisActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onPause() {
        super.onPause();
        count = 0;
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.transition.enter_from_left, R.transition.exit_to_right);
    }

    // It's cleaner to animate the start of new activities the same way.
    // Override startActivity(), and call *overridePendingTransition*
    // right after the super, so every single activity transaction will be animated:

}
