package com.gordon.forum.Model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class Message {

    @JSONField(name="post_id")
    private int postId;
    @JSONField(name="message_id")
    private int messageId;
    @JSONField(name="content")
    private String content;
    @JSONField(name="send_time")
    private String sendTime;
    @JSONField(name="sender")
    private User sender;
    @JSONField(name="content_images")
    private List<String> contentImages = new ArrayList<>();
    @JSONField(name="content_files")
    private List<String> contentFiles = new ArrayList<>();

    public Message(){}

    public Message(int postId, int messageId, User sender, String senderTime, String content, List<String> contentImages, List<String> contentFiles) {
        this.postId = postId;
        this.messageId = messageId;
        this.sender = sender;
        this.sendTime = senderTime;
        this.content = content;
        this.contentImages = contentImages;
        this.contentFiles = contentFiles;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getContentImages() {
        return contentImages;
    }

    public void setContentImages(List<String> contentImages) {
        this.contentImages = contentImages;
    }

    public List<String> getContentFiles() {
        return contentFiles;
    }

    public void setContentFiles(List<String> contentFiles) {
        this.contentFiles = contentFiles;
    }
}
