package com.pumk.attendeasesti.Students;

public class StudentModel {
    private String name, email, program, course, section, campus;
    private String academic_level, location, status, student_id, year_level;

    // Required no-arg constructor for Firestore toObject()
    public StudentModel() {}

    public String getName()           { return name; }
    public void   setName(String v)   { this.name = v; }

    public String getEmail()          { return email; }
    public void   setEmail(String v)  { this.email = v; }

    public String getProgram()        { return program; }
    public void   setProgram(String v){ this.program = v; }

    public String getCourse()         { return course; }
    public void   setCourse(String v) { this.course = v; }

    public String getSection()        { return section; }
    public void   setSection(String v){ this.section = v; }

    public String getCampus()         { return campus; }
    public void   setCampus(String v) { this.campus = v; }

    public String getAcademic_level()        { return academic_level; }
    public void   setAcademic_level(String v){ this.academic_level = v; }

    public String getLocation()        { return location; }
    public void   setLocation(String v){ this.location = v; }

    public String getStatus()          { return status; }
    public void   setStatus(String v)  { this.status = v; }

    public String getStudent_id()        { return student_id; }
    public void   setStudent_id(String v){ this.student_id = v; }

    public String getYear_level()        { return year_level; }
    public void   setYear_level(String v){ this.year_level = v; }
}