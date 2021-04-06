package com.example.housemate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.R;
import com.example.housemate.ShoppingList.ShoppingItem;

import java.util.List;
import java.util.Objects;

/* what allows us to bind views to our rows & the actual data we will be getting from firebase */
public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.ViewHolder> {
    private List<ShoppingItem> shoppingList;
    private Context context;

    public ShoppingRecyclerViewAdapter(){}

    public ShoppingRecyclerViewAdapter(List<ShoppingItem> shoppingList, Context context) {
        this.shoppingList = shoppingList;
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
    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(shoppingList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.shopping_row_item);
            date = itemView.findViewById(R.id.shopping_row_date);
        }
    }
}

