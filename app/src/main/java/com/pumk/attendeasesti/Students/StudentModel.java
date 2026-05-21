package com.pumk.attendeasesti.Students;

public class StudentModel {
    private String name;
    private String email;
    private String course;
    private String campus;
    private String academic_level;
    private String section;
    private String program;
    private String year_level;
    private String student_id;
    private String location;


    private String status;

    public StudentModel(String name, String email, String course, String campus, String academic_level, String section, String program, String year_level, String student_id, String location, String status) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.campus = campus;
        this.academic_level = academic_level;
        this.section = section;
        this.program = program;
        this.year_level = year_level;
        this.student_id = student_id;
        this.location = location;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getAcademic_level() {
        return academic_level;
    }

    public void setAcademic_level(String academic_level) {
        this.academic_level = academic_level;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getYear_level() {
        return year_level;
    }

    public void setYear_level(String year_level) {
        this.year_level = year_level;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
