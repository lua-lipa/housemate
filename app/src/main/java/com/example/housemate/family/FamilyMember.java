package com.example.housemate.family;

import java.util.HashMap;
import java.util.Map;

public class FamilyMember {
    private Boolean isAdmin;
    private String name;
    private String userId;

    public FamilyMember(Map<String, Object> familyMember) {
        this.isAdmin = (Boolean) familyMember.get("isAdmin");
        this.name = (String) familyMember.get("name");
        this.userId = (String) familyMember.get("userId");

    }

    public FamilyMember(Boolean isAdmin, String name, String userId) {
        this.isAdmin = isAdmin;
        this.name = name;
        this.userId = userId;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
