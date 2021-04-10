package com.example.housemate.Chores;

public class Chore {
    private String name;
    private String date;
    private String assignee;
    private String choresId;
    private String creator;
    private boolean isDone;

    private Chore() {}

    private Chore(String name, String date, String assignee, String choresId, String creator){
        this.name = name;
        this.date = date;
        this.assignee = assignee;
        this.choresId = choresId;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

//    @Override
//    public String toString() {
//        return "Chore{" +
//                "name='" + name + '\'' +
//                ", day='" + day + '\'' +
//                ", assignee='" + assignee + '\'' +
//                ", choresId='" + choresId + '\'' +
//                ", isDone=" + isDone +
//                '}';
//    }
}
