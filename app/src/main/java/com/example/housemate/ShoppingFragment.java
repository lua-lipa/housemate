package com.example.housemate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingFragment extends Fragment {

    BottomSheetFragment bottomSheetFragment;
    FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ShoppingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Get Items from DB
        // Display them within the Recycler view in ShoppingFragment

        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        fab = view.findViewById(R.id.addItemFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getChildFragmentManager(), "ExampleBottomSheet");

            }
        });

        mAuth = FirebaseAuth.getInstance();
        //getting from db
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
                        CollectionReference shoppingListRef = familyRef.collection("shoppingList");
                        //date

                        shoppingListRef.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            String itemName;
                                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                itemName = (String) document.get("item");
                                                Log.d("Item Name", itemName);
                                            }
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

        //Toast -> getActivity gets current activity
        //view.findbyid to get all of the stuff from xml
        return view;
    }
}