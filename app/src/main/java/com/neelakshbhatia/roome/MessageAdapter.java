package com.neelakshbhatia.roome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Card> messageList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            message = (TextView) view.findViewById(R.id.count);
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
        holder.title.setText(card.getTitle());
        holder.message.setText(card.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
