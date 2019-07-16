package com.gordon.forum.Model;

import android.graphics.Bitmap;

public class User {

    private String userId;
    private Bitmap profile_photo;
    private String phone_num;
    private String name;
    private String university;
    private String major;

    private int grade;

    public User(String userId, Bitmap profile_photo, String phone_num, String name, String university, String major, int grade) {
        this.userId = userId;
        this.profile_photo = profile_photo;
        this.phone_num = phone_num;
        this.name = name;
        this.university = university;
        this.major = major;
        this.grade = grade;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Bitmap getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(Bitmap profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
