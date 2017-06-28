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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Comments> commentList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message;

        public MyViewHolder(View view) {
            super(view);
            //name = (TextView) view.findViewById(R.id.name);
            //message = (TextView) view.findViewById(R.id.commentText);
        }
    }

    public CommentAdapter(Context mContext, List<Comments> commentList) {
        this.mContext = mContext;
        this.commentList = commentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Comments message = commentList.get(position);
       // holder.name.setText(card.getTitle());
        holder.message.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
