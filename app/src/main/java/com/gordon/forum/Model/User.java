package com.gordon.forum.Model;

import android.graphics.Bitmap;

import com.alibaba.fastjson.annotation.JSONField;

public class User {

    @JSONField(name="email")
    private String phone_num;
    @JSONField(name="name")
    private String name;
    @JSONField(name="university")
    private String university;
    @JSONField(name="major")
    private String major;
    @JSONField(name="grade")
    private String grade;
    @JSONField(name="photo_url")
    private String profile_photo;
    @JSONField(serialize = false, deserialize = false)
    private Bitmap profile_photo_bitmap;

    private User(){}

    public User(String phone_num, String profile_photo, String name, String university, String major, String grade) {
        this.phone_num = phone_num;
        this.profile_photo = profile_photo;
        this.name = name;
        this.university = university;
        this.major = major;
        this.grade = grade;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Bitmap getProfile_photo_bitmap() {
        return profile_photo_bitmap;
    }

    public void setProfile_photo_bitmap(Bitmap profile_photo_bitmap) {
        this.profile_photo_bitmap = profile_photo_bitmap;
    }
}
