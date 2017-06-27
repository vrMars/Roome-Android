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
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicMarkableReference;



public class NotificationBuilderActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private DatabaseReference mRef;
    private RecyclerView recyclerView;
    private DailyNotificationsActivity cool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference("CardList");

        recyclerView = cool.getRecyclerView();
        adapter = cool.getAdapter();

        alertBuilder();
    }

    private void alertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Message");
        // Set up the input
        Context context = recyclerView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(80,80,80,80);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleBox = new EditText(context);
        titleBox.setHint("Title");
        layout.addView(titleBox);

        final EditText descriptionBox = new EditText(context);
        descriptionBox.setHint("Content");
        layout.addView(descriptionBox);

        // Specify the type of input expected; this, for example, sets the input as a normal, and will do nothing
        //inputTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        //inputMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_TextTitle = titleBox.getText().toString();
                String m_TextMessage = descriptionBox.getText().toString();
                Card a = createCard(m_TextTitle,m_TextMessage);
                mRef.child(m_TextTitle).setValue(a);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private Card createCard(String title, String message){
        Card name = new Card();
        name.setTitle(title);
        name.setDate("lol");
        name.setMessage(message);
        return name;
    }


    @Override
    public void onPause() {
        super.onPause();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.transition.enter_from_left, R.transition.exit_to_right);
    }

    // It's cleaner to animate the start of new activities the same way.
    // Override startActivity(), and call *overridePendingTransition*
    // right after the super, so every single activity transaction will be animated:

}
