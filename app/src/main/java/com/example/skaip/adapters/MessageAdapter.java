package com.example.skaip.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaip.R;
import com.example.skaip.models.Message;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    // Returns the message view/element/layout, parent is the RecyclerView
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
        Log.d("msg", "COUNT: " + getItemCount());
        return new MessageViewHolder(view);
    }

    // Provides data for the created MessageViewHolder(view) element
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        Log.d("msg", "MESSAGE: ");
        holder.messageContent.setText(message.getText());
        holder.senderId.setText(message.getSenderId());
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
            senderId = itemView.findViewById(R.id.textName);
            timestamp = itemView.findViewById(R.id.textDateTime);
        }
    }

    private String timestampFormat(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/YYYY HH:MM", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        else {
            return "";
        }
    }
}