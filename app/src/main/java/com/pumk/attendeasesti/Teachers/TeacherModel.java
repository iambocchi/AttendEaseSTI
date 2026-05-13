package com.pumk.attendeasesti.Teachers;

// TeacherModel.java
public class TeacherModel {

    private String name, email, course, status;
    private int imageResId;

    public TeacherModel(String name, String email, String course, String status, int imageResId) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public String getStatus() {
        return status;
    }

    public int getImageResId() {
        return imageResId;
    }
}