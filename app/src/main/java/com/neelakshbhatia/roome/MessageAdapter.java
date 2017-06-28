package com.neelakshbhatia.roome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

/**
 * Created by neelakshbhatia on 2017-05-24.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Card> messageList;
    private Context mContext;
    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //Textviews from Card
        public TextView title, message;

        public MyViewHolder(View view) {
            super(view);
            //text from card
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
        Log.d("bad", String.valueOf(position));
        if (!card.getTitle().equals("") && !card.getMessage().equals("")) {
            holder.title.setText(card.getTitle());
            holder.message.setText(card.getMessage());
        }
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(mContext, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
