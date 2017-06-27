package com.neelakshbhatia.roome;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DailyNotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "LOG";
    private List<Card> cardList;
    private ArrayList<String> mKeys = new ArrayList<>();
    private DatabaseReference mRef;

    private Intent signOut;

    private FloatingActionButton fab;

    private FirebaseAuth mAuth;

    Card value = new Card("","","");
    int rmPosition;
    int indexKey;
    private String m_TextTitle = "";
    private String m_TextMessage = "";

    private static RecyclerView recyclerView;

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView x){
        this.recyclerView = x;
    }

    private static MessageAdapter adapter;

    public static MessageAdapter getAdapter(){
        return adapter;
    }

    public void setAdapter (MessageAdapter x){
        this.adapter = x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_notifications);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Home");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        cardList = new ArrayList<>();
        adapter = new MessageAdapter(this, cardList);

        //RecyclerView for Cards setup
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {

                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Query applesQuery = mRef.orderByChild("title").equalTo(cardList.get(position).getTitle());
                                    Log.e(TAG, String.valueOf(mRef.orderByChild("title")));
                                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                                appleSnapshot.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e(TAG, "onCancelled", databaseError.toException());
                                        }
                                    });
                                    cardList.remove(position);
                                    Snackbar.make(recyclerView,"Deleted :)",Snackbar.LENGTH_SHORT).show();
                                    rmPosition = position;
                                    adapter.notifyItemRemoved(position);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Query applesQuery = mRef.orderByChild("title").equalTo(cardList.get(position).getTitle());
                                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                                appleSnapshot.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e(TAG, "onCancelled", databaseError.toException());
                                        }
                                    });
                                    cardList.remove(position);
                                    Snackbar.make(recyclerView,"Deleted :)",Snackbar.LENGTH_SHORT).show();
                                    rmPosition = position;
                                    adapter.notifyItemRemoved(position);
                                    indexKey--;
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference("CardList");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNotification = new Intent(getApplicationContext(),NotificationBuilderActivity.class);
                startActivity(createNotification);
            }
        });

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                value = dataSnapshot.getValue(Card.class);
                mKeys.add(dataSnapshot.getKey());
                prepareMessages(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Card newVal = dataSnapshot.getValue(Card.class);
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);
                cardList.set(index,newVal);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Card rmVal = dataSnapshot.getValue(Card.class);
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);
                mKeys.remove(key);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //LOSE FOCUS ON EDITEXT when clicked outside :o
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
    /**
     * Adding few messages for testing
     */
    private void prepareMessages(Card a) {
        cardList.add(a);
    }

    private void alertBuilder(){
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
                m_TextTitle = titleBox.getText().toString();
                m_TextMessage = descriptionBox.getText().toString();
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
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void signOut() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mAuth.signOut();
        }
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
                signOut();
                signOut = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(signOut);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }



    // It's cleaner to animate the start of new activities the same way.
    // Override startActivity(), and call *overridePendingTransition*
    // right after the super, so every single activity transaction will be animated:

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (intent != signOut) {
            onStartNewActivity();
        }
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        if (intent != signOut) {
            onStartNewActivity();
        }
    }

    protected void onStartNewActivity() {
        overridePendingTransition(R.transition.enter_from_right, R.transition.exit_to_left);
    }
}
