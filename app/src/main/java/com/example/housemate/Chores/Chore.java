package com.example.housemate.Chores;

public class Chore {
    private String name;
    private String day;
    private String assignee;
    private String choresId;
    private boolean isDone;

    private Chore() {}

    private Chore(String name, String day, String assignee){
        this.name = name;
        this.day = day;
        this.assignee = assignee;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean done) {
        isDone = done;
    }

    public String getChoresId() {
        return choresId;
    }

    public void setChoresId(String choreId) {
        this.choresId = choreId;
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

    @Override
    public String toString() {
        return "Chore{" +
                "name='" + name + '\'' +
                ", day='" + day + '\'' +
                ", assignee='" + assignee + '\'' +
                ", choresId='" + choresId + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
