package com.example.housemate.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
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
//        holder.assignee.setText(bill.getAssignee());
        holder.date.setText(bill.getDate());
    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(billsList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView billCard;
        public TextView title;
        public TextView amount;
        public TextView date;
//        public TextView assignee;
//        public TextView removeBillButton;
//        public CheckBox billCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bills_row_title);
            amount = itemView.findViewById(R.id.bills_row_amount);
            date = itemView.findViewById(R.id.bills_row_date);
//            assignee = itemView.findViewById(R.id.bills_row_assignee);
//            billCheckBox = itemView.findViewById(R.id.bills_row_checkbox);
//            removeBillButton = itemView.findViewById(R.id.bills_row_remove_bill);
            billCard = itemView.findViewById(R.id.bills_row_card_view);

//            removeBillButton.setVisibility(View.INVISIBLE);
//            removeBillButton.setClickable(false);

//            billCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked) {
//                        removeBillButton.setVisibility(View.VISIBLE);
//                        billCard.setBackgroundColor(Color.TRANSPARENT);
//                        removeBillButton.setClickable(true);
//                        removeBillButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Log.d("bills recycler view", "remove button clicked");
//                                /* remove the bill from the screen & database */
//
//                            }
//                        });
//                    } else {
//                        removeBillButton.setVisibility(View.INVISIBLE);
//                        billCard.setBackgroundColor(Color.WHITE);
//                    }
//                }
//
//            });




        }
    }
}
