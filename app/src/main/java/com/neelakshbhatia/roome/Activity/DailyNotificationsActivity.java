package com.neelakshbhatia.roome.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
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
import com.neelakshbhatia.roome.Utils.Utils;
import com.wooplr.spotlight.SpotlightView;

import org.puder.highlight.HighlightManager;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.neelakshbhatia.roome.Activity.LoginActivity.name;
import static com.neelakshbhatia.roome.Activity.LoginActivity.originalGroup;


public class DailyNotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "LOG";

    //Items for Card-> cardList -> RecyclerView + Animations!
    private List<Card> cardList;
    private ArrayList<String> mKeys = new ArrayList<>();
    private DatabaseReference mRef;
    private DatabaseReference mRef_reminders;
    private SlideInUpAnimator animator;
    private TextView emptyCardList;

    private GoogleApiClient mGoogleApiClient;

    private Intent signOut;
    private Boolean alreadyRemoved = false;
    private FloatingActionMenu fab;

    private FirebaseAuth mAuth;
    private ArrayList<CheckedReminderList> reminder_array;

    private Intent loginActivty;
    private Intent refresh;

    public static  SharedPreferences SP;

    //Onboarding check
    public static final String PREF_USER_FIRST_TIME = "abcd";


    private SwipeRefreshLayout swipeContainer;

    Card value = new Card("","","","",reminder_array);
    int rmPosition;
    int indexKey;
    private String m_TextTitle = "";
    private String m_TextMessage = "";

    private static RecyclerView recyclerView;
    private Boolean isUserFirstTime;
    private SpotlightView spotLight;

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }

    private static MessageAdapter adapter;

    public static MessageAdapter getAdapter(){
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HighlightManager highlightManager = new HighlightManager(DailyNotificationsActivity.this);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(DailyNotificationsActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(DailyNotificationsActivity.this, Onboarding.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);
        if (isUserFirstTime){
            startActivity(introIntent);
        }


        setContentView(R.layout.activity_daily_notifications);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Home");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            loginActivty = new Intent (this,LoginActivity.class);
            startActivity(loginActivty);
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);


            //Nav drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView account = (TextView) header.findViewById(R.id.account_text_view);
            account.setTextSize(18);

            fab = (FloatingActionMenu) findViewById(R.id.fab);


        fab.setClosedOnTouchOutside(true);


            com.github.clans.fab.FloatingActionButton reminder = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
            reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNotification = new Intent(getApplicationContext(), NotificationBuilderActivity.class);
                    createNotification.putExtra("type", "Reminder");
                    startActivity(createNotification);
                }
            });
            com.github.clans.fab.FloatingActionButton poll = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
            poll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNotification = new Intent(getApplicationContext(), NotificationBuilderActivity.class);
                    createNotification.putExtra("type", "Poll");
                    startActivity(createNotification);
                }
            });
            com.github.clans.fab.FloatingActionButton message = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNotification = new Intent(getApplicationContext(), NotificationBuilderActivity.class);
                    createNotification.putExtra("type", "Message");
                    startActivity(createNotification);
                }
            });

            //Recycle view + adapter
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        highlightManager.addView(R.id.material_design_floating_action_menu_item3).setTitle(R.string.Fab_title)
                .setDescriptionId(R.string.Fab_desc)
                .setScreenPosition(0,0,0,0);

            cardList = new ArrayList<>();
            adapter = new MessageAdapter(this, cardList);
            emptyCardList = (TextView) findViewById(R.id.emptyCardView);

            if (cardList.size() == 0) {
                emptyCardList.setVisibility(View.VISIBLE);
            }


            //RecyclerView for Cards setup
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setAdapter(adapter);

            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = new Intent (DailyNotificationsActivity.this,DailyNotificationsActivity.class);
                startActivity(refresh);
                swipeContainer.setRefreshing(false);
                }
            });

            //Swipe to delete functionality exp!
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fab.isShown()) {
                        fab.hideMenuButton(true);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.showMenuButton(true);
                    }

                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            //Firebase shit
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser()!=null) {
                account.setText(mAuth.getCurrentUser().getDisplayName());
            }


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (mAuth.getCurrentUser() != null) {
                SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String strUserName = SP.getString("groupName", "NA");
                boolean leaveGroup = SP.getBoolean("leave_current_group",false);

                if (!leaveGroup) {
                    if (originalGroup!=strUserName) {
                        DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child(originalGroup);
                        root1.child("members").child(mAuth.getCurrentUser().getUid()).removeValue();
                        originalGroup = strUserName;
                    }
                    ArrayList<String> userObj = new ArrayList<String>();
                    userObj.add(mAuth.getCurrentUser().getDisplayName());
                    userObj.add(mAuth.getCurrentUser().getUid());
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(strUserName);
                    root.child("members").child(mAuth.getCurrentUser().getUid()).setValue(userObj);
                }
                else if (leaveGroup){
                    SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    strUserName= SP.getString("groupName","NA");
                    if (originalGroup!=strUserName) {
                        DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child(originalGroup);
                        root1.child("members").child(mAuth.getCurrentUser().getUid()).removeValue();
                        originalGroup = strUserName;
                    }
                    SharedPreferences.Editor editor = SP.edit();
                    editor.clear();
                    editor.commit();
                }
                mRef = database.getReference(strUserName+"/cards");

                mRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        value = dataSnapshot.getValue(Card.class);
                        mKeys.add(dataSnapshot.getKey());
                        int index = mKeys.indexOf(dataSnapshot.getKey());
                        prepareMessages(value);
                        emptyCardList.setVisibility(View.GONE);
                        adapter.notifyItemInserted(index);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Card newVal = dataSnapshot.getValue(Card.class);
                        String key = dataSnapshot.getKey();
                        int index = mKeys.indexOf(key);
                        cardList.set(index, newVal);
                        if (cardList.size() == 0) {
                            emptyCardList.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        int index = mKeys.indexOf(key);
                        mKeys.remove(key);
                        if (!alreadyRemoved) {
                            cardList.remove(index);
                            adapter.notifyItemRemoved(index);
                        }
                        if (cardList.size() == 0) {
                            emptyCardList.setVisibility(View.VISIBLE);
                        }
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

    public void signOut() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mAuth.signOut();
        }
        LoginManager.getInstance().logOut();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
                signOut();
                signOut = new Intent(this, LoginActivity.class);
                startActivity(signOut);

        } else if (id == R.id.nav_home) {
            // Do nothing (already on it)
        }
        else if (id == R.id.settings){
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
        }

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
        Log.d("bad","1bad");
        if (intent != signOut && intent!=refresh) {
            Log.d("bad","2bad");
            onStartNewActivity();
        }
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        Log.d("bad","REALLYbad");
        if (intent != signOut && intent!=refresh) {
            Log.d("bad","REALLYbad2");
            onStartNewActivity();
        }
        else if (intent == refresh){
            overridePendingTransition(0,0);
        }
    }

    protected void onStartNewActivity() {
        overridePendingTransition(R.transition.enter_from_right, R.transition.exit_to_left);
    }

    void updatePreferences(boolean update) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());;
        if(update) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            update = false;
            editor.commit();
        }
    }
}
