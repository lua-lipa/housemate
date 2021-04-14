package com.example.housemate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.R;
import com.example.housemate.ShoppingList.ShoppingItem;
import com.example.housemate.util.HousemateAPI;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* what allows us to bind views to our rows & the actual data we will be getting from firebase */
public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.ViewHolder> {
    //necessary shopping lists in order to reload the recycler view and display them accordingly
    private List<ShoppingItem> shoppingList;
    private List<ShoppingItem> checkedShoppingList;
    private List<ShoppingItem> shoppingListItemsToDelete;
    private Context context;
    HousemateAPI housemateAPI = HousemateAPI.getInstance();

    public ShoppingRecyclerViewAdapter(){
        //required empty constructor
    }

    public ShoppingRecyclerViewAdapter(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.checkedShoppingList = shoppingList;
        housemateAPI.setCheckedShoppingList(checkedShoppingList);
        shoppingListItemsToDelete = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* creating a view for each data row */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* bind data to our items */
        /* position = where are we on our list? */
        ShoppingItem shoppingItem = Objects.requireNonNull(shoppingList).get(position);
        holder.name.setText(shoppingItem.getItem());
        holder.date.setText(shoppingItem.getDate());
        holder.user.setText(shoppingItem.getUser());
        holder.checkBox.setVisibility(View.VISIBLE);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkedShoppingList.remove(shoppingItem);
                    shoppingListItemsToDelete.add(shoppingItem);
                }else{
                    checkedShoppingList.add(shoppingItem);
                    shoppingListItemsToDelete.remove(shoppingItem);
                }
                housemateAPI.setCheckedShoppingList(checkedShoppingList);
                housemateAPI.setShoppingListItemsToDelete(shoppingListItemsToDelete);
            }
        });
    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(shoppingList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //setting the vairiables to the shopping_row.xml
        public TextView name;
        public TextView date;
        public CheckBox checkBox;
        public FloatingActionButton deleteFAB;
        public TextView user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //references to everything in the shopping_row.xml
            name = itemView.findViewById(R.id.shopping_row_item);
            date = itemView.findViewById(R.id.shopping_row_date);
            checkBox = itemView.findViewById(R.id.shopping_row_checkbox);
            deleteFAB = itemView.findViewById(R.id.deleteItemFAB);
            user = itemView.findViewById(R.id.shopping_row_name_display);
            checkBox.setChecked(false);
        }
    }
}

