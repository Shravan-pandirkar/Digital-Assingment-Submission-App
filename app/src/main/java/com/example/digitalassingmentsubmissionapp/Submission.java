package com.example.digitalassingmentsubmissionapp;

public class Submission {

    private int subId;
    private String studentName;
    private String rollNo;
    private String enrollment;
    private String date;
    private String subject;
    private String teacherUsername;
    private String submittedBy;
    private String filePath; // ✅ NEW

    // ✅ Updated constructor — now 9 parameters
    public Submission(int subId, String studentName, String rollNo, String enrollment,
                      String date, String subject, String teacherUsername,
                      String submittedBy, String filePath) {
        this.subId           = subId;
        this.studentName     = studentName;
        this.rollNo          = rollNo;
        this.enrollment      = enrollment;
        this.date            = date;
        this.subject         = subject;
        this.teacherUsername = teacherUsername;
        this.submittedBy     = submittedBy;
        this.filePath        = filePath; // ✅ NEW
    }

    // Getters
    public int    getSubId()           { return subId; }
    public String getStudentName()     { return studentName; }
    public String getRollNo()          { return rollNo; }
    public String getEnrollment()      { return enrollment; }
    public String getDate()            { return date; }
    public String getSubject()         { return subject; }
    public String getTeacherUsername() { return teacherUsername; }
    public String getSubmittedBy()     { return submittedBy; }
    public String getFilePath()        { return filePath; } // ✅ NEW

    // Setters
    public void setSubId(int subId)                        { this.subId = subId; }
    public void setStudentName(String studentName)         { this.studentName = studentName; }
    public void setRollNo(String rollNo)                   { this.rollNo = rollNo; }
    public void setEnrollment(String enrollment)           { this.enrollment = enrollment; }
    public void setDate(String date)                       { this.date = date; }
    public void setSubject(String subject)                 { this.subject = subject; }
    public void setTeacherUsername(String teacherUsername) { this.teacherUsername = teacherUsername; }
    public void setSubmittedBy(String submittedBy)         { this.submittedBy = submittedBy; }
    public void setFilePath(String filePath)               { this.filePath = filePath; } // ✅ NEW

    // ✅ Updated toHtmlString — now includes file name display
    public String toHtmlString() {
        String fileDisplay = (filePath != null && !filePath.isEmpty())
                ? filePath.substring(filePath.lastIndexOf('/') + 1) // show just file name
                : "No file attached";

        return "<b><font color='#1565C0'>👤 Name:</font></b> <font color='#212121'>" + studentName + "</font><br>" +
                "<b><font color='#1565C0'>📋 Roll No:</font></b> <font color='#212121'>" + rollNo + "</font><br>" +
                "<b><font color='#1565C0'>🆔 Enrollment:</font></b> <font color='#212121'>" + enrollment + "</font><br>" +
                "<b><font color='#1565C0'>📚 Subject:</font></b> <font color='#E65100'>" + subject + "</font><br>" +
                "<b><font color='#1565C0'>👨‍🏫 Teacher:</font></b> <font color='#388E3C'>" + teacherUsername + "</font><br>" +
                "<b><font color='#1565C0'>🔑 Username:</font></b> <font color='#212121'>" + submittedBy + "</font><br>" +
                "<b><font color='#1565C0'>📅 Date:</font></b> <font color='#C62828'>" + date + "</font><br>" +
                // ✅ NEW — file name shown in card
                "<b><font color='#1565C0'>📎 File:</font></b> <font color='#00695C'>" + fileDisplay + "</font>";
    }
}