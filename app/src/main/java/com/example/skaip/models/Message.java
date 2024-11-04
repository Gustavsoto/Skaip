package com.example.skaip.models;
import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Message implements Serializable {
    private String senderId;
    private String text;
    Timestamp timestamp;

    // Empty constructor (For firebase)
    public Message() { }

    // Regular constructor
    public Message(String senderId, String text, Timestamp timestamp) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public boolean sentByUser(String userId) {
        return senderId.equals(userId);
    }
    public String getSenderId() {
        return senderId;
    }
    public String getText() {
        return text;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
