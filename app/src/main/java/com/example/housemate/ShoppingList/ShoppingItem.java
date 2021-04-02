package com.example.housemate.ShoppingList;

public class ShoppingItem {
    private String name;
    private String date;
    private boolean isBought;

    private ShoppingItem(){}

    private ShoppingItem(String name, String date){
        this.name=name;
        this.date=date;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public boolean isBought() { return isBought; }

    public void setBought(boolean isBought) { this.isBought = isBought; }

    @Override
    public String toString() { return "ShoppingItem{" + "name='" + name + '\'' + ", date='" + date + '\'' + '}'; }
}
