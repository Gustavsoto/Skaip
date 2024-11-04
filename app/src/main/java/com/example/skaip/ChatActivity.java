package com.example.skaip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaip.adapters.MessageAdapter;
import com.example.skaip.models.Message;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ListenerRegistration listenerReg;
    private Preferences preferences;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private FrameLayout sendButton;
    private TextView groupName;
    private FirebaseFirestore database;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        intent = getIntent();

        messageEditText = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.layoutSend);
        groupName = findViewById(R.id.textName);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        preferences = new Preferences(getApplicationContext());
        database = FirebaseFirestore.getInstance();

        loadGroupName();
        loadMessages(intent.getStringExtra("groupId"));
        sendButton.setOnClickListener(v -> sendMessage());
    }
    private void loadGroupName() {
        String groupNameString = intent.getStringExtra("groupName");
        if(groupNameString != null) {
            groupName.setText(groupNameString);
        }
        else {
            groupName.setText("No group name!");
        }
    }
    private void sendMessage() {
        String content = messageEditText.getText().toString().trim();
        if (!content.isEmpty()) {

            Map<String, Object> message = new HashMap<>();
            message.put("senderId", preferences.getString(Constants.KEY_NAME));
            message.put("text", content);
            message.put("timestamp", FieldValue.serverTimestamp());

            Intent intent = getIntent();
            String groupId = intent.getStringExtra("groupId");

            database.collection("groups").document(groupId).collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Sent message ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    messageEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error sending message", Toast.LENGTH_SHORT).show();
                });
        }
    }

    public void loadMessages(String groupId) {
        listenerReg = database.collection("groups").document(groupId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener((querySnapshot, exception) -> {
                if (exception != null) {
                    Toast.makeText(getApplicationContext(), "Error loading messages", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (querySnapshot != null) {
                    messageList.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        messageList.add(message);
                        Log.d("msg", "Found message: " + message.getText());
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No messages to show!", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerReg != null) {
            listenerReg.remove();
        }
    }
}