package com.example.housemate.Chores;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.Chores.Chore;
import com.example.housemate.R;
import com.example.housemate.adapter.ChoreRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

public class ViewChoresFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /* */
    private RecyclerView recyclerView;
    private ChoreRecyclerViewAdapter choreRecyclerViewAdapter;
    private List<Chore> choresList;
    private ViewModelProvider choresViewModel;
    private CollectionReference collectionReference = db.collection("familyId");
    private FirebaseUser user;
    private Button selectedButton;
    private Button myChoresButton;
    private Button houseChoresButton;
    FloatingActionButton addChore;

    //private TextView textView;
    //private TextView noChoreText; /* displaying when no chores have been created */



    public ViewChoresFragment() {
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
        View v =  inflater.inflate(R.layout.fragment_chores, container, false);

        addChore = v.findViewById(R.id.choresFloatingActionButton);
        addChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddChoreBottomFragment bottomSheet = new AddChoreBottomFragment();
                bottomSheet.show(getChildFragmentManager(), "ExampleBottomSheet");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();

        user = mAuth.getCurrentUser();


        choresList = new ArrayList<>();
        //noChoreText = v.findViewById(R.id.no_chore_text);


        /* set up the recycler view */
        recyclerView = v.findViewById(R.id.ChoresRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity)); /* activity meant to be .this */

        myChoresButton = v.findViewById(R.id.choresMyChoresButton);
        houseChoresButton = v.findViewById(R.id.choresHouseChoresButton);
        selectedButton = myChoresButton;
        selectedButton.setBackgroundResource(R.drawable.chores_button_orange);
        //textView = v.findViewById(R.id.choresTextView);

        myChoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = myChoresButton;
                //textView.setText("my chores");
            }
        });

        houseChoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = houseChoresButton;
                //textView.setText("house chores");
            }
        });

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
                        String familyId = documentSnapshot.getString("familyId");
                        DocumentReference familyRef = db.collection("families").document(familyId);
                        CollectionReference choresRef = familyRef.collection("chores");
                        choresRef.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(!queryDocumentSnapshots.isEmpty()) {
                                            //noChoreText.setText("not empty");
                                            for(QueryDocumentSnapshot chores: queryDocumentSnapshots) {
                                                Chore chore = chores.toObject(Chore.class);
                                                //Log.d("Chore", chore.getName());
                                                choresList.add(chore);
                                            }
                                            /* invoke recycler view*/

                                            choreRecyclerViewAdapter = new ChoreRecyclerViewAdapter(choresList, getActivity());
                                            recyclerView.setAdapter(choreRecyclerViewAdapter);
                                            choreRecyclerViewAdapter.notifyDataSetChanged();

                                        } else {
                                            //noChoreText.setText("No Chores, enjoy a break :)"); /* display no data text view */
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