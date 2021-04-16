package com.example.housemate.Chores;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewChoresFragment extends Fragment {
    //declaring variables
    //connect to firestore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //recycler view for viewing the chores
    private RecyclerView recyclerView;
    private ChoresRecyclerViewAdapter choresRecyclerViewAdapter;
    private List<Chore> choresList;
    //get user family information
    private CollectionReference collectionReference = db.collection("familyId");
    private FirebaseUser user;
    //button for adding a chore
    FloatingActionButton addChore;
    //chips for changing between chores
    private Chip myChores;
    private Chip houseChores;
    private Chip completedChores;
    //booleans for enabling/disabling swipe on the different lists
    private boolean swipeLeft;
    private boolean swipeRight;
    //booleans for displaying the toasts
    private boolean completeFirst;
    private boolean mineFirst;

    // Required empty public constructor
    public ViewChoresFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_chores, container, false);

        //connect to the add chore button and set an on click listener to bring up the bottom sheet fragment
        addChore = v.findViewById(R.id.choresFloatingActionButton);
        addChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddChoreBottomFragment bottomSheet = new AddChoreBottomFragment();
                bottomSheet.show(getChildFragmentManager(), "ExampleBottomSheet");
            }
        });

        //connect to database to get information relevant to the user
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();
        user = mAuth.getCurrentUser();

        //initiate swiping left to true and swiping right to false as the user lands on my chores first
        swipeLeft = true;
        swipeRight = false;

        //initiate complete first and mine first to true as its the first time the user is looking at this pages
        completeFirst = true;
        mineFirst = true;

        //initiate the chore list as an array list
        choresList = new ArrayList<>();

        //set up the recycler view
        recyclerView = v.findViewById(R.id.ChoresRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity)); /* activity meant to be .this */

        //initiate the item touch helpers for swiping left and right
        new ItemTouchHelper(itemTouchHelperCallbackLeft).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(itemTouchHelperCallbackRight).attachToRecyclerView(recyclerView);

        //initiate the chips
        myChores = v.findViewById(R.id.ChoresMyChoresChip);
        houseChores = v.findViewById(R.id.ChoresHouseChoresChip);
        completedChores = v.findViewById(R.id.ChoresCompletedChoresChip);

        //on click listener for when "mine" is clicked
        myChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLeft = true; //true because user should be able to swipe left to complete
                swipeRight = false; //false because user shouldn't be able to swipe right to delete
                //get the housemate api to get the family id and users name
                HousemateAPI api = HousemateAPI.getInstance();
                String familyId = api.getFamilyId();
                String userName = api.getUserName();
                //get the chores document under the family with familyId's document
                DocumentReference familyRef = db.collection("families").document(familyId);
                CollectionReference choresRef = familyRef.collection("chores");
                //add snapshot listener to the chores collection to get real time database updates
                choresRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("view chores", "Listen failed.", error);
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) { //if there is chore documents in the chores document
                            choresList.clear(); //clear list to not duplicate chores
                            //begin adding the chores to the chores list
                            for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {
                                Chore chore = chores.toObject(Chore.class);
                                //only add the chore is it hasn't been done yet and its assigned to the user
                                if(chore.getIsDone() == false && chore.getAssignee().equals(userName)){
                                    chore.setAssignee(" "); //set assignee to blank so that it doesn't display their name
                                    choresList.add(chore);
                                }
                            }
                            sortItems(); //sort the list
                            //invoke the recycler view
                            choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                            recyclerView.setAdapter(choresRecyclerViewAdapter);
                            choresRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        //on click listener for when "house" is clicked
        houseChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set both swipe left and right to false as the user shouldn't able to swipe on house list
                swipeLeft = false;
                swipeRight = false;
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
                            choresList.clear();
                            for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {
                                Chore chore = chores.toObject(Chore.class);
                                //only add the chore if it hasn't been done yet
                                if(chore.getIsDone() == false) choresList.add(chore);
                            }
                            sortItems();
                            choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                            recyclerView.setAdapter(choresRecyclerViewAdapter);
                            choresRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        //on click listener for when "complete" is clicked
        completedChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLeft = false; //false because user shouldn't be able to swipe left
                swipeRight = true; //true because user should be able to swipe right
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
                            choresList.clear();
                            for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {
                                Chore chore = chores.toObject(Chore.class);
                                //only add chore to list if its already been done
                                if(chore.getIsDone() == true) choresList.add(chore);
                            }
                            sortItems();
                            choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                            recyclerView.setAdapter(choresRecyclerViewAdapter);
                            choresRecyclerViewAdapter.notifyDataSetChanged();
                            if(completeFirst) {
                                Toast.makeText(activity, "Swipe Right to Delete a Chore.", Toast.LENGTH_LONG).show();
                                completeFirst = false;
                            }
                        }
                    }
                });
            }
        });
        return v;
    }

    //item touch helper for when chore is swiped left
    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackLeft = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean isItemViewSwipeEnabled()
        {
            return swipeLeft;
        } //check whether we should allow the swipe or not

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //remove the card object from the view
            Activity activity = getActivity();
            Chore choreSwiped = choresList.get(viewHolder.getAdapterPosition());
            //update the status of this chore being done inside firestore
            HousemateAPI api = HousemateAPI.getInstance();
            String userId = mAuth.getUid();
            DocumentReference familyRef = db.collection("families").document(api.getFamilyId());
            DocumentReference choresRef = familyRef.collection("chores").document(choreSwiped.getChoresId());
            //only allow an admin or the user the chore is assigned to to set its as done
            choresRef.update("isDone", true);
            addChoreFinishedActivity(choreSwiped); //update house activity
            choresList.remove(viewHolder.getAdapterPosition());
            Toast.makeText(activity, "Chore completed.", Toast.LENGTH_LONG).show();
            choresRecyclerViewAdapter.notifyDataSetChanged();
        }

    };

    //item touch helper for when chore is swiped right
    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean isItemViewSwipeEnabled()
        {
            return swipeRight;
        } //check whether we should allow the swipe or not

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //remove the card object from the view
            Activity activity = getActivity();
            Chore choreSwiped = choresList.get(viewHolder.getAdapterPosition());
            //Remove this chore from firestore
            HousemateAPI api = HousemateAPI.getInstance();
            String userId = mAuth.getUid();
            DocumentReference familyRef = db.collection("families").document(api.getFamilyId());
            DocumentReference choresRef = familyRef.collection("chores").document(choreSwiped.getChoresId());
            //only allow the user that created the chore or an admin to delete it
            if(choreSwiped.getCreator().equals(userId) || api.isAdmin()) {
                choresRef.delete();
                choresList.remove(viewHolder.getAdapterPosition());
                Toast.makeText(activity, "Chore deleted.", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(activity, "Only the chore creator or an admin can delete a chore.", Toast.LENGTH_LONG).show();
            }
            choresRecyclerViewAdapter.notifyDataSetChanged();
        }

    };

    private void addChoreFinishedActivity(Chore chore) {
        //connect to firestore and the families house activity document
        HousemateAPI api = HousemateAPI.getInstance();
        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        String houseActivityId = familyRef.collection("houseActivity").document().getId();
        DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(houseActivityId);
        //get the name of the user that completed the chore, it will be the same as the user that it was assigned to
        String assignerUserName = api.getUserName().substring(0, api.getUserName().indexOf(" "));
        //create a house activity object to add to firestore
        Map<String, Object> houseActivityObj = new HashMap<>();
        //create the message to display in the recyclerView
        String message = assignerUserName + " finished the " + chore.getName() + " chore.";
        //the date gets formatted to display correctly in the activity view
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date();
        //get the current time to display when the chore was finished
        String cur_time = formatter.format(d);
        Date currentTime = Calendar.getInstance().getTime();
        houseActivityObj.put("houseActivityId", houseActivityId);
        houseActivityObj.put("message", message) ;
        houseActivityObj.put("date", cur_time);
        houseActivityObj.put("type", "chore");
        houseActivityRef.set(houseActivityObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "add chore activity success", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
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
                    choresList.clear();
                    for (QueryDocumentSnapshot chores : queryDocumentSnapshots) {
                        Chore chore = chores.toObject(Chore.class);
                        Log.d("Chore", chore.getAssignee());
                        if(chore.getIsDone() == false && chore.getAssignee().equals(userName)){
                            chore.setAssignee(" ");
                            choresList.add(chore);
                        }
                    }
                    sortItems();
                    choresRecyclerViewAdapter = new ChoresRecyclerViewAdapter(choresList, getActivity());
                    recyclerView.setAdapter(choresRecyclerViewAdapter);
                    choresRecyclerViewAdapter.notifyDataSetChanged();
                    if(mineFirst) {
                        Toast.makeText(activity, "Swipe Left to Complete a Chore.", Toast.LENGTH_LONG).show();
                        mineFirst = false;
                    }
                }
            }
        });
    }

    public void sortItems(){
        Collections.sort(choresList, new Comparator<Chore>() {
            @Override
            public int compare(Chore c1, Chore c2) {
                return c1.getDate().compareTo(c2.getDate()); //sort according to date
            }
        });
    }

}