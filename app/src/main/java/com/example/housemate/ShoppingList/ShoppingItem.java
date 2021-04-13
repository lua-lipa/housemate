package com.example.housemate.ShoppingList;

public class ShoppingItem {
    //standard object class
    //allows us to create shopping item objects

    //item in the db
    private String item;
    //date the item was added
    private String date;
    //shopping list id in reference in the database so we can delete it and manipulate it
    private String shoppingListId;

    //isBought check in order to see if the item has been purchased or not
    private boolean isBought;

    //getters and setters for all


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

    //toString method to help with testing

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "item='" + item + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
