package com.example.housemate.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Chores.Chore;
import com.example.housemate.R;
import com.example.housemate.chat.Chat;
import com.example.housemate.util.HousemateAPI;

import java.util.List;
import java.util.Objects;


//what allows us to bind views to our rows & the actual data we will be getting from firebase
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {
    private List<Chat> chatList;
    private Context context;
    HousemateAPI housemateAPI = HousemateAPI.getInstance();

    //constructor
    public ChatRecyclerViewAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //creating a view for each data row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //bind data to our items
        //position = where are we on our list?
        Chat chat = Objects.requireNonNull(chatList).get(position);
        holder.chatMessageText.setText(chat.getMessage());
        holder.chatMessageName.setText(chat.getSender());


        /*
        ViewGroup.LayoutParams lp = holder.chatLayout.getLayoutParams();
        lp.height = holder.chatMessageCardView.getHeight() + 20;
        holder.chatLayout.setLayoutParams(lp);
         */

    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(chatList).size();
    } //how many chores

    //the items that make up each row
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chatMessageText;
        public TextView chatMessageName;
        public ConstraintLayout chatLayout;
        public CardView chatMessageCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatMessageText = itemView.findViewById(R.id.chatMessageText);
            chatMessageName = itemView.findViewById(R.id.chatMessageName);
            chatMessageCardView = itemView.findViewById(R.id.chatMessageCardView);
            chatLayout = itemView.findViewById(R.id.chatLayout);

        }
    }
}
