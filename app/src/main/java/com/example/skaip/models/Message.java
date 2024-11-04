package com.example.skaip.models;
import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Message implements Serializable {
    private String senderId;
    private String text;
    private String senderName;
    private String encodedImage;
    Timestamp timestamp;

    // Empty constructor (For firebase)
    public Message() { }

    // Regular constructor
    public Message(String senderId, String text, String senderName, Timestamp timestamp, String encodedImage) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.timestamp = timestamp;
        this.encodedImage = encodedImage;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public boolean sentByUser(String userId) {
        return senderId.equals(userId);
    }
    public String getSenderId() {
        return senderId;
    }
    public String getSenderName() {
        return senderName;
    }
    public String getEncodedImage() {
        return encodedImage;
    }
    public String getText() {
        return text;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
