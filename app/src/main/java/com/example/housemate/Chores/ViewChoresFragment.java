package com.example.housemate.Chores;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.R;
import com.example.housemate.ShoppingList.ShoppingItem;
import com.example.housemate.adapter.ChoresRecyclerViewAdapter;
import com.example.housemate.util.HousemateAPI;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewChoresFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /* */
    private RecyclerView recyclerView;
    private ChoresRecyclerViewAdapter choresRecyclerViewAdapter;
    private List<Chore> choresList;
    private ViewModelProvider choresViewModel;
    private CollectionReference collectionReference = db.collection("familyId");
    private FirebaseUser user;
    FloatingActionButton addChore;
    private Chip myChores;
    private Chip houseChores;
    private Chip completedChores;
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
        new ItemTouchHelper(itemTouchHelperCallbackLeft).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(itemTouchHelperCallbackRight).attachToRecyclerView(recyclerView);

        myChores = v.findViewById(R.id.ChoresMyChoresChip);
        houseChores = v.findViewById(R.id.ChoresHouseChoresChip);
        completedChores = v.findViewById(R.id.ChoresCompletedChoresChip);

        myChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getUid();
                HousemateAPI api = HousemateAPI.getInstance();
                String familyId = api.getFamilyId();
                String userName = api.getUserName();
                DocumentReference familyRef = db.collection("families").document(familyId);
                CollectionReference choresRef = familyRef.collection("chores");
                choresRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("view chores", "Listen failed.", error);
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            //noChoreText.setText("not empty");
                            choresList.clear();
                            for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {

                                Chore chore = chores.toObject(Chore.class);
                                //Log.d("Chore", chore.getName());
                                if(chore.getIsDone() == false && chore.getAssignee().equals(userName)){
                                    chore.setAssignee(" ");
                                    choresList.add(chore);
                                }
                            }
                            sortItems();
//                    Log.d("LOL", choresList.toString());
                            /* invoke recycler view*/
                            choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                            recyclerView.setAdapter(choresRecyclerViewAdapter);
                            choresRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            //noChoreText.setText("No Chores, enjoy a break :)"); /* display no data text view */
                        }
                    }
                });
            }
        });

        houseChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getUid();
                HousemateAPI api = HousemateAPI.getInstance();
                String familyId = api.getFamilyId();
                DocumentReference familyRef = db.collection("families").document(familyId);
                CollectionReference choresRef = familyRef.collection("chores");
                choresRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("view chores", "Listen failed.", error);
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            //noChoreText.setText("not empty");
                            choresList.clear();
                            for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {

                                Chore chore = chores.toObject(Chore.class);
                                //Log.d("Chore", chore.getName());
                                if(chore.getIsDone() == false) choresList.add(chore);
                            }
                            sortItems();
//                    Log.d("LOL", choresList.toString());
                            /* invoke recycler view*/
                            choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                            recyclerView.setAdapter(choresRecyclerViewAdapter);
                            choresRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            //noChoreText.setText("No Chores, enjoy a break :)"); /* display no data text view */
                        }
                    }
                });
            }
        });

        completedChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getUid();
                HousemateAPI api = HousemateAPI.getInstance();
                String familyId = api.getFamilyId();
                DocumentReference familyRef = db.collection("families").document(familyId);
                CollectionReference choresRef = familyRef.collection("chores");
                choresRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("view chores", "Listen failed.", error);
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            //noChoreText.setText("not empty");
                            choresList.clear();
                            for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {

                                Chore chore = chores.toObject(Chore.class);
                                //Log.d("Chore", chore.getName());
                                if(chore.getIsDone() == true) choresList.add(chore);
                            }
                            sortItems();
//                    Log.d("LOL", choresList.toString());
                            /* invoke recycler view*/
                            choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                            recyclerView.setAdapter(choresRecyclerViewAdapter);
                            choresRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            //noChoreText.setText("No Chores, enjoy a break :)"); /* display no data text view */
                        }
                    }
                });
            }
        });

        return v;

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackLeft = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            /* remove the card object from the view */
            Chore choreSwiped = choresList.get(viewHolder.getAdapterPosition());
            /* update the status of this bill being paid inside firestore */
            HousemateAPI api = HousemateAPI.getInstance();
            String userId = mAuth.getUid();
            DocumentReference familyRef = db.collection("families").document(api.getFamilyId());
            DocumentReference choresRef = familyRef.collection("chores").document(choreSwiped.getChoresId());
            if(choreSwiped.getAssignee().equals(userId)) {
                choresRef.update("isDone", true);
                choresList.remove(viewHolder.getAdapterPosition());
            }
            choresRecyclerViewAdapter.notifyDataSetChanged();
        }

    };

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            /* remove the card object from the view */
            Chore choreSwiped = choresList.get(viewHolder.getAdapterPosition());
            /* update the status of this bill being paid inside firestore */
            HousemateAPI api = HousemateAPI.getInstance();
            String userId = mAuth.getUid();
            DocumentReference familyRef = db.collection("families").document(api.getFamilyId());
            DocumentReference choresRef = familyRef.collection("chores").document(choreSwiped.getChoresId());
            if(choreSwiped.getCreator().equals(userId)) {
                choresRef.delete();
                choresList.remove(viewHolder.getAdapterPosition());
            }
            choresRecyclerViewAdapter.notifyDataSetChanged();
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        String userId = mAuth.getUid();
        HousemateAPI api = HousemateAPI.getInstance();
        String familyId = api.getFamilyId();
        String userName = api.getUserName();
        DocumentReference familyRef = db.collection("families").document(familyId);
        CollectionReference choresRef = familyRef.collection("chores");
        choresRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("view chores", "Listen failed.", error);
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    //noChoreText.setText("not empty");
                    choresList.clear();
                    for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {

                        Chore chore = chores.toObject(Chore.class);
                        //Log.d("Chore", chore.getName());
                        Log.d("Chore", chore.getAssignee());
                        if(chore.getIsDone() == false && chore.getAssignee().equals(userName)){
                            chore.setAssignee(" ");
                            choresList.add(chore);
                        }
                    }
                    sortItems();
//                    Log.d("LOL", choresList.toString());
                    /* invoke recycler view*/
                    choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                    recyclerView.setAdapter(choresRecyclerViewAdapter);
                    choresRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    //noChoreText.setText("No Chores, enjoy a break :)"); /* display no data text view */
                }
            }
        });
    }

    public void sortItems(){
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Collections.sort(choresList, new Comparator<Chore>() {
            @Override
            public int compare(Chore c1, Chore c2) {
                return c1.getDate().compareTo(c2.getDate());
            }
        });
    }

}