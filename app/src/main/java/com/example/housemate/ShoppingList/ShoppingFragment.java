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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingFragment extends Fragment {

    //items from shopping fragment xml file
    private FloatingActionButton fab;
    private FloatingActionButton fabMoreInfo;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabFinalDelete;
    private Chip listChip;
    private Chip activityChip;
    private CheckBox checkBox;

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
                if(itemsToDelete!=null) {
                    for (int i = 0; i < itemsToDelete.size(); i++) {
                        //setting the selected items to bought in the DB
                        DocumentReference shoppingItemRef = shoppingListRef.document(itemsToDelete.get(i).getShoppingListId());
                        batch.update(shoppingItemRef, "isBought", true);
                    }

                    addItemBoughtActivity(itemsToDelete);

                    //committing the batch changes
                    batch.commit()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Added to Activity List!", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Nothing Selected!", Toast.LENGTH_LONG).show();
                }

            }
        });

        //FAB to delete from activity list
        fabFinalDelete = view.findViewById(R.id.finalDeleteItemFAB);
        fabFinalDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete selected items from activity list
                List<ShoppingItem> finalShoppingListItemsToDelete = housemateAPI.getShoppingListItemsToDelete();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
                CollectionReference shoppingListRef = familyRef.collection("shoppingList");
                WriteBatch batch = db.batch();
                for (int i = 0; i < finalShoppingListItemsToDelete.size(); i++) {
                    DocumentReference shoppingItemRef = shoppingListRef.document(finalShoppingListItemsToDelete.get(i).getShoppingListId());
                    batch.delete(shoppingItemRef);
                }

                batch.commit()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Item Deleted!", Toast.LENGTH_LONG).show();
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
                fabFinalDelete.setVisibility(View.GONE);
                fabMoreInfo.setVisibility(View.VISIBLE);
                onStart();
            }
        });

        //Activity List Chip where our items will be sent with extra details on who bought them and when
        activityChip = view.findViewById(R.id.shopping_activity_chip);
        activityChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We want to set all of the buttons to invisible that we no longer need
                fab.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
                fabFinalDelete.setVisibility(View.VISIBLE);
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

    private void addItemBoughtActivity(List<ShoppingItem> items) {
        String item_name = "";
        if (items.size() == 1) {
            item_name = items.get(0).getItem();
        } else {
            item_name = items.size() + " items";
        }
        HousemateAPI api = HousemateAPI.getInstance();
        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        String billActivityId = familyRef.collection("houseActivity").document().getId();
        DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(billActivityId);
        String buyer = api.getUserName().substring(0, api.getUserName().indexOf(" "));

        Map<String, Object> houseActivityObj = new HashMap<>();
        String message = buyer + " bought " + item_name + " from the shopping list.";
        /* the date gets formatted to display correctly in the activity view */
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date();
        String cur_time = formatter.format(d);
        Date currentTime = Calendar.getInstance().getTime();
        houseActivityObj.put("billActivityId", billActivityId);
        houseActivityObj.put("message", message);
        houseActivityObj.put("date", cur_time);
        houseActivityObj.put("type", "shopping");
        houseActivityRef.set(houseActivityObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Added to Activity Home!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}