package com.example.housemate.Chores;

public class Chore {
    private String name;
    private String day;
    private String assignee;

    private Chore(String name, String date, String assignee){
        this.name = name;
        this.day = day;
        this.assignee = assignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
