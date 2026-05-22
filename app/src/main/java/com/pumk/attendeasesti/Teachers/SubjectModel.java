package com.pumk.attendeasesti.Teachers;

public class SubjectModel {
    private String subject_name, day, time;

    // Required for Firestore toObject()
    public SubjectModel() {}

    public SubjectModel(String subject_name, String day, String time) {
        this.subject_name = subject_name;
        this.day          = day;
        this.time         = time;
    }

    public String getSubject_name()           { return subject_name; }
    public void   setSubject_name(String v)   { this.subject_name = v; }

    public String getDay()                    { return day; }
    public void   setDay(String v)            { this.day = v; }

    public String getTime()                   { return time; }
    public void   setTime(String v)           { this.time = v; }
}