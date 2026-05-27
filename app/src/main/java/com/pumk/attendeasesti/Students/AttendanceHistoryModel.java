package com.pumk.attendeasesti.Students;

public class AttendanceHistoryModel {
    private String date, dayofweek, month, status, subject;

    public AttendanceHistoryModel() {}

    public AttendanceHistoryModel(String date, String dayofweek, String month,
                                  String status, String subject) {
        this.date      = date;
        this.dayofweek = dayofweek;
        this.month     = month;
        this.status    = status;
        this.subject   = subject;
    }

    public String getDate()                  { return date; }
    public void   setDate(String v)          { this.date = v; }

    public String getDayofweek()             { return dayofweek; }
    public void   setDayofweek(String v)     { this.dayofweek = v; }

    public String getMonth()                 { return month; }
    public void   setMonth(String v)         { this.month = v; }

    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status = v; }

    public String getSubject()               { return subject; }
    public void   setSubject(String v)       { this.subject = v; }
}