package com.example.housemate.family;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Chores.Chore;
import com.example.housemate.R;
import com.example.housemate.ShoppingList.BottomSheetFragment;
import com.example.housemate.util.HousemateAPI;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/* what allows us to bind views to our rows & the actual data we will be getting from firebase */
public class FamilyRecyclerViewAdapter extends RecyclerView.Adapter<FamilyRecyclerViewAdapter.ViewHolder> {
    private List<Map<String, Object>> familyMembersList;
    private Context context;

    private static FamilyRecyclerViewAdapter instance;

    private HousemateAPI housemateAPI = HousemateAPI.getInstance();

    public FamilyRecyclerViewAdapter(List<Map<String, Object>> familyMembersList, Context context) {
        this.familyMembersList = familyMembersList;
        this.context = context;
    }

    public static FamilyRecyclerViewAdapter getInstance(List<Map<String, Object>> familyMembersList, Context context) {
        if (instance == null) {
            instance = new FamilyRecyclerViewAdapter(familyMembersList, context);
        }
        return instance;
    }

    public static FamilyRecyclerViewAdapter getInstance() {
        return instance;
    }

    public void updateData(List<Map<String, Object>> familyMembersList) {
        this.familyMembersList = familyMembersList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* creating a view for each data row */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.family_member_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    FamilyMember familyMember = new FamilyMember(familyMembersList.get(position));
                    if ((housemateAPI.isAdmin() && !housemateAPI.getFamilyOwnerId().equals(familyMember.getUserId()))
                        && !housemateAPI.getUserId().equals(familyMember.getUserId())) {
                        housemateAPI.setSelectedMember(familyMember);
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        FamilyBottomSheetFragment bottomSheet = new FamilyBottomSheetFragment();
                        bottomSheet.show(activity.getSupportFragmentManager(), "ExampleBottomSheet");
                    }
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* bind data to our items */
        /* position = where are we on our list? */

        FamilyMember familyMember = new FamilyMember(familyMembersList.get(position));
        holder.name.setText(familyMember.getName());
    }

    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(familyMembersList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.familyMemberItemTextView);


        }
    }
}
