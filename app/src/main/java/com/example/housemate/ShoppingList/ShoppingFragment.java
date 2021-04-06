package com.example.housemate.ShoppingList;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.housemate.Bills.Bill;
import com.example.housemate.R;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.example.housemate.adapter.ShoppingRecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {

    FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recyclerView;
    private List<ShoppingItem> shoppingList;
    private ViewModelProvider shoppingViewModel;
    private CollectionReference collectionReference = db.collection("familyId");

    private ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;

    public ShoppingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        Activity activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        shoppingList = new ArrayList<>();

        /* set up the recycler view */
        recyclerView = view.findViewById(R.id.shopping_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity)); /* activity meant to be .this */

        fab = view.findViewById(R.id.addItemFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getChildFragmentManager(), "ExampleBottomSheet");
            }
        });

        return view;
    }

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
                        String familyId = documentSnapshot.getString("familyId");
                        DocumentReference familyRef = db.collection("families").document(familyId);
                        CollectionReference shoppingListRef = familyRef.collection("shoppingList");
                        //date
                        shoppingListRef.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                        if (!queryDocumentSnapshots.isEmpty()) {
//                                            String itemName;
//                                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                                                itemName = (String) document.get("item");
//                                                Log.d("Item Name", itemName);
//                                            }
//                                        }

                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            for (QueryDocumentSnapshot shoppingItems : queryDocumentSnapshots) {
                                                /*Problem here*/
                                                ShoppingItem shoppingItem = shoppingItems.toObject(ShoppingItem.class);
                                                shoppingList.add(shoppingItem);

                                            }

                                            /* invoke recycler view*/
                                            shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(shoppingList, getActivity());
                                            recyclerView.setAdapter(shoppingRecyclerViewAdapter);
                                            shoppingRecyclerViewAdapter.notifyDataSetChanged();
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
    }
}