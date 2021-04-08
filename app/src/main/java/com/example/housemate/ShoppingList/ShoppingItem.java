package com.example.housemate.ShoppingList;

public class ShoppingItem {
    private String item;
    private String date;
    private String shoppingListId;

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

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "item='" + item + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
