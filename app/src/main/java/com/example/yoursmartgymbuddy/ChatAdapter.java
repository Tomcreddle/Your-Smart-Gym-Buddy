package com.example.yoursmartgymbuddy;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatMessage.SENT_BY_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bot_message, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).textView.setText(msg.getMessage());
        } else {
            ((BotViewHolder) holder).textView.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        UserViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.messageTextView);
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        BotViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
