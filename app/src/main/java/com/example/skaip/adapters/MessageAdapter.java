package com.example.skaip.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaip.Constants;
import com.example.skaip.Preferences;
import com.example.skaip.R;
import com.example.skaip.models.Message;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    Preferences preferences;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        preferences = new Preferences(context);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        String userId = preferences.getString(Constants.KEY_USER_ID);
        Log.d("id", "id of msg: " + message.getSenderId());
        Log.d("id", "id of usr " + userId);
        return message.sentByUser(userId) ? 1 : 0;
    }

    @NonNull
    @Override
    // Returns the message view/element/layout, parent is the RecyclerView
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
        }
        return new MessageViewHolder(view);
    }

    // Provides data for the created MessageViewHolder(view) element
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageContent.setText(message.getText());
        //holder.senderId.setText(message.getSenderId());
        holder.timestamp.setText(timestampFormat(message.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        Log.d("ChatActivity", "item count: " + messages.size());
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp;
        TextView messageContent;
        TextView senderId;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.textMessage);
            //senderId = itemView.findViewById(R.id.textName);
            timestamp = itemView.findViewById(R.id.textDateTime);
        }
    }

    private String timestampFormat(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:MM", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        else {
            return "";
        }
    }
}