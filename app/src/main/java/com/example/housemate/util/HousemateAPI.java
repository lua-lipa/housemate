package com.example.housemate.util;

import android.app.Application;

import com.example.housemate.ShoppingList.ShoppingItem;
import com.example.housemate.family.FamilyMember;

import java.util.List;
import java.util.Map;

public class HousemateAPI extends Application {

    private String userName;
    private String userId;
    private String familyName;
    private String familyId;
    private String familyOwnerId;
    private Boolean isAdmin;

    private FamilyMember selectedMember;


    private List<Map<String, Object>> membersList;
    private String[] memberNames;

    private List<ShoppingItem> checkedShoppingList;
    private List<ShoppingItem> shoppingListItemsToDelete;


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

    public String getFamilyOwnerId() {
        return familyOwnerId;
    }

    public void setFamilyOwnerId(String familyOwnerId) {
        this.familyOwnerId = familyOwnerId;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }


    public List<Map<String, Object>> getMembersList() {
        return membersList;
    }

    public void setMembersList(List<Map<String, Object>> membersList) {
        this.membersList = membersList;
    }

    public Boolean isOwner(String userId) {
        return userId == familyOwnerId;
    }

    public FamilyMember getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(FamilyMember selectedMember) {
        this.selectedMember = selectedMember;
    }

    public List<ShoppingItem> getCheckedShoppingList() {
        return checkedShoppingList;
    }

    public void setCheckedShoppingList(List<ShoppingItem> checkedShoppingList) {
        this.checkedShoppingList = checkedShoppingList;
    }

    public List<ShoppingItem> getShoppingListItemsToDelete() {
        return shoppingListItemsToDelete;
    }

    public void setShoppingListItemsToDelete(List<ShoppingItem> shoppingListItemsToDelete) {
        this.shoppingListItemsToDelete = shoppingListItemsToDelete;
    }

    public String[] getMemberNames() {
        String[] memberNames = new String[membersList.size()];
        for(int i = 0; i < membersList.size(); i++) {
            Map<String, Object> memberMap = membersList.get(i);
            String memberName = (String) memberMap.get("name");
            memberNames[i] = memberName;
        }
        return memberNames;
    }

    public Map<String, Object> getMemberFromUserId(String userId) {
        for(int i = 0; i < membersList.size(); i++) {
            Map<String, Object> memberMap = membersList.get(i);
            String memberUserId = (String) memberMap.get("userId");
            if (memberUserId.equals(userId)) {
                return memberMap;
            }
        }
        return null;
    }



}
