package com.gordon.forum.Model;

import java.util.Date;

public class Message {

    private User sender;
    private Date senderTime;
    private String content;

    public Message(User sender, Date senderTime, String content) {
        this.sender = sender;
        this.senderTime = senderTime;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Date getSenderTime() {
        return senderTime;
    }

    public void setSenderTime(Date senderTime) {
        this.senderTime = senderTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
