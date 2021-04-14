package com.example.housemate.Bills;

public class BillActivity {

    private String message;
    private String date;
    private String billActivityId;

    public BillActivity(String message, String date, String billActivityId) {
        this.message = message;
        this.date = date;
        this.billActivityId = billActivityId;
    }

    public BillActivity() {}

    public void setMessage(String message) {
        this.message  = message;
    }

    public String getMessage() { return message; }

    public void setDate (String date) {
        this.date = date;
    }

    public String getDate() { return date; }

    public String getBillActivityId() {
        return billActivityId;
    }

    public void setBillActivityId(String billActivityId) {
        this.billActivityId = billActivityId;
    }
}
