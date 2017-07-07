package com.neelakshbhatia.roome.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neelakshbhatia.roome.Activity.DailyNotificationsActivity;
import com.neelakshbhatia.roome.Objects.Card;
import com.neelakshbhatia.roome.Objects.CheckedReminderList;
import com.neelakshbhatia.roome.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    public static List<Card> messageList;
    private Context mContext;
    private String cardOption = "";
    private int lastPosition = -1;
    private int size = 0;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference("GroupX/cards");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (!card.getTitle().equals("")) {
            if (card.getType().equals("Reminder")) {
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#b71c1c"));
                holder.parentCard.setRadius(70);
                int height = 0;
                if (card.getReminderArray()!=null) {
                    height = card.getReminderArray().size();
                    holder.parentCard.getLayoutParams().height = (int) convertDpToPixel(150 + (height * 40));

                    final ArrayList<CheckedReminderList> reminderArray = card.getReminderArray();
                    final CheckedRemindersListAdapter listAdapter = new CheckedRemindersListAdapter(mContext, reminderArray);
                    holder.reminderArrayListView.setAdapter(listAdapter);

                    holder.reminderArrayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (view != null) {
                                CheckedTextView x = (CheckedTextView) view.findViewById(R.id.reminder_list_item1);
                                if (reminderArray.get(position).getReminderCheck()) {
                                    x.setChecked(false);
                                } else {
                                    x.setChecked(true);
                                }
                                reminderArray.get(position).setReminderCheck(!reminderArray.get(position).getReminderCheck());
                                card.setReminderArray(reminderArray);
                                mRef.child(card.getTitle()).setValue(card);
                                lastPosition++;
                            }
                        }
                    });
                    //TODO: FIX IF ARRAY BECOMES EMPTY

                    for (int i = reminderArray.size() - 1; i >= 0; i--) {
                        if (reminderArray.get(i).getReminderCheck()) {
                            reminderArray.remove(i);
                            card.setReminderArray(reminderArray);
                            mRef.child(card.getTitle()).setValue(card);
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                    height = card.getReminderArray().size();
                    holder.parentCard.getLayoutParams().height = (int) convertDpToPixel(150 + (height * 35));
                }
                else{
                    holder.parentCard.setCardBackgroundColor(Color.parseColor("#559e83"));
                    holder.message.setText("All done!");
                    holder.message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    holder.message.setTextSize(25);
                }
            }
            else if (card.getType().equals("Poll")){
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#546e7a"));
            }
            else{
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#1565c0"));
            }
            holder.type.setText(card.getType());
            holder.title.setText(card.getTitle());
            if (!card.getType().equals("Reminder")) {
                holder.message.setText(card.getMessage());
            }
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
