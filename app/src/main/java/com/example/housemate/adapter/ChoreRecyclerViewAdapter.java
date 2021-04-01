package com.example.housemate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Chores.Chore;
import com.example.housemate.R;

import java.util.List;
import java.util.Objects;


/* what allows us to bind views to our rows & the actual data we will be getting from firebase */
public class ChoreRecyclerViewAdapter extends RecyclerView.Adapter<ChoreRecyclerViewAdapter.ViewHolder> {
    private List<Chore> choresList;
    private Context context;

    public ChoreRecyclerViewAdapter(List<Chore> billsList, Context context) {
        this.choresList = choresList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* creating a view for each data row */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chores_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* bind data to our items */
        /* position = where are we on our list? */

        Chore chore = Objects.requireNonNull(choresList).get(position);
        holder.name.setText(chore.getName());
        holder.day.setText(chore.getDay());
        holder.assignee.setText(chore.getAssignee());
    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(choresList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView day;
        public TextView assignee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ChoreChoreName);
            day = itemView.findViewById(R.id.ChoresChoreDay);
            assignee = itemView.findViewById(R.id.ChoresChoreAssignee);


        }
    }
}
