package com.neelakshbhatia.roome;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.ObbInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;


/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    public static List<Card> messageList;
    private Context mContext;
    private String cardOption = "";
    private int lastPosition = -1;
    boolean checkState[];

    private DailyNotificationsActivity notificationActivity;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        //Textviews from Card
        public CardView parentCard;
        public Spinner cardType;
        public TextView title, message, type;
        public ListView reminderArrayListView;

        public MyViewHolder(View view) {
            super(view);
            //text from card
            parentCard = (CardView) view.findViewById(R.id.card_view);
            cardType = (Spinner) view.findViewById(R.id.card_options_spinner);
            type = (TextView) view.findViewById(R.id.type);
            title = (TextView) view.findViewById(R.id.title);
            message = (TextView) view.findViewById(R.id.count);
            reminderArrayListView = (ListView) view.findViewById(R.id.reminder_list_view);
            }
    }

    public MessageAdapter(Context mContext, List<Card> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_card, parent, false);
        // perform on Click Event Listener on CheckedTextView
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Card card = messageList.get(position);
        if (!card.getTitle().equals("")) {
            if (card.getType().equals("Reminder")) {
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#b71c1c"));
                holder.parentCard.setRadius(70);
                int height = card.getReminderArray().size();
                holder.parentCard.getLayoutParams().height = (int)convertDpToPixel(150+(height * 35));
                //CUSTOM ADAPTER!!!
                //TODO: CUSTOM ARRAY ADAPTER
                final ArrayList<CheckedReminderList> reminderArray = card.getReminderArray();
                holder.reminderArrayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        reminderArray.get(position).setReminderText("pomop");
                    }
                });
                final CheckedRemindersListAdapter listAdapter = new CheckedRemindersListAdapter(mContext,reminderArray);

                holder.reminderArrayListView.setAdapter(listAdapter);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid()+"/"+card.getTitle()+"/reminderArray");
                mRef.setValue(new CheckedReminderList("cool",true));
                notificationActivity.getAdapter().notifyDataSetChanged();

            }
            else if (card.getType().equals("Poll")){
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#546e7a"));
            }
            else{
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#1565c0"));
            }
            holder.type.setText(card.getType());
            holder.title.setText(card.getTitle());
            holder.message.setText(card.getMessage());
        }
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
