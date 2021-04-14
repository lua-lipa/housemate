package com.example.housemate.Chores;

public class Chore {
    private String name; //name of chore
    private String date; //due date for the chore
    private String assignee; //who needs to complete the chore
    private String choresId; //the id assigned to chore in firestore
    private String creator; //the user that created the chore
    private boolean isDone; //has a user completed the chore

    private Chore() {} //required empty constructor

    private Chore(String name, String date, String assignee, String choresId, String creator){ //chore object constructor
        this.name = name;
        this.date = date;
        this.assignee = assignee;
        this.choresId = choresId;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    } //returns the user that created the chore

    public void setCreator(String creator) {
        this.creator = creator;
    } //set who created the chore

    public boolean getIsDone() {
        return isDone;
    } //returns true if a user has completed the chore

    public void setIsDone(boolean done) {
        isDone = done;
    } //set to true when a users swipes left on the chore

    public String getChoresId() {
        return choresId;
    } //returns the chores id

    public void setChoresId(String choreId) {
        this.choresId = choreId;
    } //sets the chore id

    public String getName() {
        return name;
    } //returns the name of the chore

    public void setName(String name) {
        this.name = name;
    } //sets the name of the chore

    public String getDate() {
        return date;
    } //returns the due date of the chore

    public void setDate(String date) {
        this.date = date;
    } //sets the due date of the chore

    public String getAssignee() {
        return assignee;
    } //get the user thats assigned to the chore

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    } //set the user thats assigned to the chore

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
