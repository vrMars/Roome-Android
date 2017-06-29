package com.neelakshbhatia.roome;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Card> messageList;
    private Context mContext;
    private String cardOption = "";
    private int lastPosition = -1;

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
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Card card = messageList.get(position);
        if (!card.getTitle().equals("")) {
            if (card.getType().equals("Reminder")) {
                holder.parentCard.setCardBackgroundColor(Color.parseColor("#b71c1c"));
                holder.parentCard.setRadius(70);
                holder.parentCard.getLayoutParams().height = CardView.LayoutParams.WRAP_CONTENT;
                ArrayList<String> reminderArray = card.getReminderArray();
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext, R.layout.reminder_list_item,
                        reminderArray);
                holder.reminderArrayListView.setAdapter(listAdapter);
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

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
