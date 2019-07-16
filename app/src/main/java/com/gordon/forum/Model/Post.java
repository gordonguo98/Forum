package com.gordon.forum.Model;

import java.util.Date;
import java.util.List;

public class Post {

    private String postId;
    private User creator;
    private String question;
    private Date createTime;
    private String contentImages;
    private int likeNum;
    private int replyNum;
    private List<Message> messages;

    public Post(String postId, User creator, String question, Date createTime, String contentImages, int likeNum, int replyNum, List<Message> messages) {
        this.postId = postId;
        this.creator = creator;
        this.question = question;
        this.createTime = createTime;
        this.contentImages = contentImages;
        this.likeNum = likeNum;
        this.replyNum = replyNum;
        this.messages = messages;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContentImages() {
        return contentImages;
    }

    public void setContentImages(String contentImages) {
        this.contentImages = contentImages;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
