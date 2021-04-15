package com.example.housemate;

public class HouseActivity {

    private String message;
    private String date;
    private String houseActivityId;
    private String type;

    public HouseActivity(String message, String date, String houseActivityId) {
        this.message = message;
        this.date = date;
        this.houseActivityId = houseActivityId;
    }
//    class for house activity

    public HouseActivity() {}

    public void setMessage(String message) {
        this.message  = message;
    }

    public String getMessage() { return message; }

    public void setDate (String date) {
        this.date = date;
    }

    public String getDate() { return date; }

    public String getHouseActivityId() {
        return houseActivityId;
    }

    public void setHouseActivityId(String houseActivityId) {
        this.houseActivityId = houseActivityId;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }


}
