package com.example.housemate.chat;

import com.google.firebase.Timestamp;

public class Chat {
    private String sender;
    private String message;
    private Timestamp date;
    private String userId;

    public Chat() {

    }

    public Chat(String sender, String message, Timestamp date) {
        this.sender = sender;
        this.message = message;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
