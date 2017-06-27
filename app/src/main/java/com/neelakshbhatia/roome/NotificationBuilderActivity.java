package com.neelakshbhatia.roome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Date;
import java.util.concurrent.atomic.AtomicMarkableReference;

import static java.sql.Types.DATE;
import static java.sql.Types.NULL;


public class NotificationBuilderActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private DatabaseReference mRef;
    private RecyclerView recyclerView;

    private TextView title;
    private TextView description;


    private DailyNotificationsActivity notificationActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (EditText) findViewById(R.id.title_editText);
        description = (EditText) findViewById(R.id.description_editText);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference("CardList");

        //Get recyclerView and adapter from other activity instance
        recyclerView = notificationActivity.getRecyclerView();
        adapter = notificationActivity.getAdapter();
    }

    private Card createCard(String title, String message){
            Card name = new Card();
            name.setTitle(title);
            name.setDate(String.valueOf(DATE));
            name.setMessage(message);
            return name;
    }


    @Override
    public void onPause() {
        super.onPause();
        String m_TextTitle = title.getText().toString();
        String m_TextMessage = description.getText().toString();
        Card a = createCard(m_TextTitle, m_TextMessage);
        if (!a.getTitle().equals("") && !a.getMessage().equals("")) {
            Log.d("bad","ha:"+a.getTitle());
            mRef.child(m_TextTitle).setValue(a);
            adapter.notifyDataSetChanged();
        }
        Log.d("bad","good");
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.transition.enter_from_left, R.transition.exit_to_right);
    }

    // It's cleaner to animate the start of new activities the same way.
    // Override startActivity(), and call *overridePendingTransition*
    // right after the super, so every single activity transaction will be animated:

}
