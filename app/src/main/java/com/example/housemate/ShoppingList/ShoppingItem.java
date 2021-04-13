package com.example.housemate.ShoppingList;

public class ShoppingItem {
    private String item;
    private String date;
    private String shoppingListId;
    private boolean isBought;
    //add is bought

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(String shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public boolean getIsBought() {
        return isBought;
    }

    public void setIsBought(boolean bought) {
        isBought = bought;
    }

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "item='" + item + '\'' +
                ", date='" + date + '\'' +
                ", shoppingListId='" + shoppingListId + '\'' +
                ", isBought=" + isBought +
                '}';
    }
}
