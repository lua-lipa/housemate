package com.example.housemate.Bills;

public class Bill {

    private String title;
    private String amount;
    private String date;
    private String assignee;
    private String userId;
    private boolean isPaid;
    private String billsId;

    private Bill() {}

    private Bill(String title, String amount, String date, String assignee, String userId, String billsId) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.assignee = assignee;
        this.userId = userId;
        this.billsId = billsId;
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

    public String getUserId() { return userId; }

    public void setUserId(String userId) {  this.userId = userId; }

    public boolean getIsPaid() { return isPaid; }

    public void setIsPaid() { isPaid = true; }

    public String getBillsId() { return billsId;  }

    public void setBillsId(String billsId) { this.billsId = billsId;  }
}

