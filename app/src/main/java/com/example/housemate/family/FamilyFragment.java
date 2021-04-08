package com.example.housemate.family;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Bills.Bill;
import com.example.housemate.R;
import com.example.housemate.ShoppingList.BottomSheetFragment;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.example.housemate.util.HousemateAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class FamilyFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /* */
    private RecyclerView recyclerView;
    private FamilyRecyclerViewAdapter familyRecyclerViewAdapter;
    private List<Map<String, Object>> familyMembersList;
    private ViewModelProvider familyViewModel;
    private CollectionReference collectionReference = db.collection("familyId");

    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family, container, false);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.familyMembersRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* activity meant to be .this */


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        HousemateAPI housemateAPI = HousemateAPI.getInstance();
        familyMembersList = housemateAPI.getMembersList();

        familyRecyclerViewAdapter = FamilyRecyclerViewAdapter.getInstance(familyMembersList, getActivity());
        recyclerView.setAdapter(familyRecyclerViewAdapter);
        familyRecyclerViewAdapter.notifyDataSetChanged();
    }
}