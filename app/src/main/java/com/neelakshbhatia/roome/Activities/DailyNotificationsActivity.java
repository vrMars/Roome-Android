package com.neelakshbhatia.roome.Activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.neelakshbhatia.roome.Objects.Card;
import com.neelakshbhatia.roome.Objects.CheckedReminderList;
import com.neelakshbhatia.roome.Adapters.MessageAdapter;
import com.neelakshbhatia.roome.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class DailyNotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "LOG";

    //Items for Card-> cardList -> RecyclerView + Animations
    private List<Card> cardList;
    private ArrayList<String> mKeys = new ArrayList<>();
    private DatabaseReference mRef;
    private DatabaseReference mRef_reminders;
    private SlideInUpAnimator animator;

    //TextView for when no cards on screen
    private TextView emptyCardList;

    //Client to authenticate using Google
    private GoogleApiClient mGoogleApiClient;

    //Flag to verify if item already removed from cardList Swipe
    private Boolean alreadyRemoved = false;

    //Declaring Fab Menu
    private FloatingActionMenu fab;

    //Declare mAuth variable
    private FirebaseAuth mAuth;

    //Empty array declared for empty Card
    private ArrayList<CheckedReminderList> reminder_array;

    //Declared empty card that is modified each time added/changed in DB
    Card value = new Card("","","","",reminder_array);

    //Recycler view that displays cardList
    private static RecyclerView recyclerView;

    //Adapter to populate recyclerView
    private static MessageAdapter adapter;

    /// onCreate called at the starting of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get current user from FirebaseDB
        mAuth = FirebaseAuth.getInstance();

        //Set backing layout
        setContentView(R.layout.activity_daily_notifications);

        //Toolbar definition and header set
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.toolbar_header);

        //Navigation drawer definition based on drawer_layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            //Hamburger menu definition for navigation drawer in myToolbar
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            //Allows menu to be visible
            toggle.syncState();

        //Set up navigation view in drawer and attach a click listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set header text for navigation listener
        View header = navigationView.getHeaderView(0);
        TextView account = (TextView) header.findViewById(R.id.account_text_view);
        account.setTextSize(18);
        if (mAuth!=null) {
            account.setText(mAuth.getCurrentUser().getEmail());
        }


        //Set up fab menu
        fab = (FloatingActionMenu) findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);

        /// Starts createNotificationBuilder activity with type tapped passed with intent
            //First item in fab -> Reminder
            com.github.clans.fab.FloatingActionButton reminder = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
                    reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNotification = new Intent(getApplicationContext(),NotificationBuilderActivity.class);
                    createNotification.putExtra("type","Reminder");
                    startActivity(createNotification);
                }
            });
            //Second item in fab -> Poll
            com.github.clans.fab.FloatingActionButton poll = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
            poll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNotification = new Intent(getApplicationContext(),NotificationBuilderActivity.class);
                    createNotification.putExtra("type","Poll");
                    startActivity(createNotification);
                }
            });
            //Third item in fab -> Message
            com.github.clans.fab.FloatingActionButton message = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNotification = new Intent(getApplicationContext(),NotificationBuilderActivity.class);
                    createNotification.putExtra("type","Message");
                    startActivity(createNotification);
                }
            });

        //Declares recyclerView and sets adapter with cardList
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cardList = new ArrayList<>();
        adapter = new MessageAdapter(this, cardList);
        recyclerView.setAdapter(adapter);
        //Hides Fab if recyclerView is being scrolled
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && fab.isShown())
                {
                    fab.hideMenuButton(true);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    fab.showMenuButton(true);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Defines emptyCardList textview and defines visibility logic
        emptyCardList = (TextView) findViewById(R.id.emptyCardView);
        if (cardList.size()==0){
            //Going to be true initially (new cardList)
            emptyCardList.setVisibility(View.VISIBLE);
        }

        //RecyclerView LayoutManager setup (GridLayout) -> Single Column
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);

        //Swipe to delete functionality on cards in CardList in RecyclerView
        //simpleItemTouchCallBack looks for left/right swipe events
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /// Firebase shit
        //Get current instance of Firebase Databse
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Database path reference to look for is users/%uID
        mRef = database.getReference("users/"+mAuth.getCurrentUser().getUid());

        //Listen for changes at path reference mRef
        mRef.addChildEventListener(new ChildEventListener() {

            //Incase something added at above reference or its children
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Updates Value card with data from newly added card from DB
                value = dataSnapshot.getValue(Card.class);
                //Adds new key to cardList key array based on key of new value added
                mKeys.add(dataSnapshot.getKey());
                //Index of card added in cardList key array
                int index = mKeys.indexOf(dataSnapshot.getKey());
                //adds value card to cardList
                cardList.add(value);
                //removes emptyCardList textView incase it was visible
                emptyCardList.setVisibility(View.GONE);
                //Tells message adapter that something has been added to INDEX and requires animation
                adapter.notifyItemInserted(index);
            }

            //Incase something changed at above reference or its children
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Updates tempCard with data from changed card in DB
                Card tempCard = dataSnapshot.getValue(Card.class);
                //Stores temp key as a String
                String key = dataSnapshot.getKey();
                //Find index where key is stored in cardList key array
                int index = mKeys.indexOf(key);
                //Updates value of existing card with tempCard at correct index
                cardList.set(index, tempCard);
                //Incase value becomes null, and no cards left, must show emptyCardList textView
                if (cardList.size()==0){
                    emptyCardList.setVisibility(View.VISIBLE);
                }
            }

            //Incase something removed from above reference or its children
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Stores temp key as a String
                String key = dataSnapshot.getKey();
                //Find index where key is stored in cardList key array
                int index = mKeys.indexOf(key);
                //Removes temp key from index of cardList keys
                mKeys.remove(key);
                //If the item exists and is actually deleted via swipe, then remove it from the cardList
                if (!alreadyRemoved) {
                   cardList.remove(index);
                    //Tell message adapter something removed at INDEX, and animate it
                    adapter.notifyItemRemoved(index);
                }
                //If no cards left after recent remove, show emptyCardList textView
                if (cardList.size()==0){
                    emptyCardList.setVisibility(View.VISIBLE);
                }
                //Next remove prep
                alreadyRemoved = false;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /// Method run at start of activity
    @Override
    protected void onStart() {
        //Request email from google sign in activity
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //Add google sign in options to google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Connect with login activity api client (existing)
        mGoogleApiClient.connect();
        super.onStart();
    }

    //Item touch helper to determine swipe direction and draw delete icon when swiping
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        // we want to cache these and not allocate anything repeatedly in the onChildDraw method
        Drawable background;
        Drawable xMark;
        int xMarkMargin;
        boolean initiated;

        private void init() {
            background = new ColorDrawable(Color.RED);
            xMark = ContextCompat.getDrawable(DailyNotificationsActivity.this, R.drawable.ic_delete_sweep_white_24dp);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = (int) DailyNotificationsActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();

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
            alreadyRemoved = true;
            cardList.remove(position);
            Snackbar.make(recyclerView,"Deleted",Snackbar.LENGTH_SHORT).show();
            adapter.notifyItemRemoved(position);
            Log.d("logTAG","loggedOnSwipeRemove");
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1) {
                // not interested in those
                return;
            }

            if (!initiated) {
                init();
            }

            // draw red background
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // draw x mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

    //Signout method
    //->Google, email, facebook signout
    private void signOut() {
        //De-authorize current user in DB
        if (mAuth != null) {
            mAuth.signOut();
        }
        //Log out of Facebook instance
        LoginManager.getInstance().logOut();

        //Log out of Google instance and start LoginActivity with null mAuth
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                    }
                });
    }

    //Navigation drawer button behaviour
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) { //home button pressed
            // Do nothing (already on it)
        }
        else if (id == R.id.nav_sign_out) { //Sign out button pressed
            signOut(); //Sign out of logged in services
        }

        //Close drawer on click of a button
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //On back button pressed in drawer
    @Override
    public void onBackPressed() {

        //Behaviour if backpressed during drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Getter method for recycler view
    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }

    /// MessageAdapter getter method
    public static MessageAdapter getAdapter(){
        return adapter;
    }

    //start new activity with right->transition
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        onStartNewActivity();
    }

    //Transition for new activity (Right -> Left)
    protected void onStartNewActivity() {
        overridePendingTransition(R.transition.enter_from_right, R.transition.exit_to_left);
    }
}
