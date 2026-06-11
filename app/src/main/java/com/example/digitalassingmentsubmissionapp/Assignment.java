package com.example.digitalassingmentsubmissionapp;

public class Assignment {

    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String student;

    public Assignment(int id, String title, String description, String dueDate, String student) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.dueDate     = dueDate;
        this.student     = student;
    }

    public int getId()             { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getDueDate()     { return dueDate; }
    public String getStudent()     { return student; }

    public void setId(int id)               { this.id = id; }
    public void setTitle(String title)      { this.title = title; }
    public void setDescription(String desc) { this.description = desc; }
    public void setDueDate(String dueDate)  { this.dueDate = dueDate; }
    public void setStudent(String student)  { this.student = student; }

    @Override
    public String toString() {
        return "📌 " + title + "\n📝 " + description + "\n📅 Due: " + dueDate;
    }
}