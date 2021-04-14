package com.example.housemate.ShoppingList;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.housemate.Chores.Chore;
import com.example.housemate.R;
import com.example.housemate.adapter.ShoppingRecyclerViewAdapter;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShoppingFragment extends Fragment {

    //items from shopping fragment xml file
    private FloatingActionButton fab;
    private FloatingActionButton fabMoreInfo;
    private FloatingActionButton fabDelete;
    private Chip listChip;
    private Chip activityChip;

    //firebase initialisation
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HousemateAPI housemateAPI = HousemateAPI.getInstance();

    //recycler view variables
    private RecyclerView recyclerView;
    private List<ShoppingItem> shoppingList;
    private ShoppingRecyclerViewAdapter shoppingRecyclerViewAdapter;

    //setting activityList list - global since it is used in the activity chip and in the swipe to delete method
    List<ShoppingItem> activityList = new ArrayList<>();

    public ShoppingFragment() {
        //required constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        //activity so we dont have to keep calling getActivity()
        Activity activity = getActivity();
        //firebase entry point
        mAuth = FirebaseAuth.getInstance();
        //getting our current user from the database
        currentUser = mAuth.getCurrentUser();
        //initializing our shopping arraylist
        shoppingList = new ArrayList<>();

        //setting up the recycler view
        recyclerView = view.findViewById(R.id.shopping_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity)); /* activity meant to be .this */

        //this is to check if the item is being swiped and it will swipe left
        new ItemTouchHelper(itemTouchHelperCallbackLeft).attachToRecyclerView(recyclerView);

        //FAB for opening the fragment to add items to the shopping list
        fab = view.findViewById(R.id.addItemFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create bottom sheet fragment object and open the fragment once we click the + fab
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getChildFragmentManager(), "ExampleBottomSheet");
            }
        });

        //delete item fab
        fabDelete = view.findViewById(R.id.deleteItemFAB);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //newShoppingList list will allow us to get all of the items from the db that are in the shopping list via the housemate api
                List<ShoppingItem> newShoppingList = housemateAPI.getCheckedShoppingList();
                //itemsToDelete list will allow us to get all of the items from the list that have been selected in the shopping list
                List<ShoppingItem> itemsToDelete = housemateAPI.getShoppingListItemsToDelete();

                //setting up our recycler view
                shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(newShoppingList, getActivity());
                recyclerView.setAdapter(shoppingRecyclerViewAdapter);
                shoppingRecyclerViewAdapter.notifyDataSetChanged();

                //referencing the database
                DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
                CollectionReference shoppingListRef = familyRef.collection("shoppingList");

                //allows us to delete document references from DB
                WriteBatch batch = db.batch();

                //going through items that we have selected
                for (int i = 0; i < itemsToDelete.size(); i++) {
                    //setting the selected items to bought in the DB
                    DocumentReference shoppingItemRef = shoppingListRef.document(itemsToDelete.get(i).getShoppingListId());
                    batch.update(shoppingItemRef, "isBought", true);
                }

                //committing the batch changes
                batch.commit()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        //FAB for More info
        fabMoreInfo = view.findViewById(R.id.moreInfoFAB);
        fabMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This allows us to show the More Info fragment once we click the More Info FAB
                ShoppingMoreInfoFragment shoppingMoreInfoFragment = new ShoppingMoreInfoFragment();
                shoppingMoreInfoFragment.show(getChildFragmentManager(), "shoppingMoreInfoFragment");
            }
        });

        //Shopping List Chip where all of our items will be placed
        listChip = view.findViewById(R.id.shopping_list_chip);
        listChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the buttons to visible so we can edit our shopping list
                fab.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
                fabMoreInfo.setVisibility(View.VISIBLE);
                onStart();
            }
        });

        //Activity List Chip where our items will be sent with extra details on who bought them and when
        activityChip = view.findViewById(R.id.shopping_activity_chip);
        activityChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We want to set all of the buttons to invisible so we can simply swipe away the items we no longer need
                fab.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
                fabMoreInfo.setVisibility(View.GONE);
                HousemateAPI api = HousemateAPI.getInstance();
                String familyId = api.getFamilyId();
                DocumentReference familyRef = db.collection("families").document(familyId);
                CollectionReference shoppingListRef = familyRef.collection("shoppingList");

                shoppingListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("view shopping list", "Listen failed.", error);
                            return;
                        }
                        //Document Listener - when we add something to activity list it updates right away
                        if (!queryDocumentSnapshots.isEmpty()) {
                            activityList.clear();
                            //Populating the Activity List with items from the database that have isBought set to TRUE
                            for (QueryDocumentSnapshot shoppingItems : queryDocumentSnapshots) {
                                ShoppingItem shoppingItem = shoppingItems.toObject(ShoppingItem.class);
                                if(shoppingItem.getIsBought()){
                                    activityList.add(shoppingItem);
                                }
                            }
                            //sorting items alphabetically in the shopping list
                            sortItems();
                            //invoke the recycler view
                            shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(activityList, getActivity());
                            recyclerView.setAdapter(shoppingRecyclerViewAdapter);
                            shoppingRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        return view;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackLeft = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //remove the card object from the view
            ShoppingItem itemSwiped = activityList.get(viewHolder.getAdapterPosition());
            //update the status of this bill being paid inside firestore
            HousemateAPI api = HousemateAPI.getInstance();
            DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
            CollectionReference shoppingListRef = familyRef.collection("shoppingList");

            WriteBatch batch = db.batch();

            //checking if the item we swiped and checking if "isBought" is true to ensure that we are deleting from the activity list only
            if(itemSwiped.getIsBought()) {
                //this will delete it
                activityList.remove(viewHolder.getAdapterPosition());
                //this will delete it from the database
                for (int i = 0; i < activityList.size(); i++) {
                    DocumentReference shoppingItemRef = shoppingListRef.document(activityList.get(i).getShoppingListId());
                    batch.delete(shoppingItemRef);
                }
            }

            //committing the batch
            batch.commit()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });

            shoppingRecyclerViewAdapter.notifyDataSetChanged();
        }

    };

    public void onStart() {
        super.onStart();
        HousemateAPI api = HousemateAPI.getInstance();
        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        CollectionReference shoppingListRef = familyRef.collection("shoppingList");

        shoppingListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("view shopping list", "Listen failed.", error);
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    //must clear each time to prevent duplication
                    shoppingList.clear();
                    for (QueryDocumentSnapshot shoppingItems : queryDocumentSnapshots) {
                        ShoppingItem shoppingItem = shoppingItems.toObject(ShoppingItem.class);
                        if(!shoppingItem.getIsBought()) {
                            shoppingList.add(shoppingItem);
                        }
                    }
                    sortItems();

                    //invoke recycler view
                    shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(shoppingList, getActivity());
                    recyclerView.setAdapter(shoppingRecyclerViewAdapter);
                    shoppingRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void sortItems(){
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Collections.sort(shoppingList, new Comparator<ShoppingItem>() {
            @Override
            public int compare(ShoppingItem o1, ShoppingItem o2) {
                    return o1.getDate().compareTo(o2.getDate());
            }
        });
    }
}