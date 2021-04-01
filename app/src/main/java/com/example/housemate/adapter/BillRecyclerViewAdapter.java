package com.example.housemate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Bills.Bill;
import com.example.housemate.R;

import java.util.List;
import java.util.Objects;


/* what allows us to bind views to our rows & the actual data we will be getting from firebase */
public class BillRecyclerViewAdapter extends RecyclerView.Adapter<BillRecyclerViewAdapter.ViewHolder> {
    private List<Bill> billsList;
    private Context context;

    public BillRecyclerViewAdapter(List<Bill> billsList, Context context) {
        this.billsList = billsList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* creating a view for each data row */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bills_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* bind data to our items */
        /* position = where are we on our list? */

        Bill bill = Objects.requireNonNull(billsList).get(position);
        holder.title.setText(bill.getTitle());
        holder.amount.setText(bill.getAmount() + "€");
        holder.assignee.setText(bill.getAssignee());
        holder.date.setText(bill.getDate());
    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(billsList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView amount;
        public TextView date;
        public TextView assignee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bills_row_title);
            amount = itemView.findViewById(R.id.bills_row_amount);
            date = itemView.findViewById(R.id.bills_row_date);
            assignee = itemView.findViewById(R.id.bills_row_assignee);


        }
    }
}
