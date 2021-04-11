package com.example.housemate.ShoppingList;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

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

    private FloatingActionButton fab;
    private FloatingActionButton fabMoreInfo;
    private FloatingActionButton fabDelete;
    private Chip listChip;
    private Chip activityChip;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HousemateAPI housemateAPI = HousemateAPI.getInstance();

    private RecyclerView recyclerView;
    private List<ShoppingItem> shoppingList;

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

        fabDelete = view.findViewById(R.id.deleteItemFAB);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShoppingItem> newShoppingList = housemateAPI.getCheckedShoppingList();
                List<ShoppingItem> itemsToDelete = housemateAPI.getShoppingListItemsToDelete();
                shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(newShoppingList, getActivity());
                recyclerView.setAdapter(shoppingRecyclerViewAdapter);
                shoppingRecyclerViewAdapter.notifyDataSetChanged();

                DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
                CollectionReference shoppingListRef = familyRef.collection("shoppingList");

                WriteBatch batch = db.batch();

                for(int i = 0; i < itemsToDelete.size(); i++) {
                    DocumentReference shoppingItemRef = shoppingListRef.document(itemsToDelete.get(i).getShoppingListId());
                    batch.delete(shoppingItemRef);
                }

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

        fabMoreInfo = view.findViewById(R.id.moreInfoFAB);
        fabMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingMoreInfoFragment shoppingMoreInfoFragment = new ShoppingMoreInfoFragment();
                shoppingMoreInfoFragment.show(getChildFragmentManager(), "shoppingMoreInfoFragment");
            }
        });

        View activityRecycler;
        View shoppingRecycler;
        activityRecycler = view.findViewById(R.id.activity_recycler_view);
        shoppingRecycler = view.findViewById(R.id.shopping_recycler_view);

        listChip = view.findViewById(R.id.shopping_list_chip);
        listChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set acitivty list invisible
                shoppingRecycler.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
                fabMoreInfo.setVisibility(View.VISIBLE);
                showShoppingList();
            }
        });

        activityChip = view.findViewById(R.id.shopping_activity_chip);
        activityChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingRecycler.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
                fabMoreInfo.setVisibility(View.GONE);
                showActivityList();
            }
        });

        return view;
    }

    public void onStart() {
        super.onStart();

    }

    public void showShoppingList(){
        //get family id
        //create sub collection in that family doc using the id
        //after collection, add data
        //save data with this button

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
                    shoppingList.clear();
                    for (QueryDocumentSnapshot shoppingItems : queryDocumentSnapshots) {
                        ShoppingItem shoppingItem = shoppingItems.toObject(ShoppingItem.class);
                        shoppingList.add(shoppingItem);
                    }
                    sortItems();
                    /* invoke recycler view*/
                    shoppingRecyclerViewAdapter = new ShoppingRecyclerViewAdapter(shoppingList, getActivity());
                    recyclerView.setAdapter(shoppingRecyclerViewAdapter);
                    shoppingRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void showActivityList(){

    }

    public void sortItems(){
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Collections.sort(shoppingList, new Comparator<ShoppingItem>() {
            @Override
            public int compare(ShoppingItem o1, ShoppingItem o2) {
                    return o1.getItem().compareTo(o2.getItem());
            }
        });
    }
}