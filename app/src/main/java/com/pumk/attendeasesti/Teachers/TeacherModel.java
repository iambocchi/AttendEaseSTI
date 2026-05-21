package com.pumk.attendeasesti.Teachers;

// TeacherModel.java
public class TeacherModel {
    private String name, email, department, campus, status;
    private int teacher_id;



    public TeacherModel(String name, String email, String department, String campus, int teacher_id)
        {
        this.name = name;
        this.email = email;
        this.department = department;
        this.campus = campus;
        this.teacher_id = teacher_id;
        this.status = "Present";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


