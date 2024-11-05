package com.example.skaip.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaip.Constants;
import com.example.skaip.Preferences;
import com.example.skaip.R;
import com.example.skaip.models.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

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

    // Sets data in the created MessageViewHolder(view) element
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageContent.setText(message.getText());
        String userId = preferences.getString(Constants.KEY_USER_ID);
        holder.timestamp.setText(timestampFormat(message.getTimestamp()));

        if (message.sentByUser(userId)) {
            holder.senderUsername.setVisibility(View.GONE);
            holder.senderImage.setVisibility(View.GONE);
        }
        else {
            holder.senderImage.setImageBitmap(decodeImage(message.getEncodedImage()));
            holder.senderImage.setVisibility(View.VISIBLE);
            holder.senderUsername.setText(message.getSenderName());
            holder.senderUsername.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp;
        TextView messageContent;
        TextView senderUsername;
        ImageView senderImage;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.textMessage);
            senderUsername = itemView.findViewById(R.id.textUsername);
            senderImage = itemView.findViewById(R.id.imageProfile);
            timestamp = itemView.findViewById(R.id.textDateTime);
        }
    }

    private Bitmap decodeImage(String base64String){
        if (base64String == null || base64String.isEmpty()) {
            Bitmap black = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(black);
            canvas.drawColor(Color.BLACK);
            return black;
        }
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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