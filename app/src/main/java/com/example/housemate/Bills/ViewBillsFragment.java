package com.example.housemate.Bills;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Bills.Bill;
import com.example.housemate.R;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class  ViewBillsFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /* */
    private RecyclerView recyclerView;
    private BillRecyclerViewAdapter billRecyclerViewAdapter;
    private List<Bill> billsList;
    private ViewModelProvider billsViewModel;
    private CollectionReference collectionReference = db.collection("familyId");
    private FirebaseUser user;
    private TextView noBillText; /* displaying when no bills have been created */



    public ViewBillsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View v =  inflater.inflate(R.layout.fragment_view_bills, container, false);

        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();

        user = mAuth.getCurrentUser();


        billsList = new ArrayList<>();
        noBillText = v.findViewById(R.id.no_bill_text);


        /* set up the recycler view */
        recyclerView = v.findViewById(R.id.bills_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity)); /* activity meant to be .this */

//        billsViewModel = new ViewModelProvider.AndroidViewModelFactory(activity.getApplication()).create(BillsViewModel.class);
//        billsViewModel.getAllBills().observe(this, bills -> {
//            /* set up recycler view */
//           recyclerViewAdapter = new RecyclerViewAdapter(billsList, activity);
//           recyclerView.setAdapter(recyclerViewAdapter);
//
//        });

        return v;

    }

    @Override
    public void onStart() {

        super.onStart();

        String userId = mAuth.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //get family id
                        //create sub collection in that family doc using the id
                        //after collection, add data
                        //save data with this button
                        String familyId = documentSnapshot.getString("family");
                        DocumentReference familyRef = db.collection("families").document(familyId);
                        CollectionReference billsRef = familyRef.collection("bills");
                        //date
                        billsRef.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(!queryDocumentSnapshots.isEmpty()) {
                                            noBillText.setText("not empty");
                                            for(QueryDocumentSnapshot bills: queryDocumentSnapshots) {
                                                Bill bill = bills.toObject(Bill.class);
                                                billsList.add(bill);
                                            }
                                            /* invoke recycler view*/

                                            billRecyclerViewAdapter = new BillRecyclerViewAdapter(billsList, getActivity());
                                            recyclerView.setAdapter(billRecyclerViewAdapter);
                                            billRecyclerViewAdapter.notifyDataSetChanged();

                                        } else {
                                            noBillText.setText("empty"); /* display no data text view */
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        /*
        collectionReference.whereEqualTo("familyId", "KPrCIWOp1KpHDQLW5erT")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        if(!queryDocumentSnapshots.isEmpty()) {
                            noBillText.setText("not empty");
                            for(QueryDocumentSnapshot bills: queryDocumentSnapshots) {
                                BillsModel bill = bills.toObject(BillsModel.class);
                                billsList.add(bill);
                            }

                            recyclerViewAdapter = new RecyclerViewAdapter(billsList, getActivity());
                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerViewAdapter.notifyDataSetChanged();

                        } else {
                            noBillText.setText("empty");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                        noBillText.setText("Can't get bills");
                    }
            });

        */

    }
}