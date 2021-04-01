package com.example.housemate.util;

import android.app.Application;

import java.util.List;
import java.util.Map;

public class HousemateAPI extends Application {

    private String userName;
    private String userId;
    private String familyName;
    private String familyId;

    private List<Map<String, Object>> membersList;

    private static HousemateAPI instance;

    public static HousemateAPI getInstance() {
        if (instance == null) {
            instance = new HousemateAPI();
        }
        return instance;
    }

    public HousemateAPI() { }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId)  {
        this.userId = userId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }


    public List<Map<String, Object>> getMembersList() {
        return membersList;
    }

    public void setMembersList(List<Map<String, Object>> membersList) {
        this.membersList = membersList;
    }


}
