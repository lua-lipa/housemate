package com.example.housemate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.housemate.Bills.BillActivity;
import com.example.housemate.R;
import java.util.List;
import java.util.Objects;

public class BillsActivityRecyclerViewAdapter extends RecyclerView.Adapter<BillsActivityRecyclerViewAdapter.ViewHolder> {
    private List<BillActivity> billsActivityList;
    private Context context;

    public BillsActivityRecyclerViewAdapter(List<BillActivity> billsList, Context context) {
        this.billsActivityList = billsList;
        this.context = context;
    }
    @NonNull
    @Override
    public BillsActivityRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bills_activity_row, parent, false);
        return new BillsActivityRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillsActivityRecyclerViewAdapter.ViewHolder holder, int position) {
        BillActivity billActivity = Objects.requireNonNull(billsActivityList).get(position);
        holder.message.setText(billActivity.getMessage());
        holder.date.setText(billActivity.getDate());
    }


    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(billsActivityList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.bills_activity_message);
            date = itemView.findViewById(R.id.bills_activity_date);
        }
    }


}
