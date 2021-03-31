package com.example.housemate;

import java.util.Date;

public class ShoppingItem {
    private int item_id;
    private String item;
    private Date dateAdded;
    private boolean itemState;

    public ShoppingItem(){}

    public ShoppingItem(String item, Date dateAdded, boolean itemState){
        this.item_id = item_id;
        this.item = item;
        this.dateAdded = dateAdded;
        this.itemState = itemState;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isItemState() {
        return itemState;
    }

    public void setItemState(boolean itemState) {
        this.itemState = itemState;
    }

    @Override
    public String toString() {
        return "shoppingItems{" +
//                "item_id=" + item_id +
                ", item='" + item + '\'' +
                ", dateAdded=" + dateAdded +
                ", itemState=" + itemState +
                '}';
    }
}
