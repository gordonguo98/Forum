package com.gordon.forum.Model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    @JSONField(name="post_id")
    private int postId;
    @JSONField(name="question")
    private String question;
    @JSONField(name="reply_num")
    private int replyNum;
    @JSONField(name="like_num")
    private int likeNum;
    @JSONField(name="create_time")
    private String createTime;
    @JSONField(name="course_id")
    private int courseId;
    @JSONField(name="creator")
    private User creator;
    @JSONField(name="content_images", serialize = false, deserialize = false)
    private List<String> contentImages = new ArrayList<>();


    @JSONField(serialize = false, deserialize = false)
    private List<Message> messages = new ArrayList<>();

    private Post(){}

    public Post(int postId, User creator, String question, String createTime, List<String> contentImages, int likeNum, int replyNum, List<Message> messages) {
        this.postId = postId;
        this.creator = creator;
        this.question = question;
        this.createTime = createTime;
        this.contentImages = contentImages;
        this.likeNum = likeNum;
        this.replyNum = replyNum;
        this.messages = messages;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getContentImages() {
        return contentImages;
    }

    public void setContentImages(List<String> contentImages) {
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

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
