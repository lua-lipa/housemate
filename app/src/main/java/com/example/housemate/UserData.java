package com.example.housemate;

public class UserData {
    private String email, fullname;

    public UserData(){
    }

    public UserData(String email) {
        this.email = email;
    }

    public UserData(String email, String fullname) {
        this.email = email;
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

