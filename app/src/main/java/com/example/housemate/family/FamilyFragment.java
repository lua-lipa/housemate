package com.example.housemate.family;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Bills.Bill;
import com.example.housemate.FamilyActivity;
import com.example.housemate.LoginActivity;
import com.example.housemate.MainActivity;
import com.example.housemate.R;
import com.example.housemate.ShoppingList.BottomSheetFragment;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HousemateAPI housemateAPI = HousemateAPI.getInstance();

    Button inviteCodeButton;

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

        inviteCodeButton = view.findViewById(R.id.familyMembersInviteCodeButton);
        inviteCodeButton.setText(housemateAPI.getFamilyId());

        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.familyMembersRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); /* activity meant to be .this */

        inviteCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Invite Code", inviteCodeButton.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

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