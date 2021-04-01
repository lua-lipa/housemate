package com.example.housemate.Bills;

public class Bill {

    private String title;
    private String amount;
    private String date;
    private String assignee;

    private Bill() {}

    private Bill(String title, String amount, String date, String assignee) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.assignee = assignee;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String newAmount) {
        amount = newAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String newDate) {
        date = newDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String newAssignee) {
        assignee = newAssignee;
    }
}

