
package com.example.housemate.Bills;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.housemate.R;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.example.housemate.util.HousemateAPI;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewBillsFragment extends Fragment {
    /* database globals */
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HousemateAPI api;
    /* layout element globals */
    private RecyclerView myBillsRecyclerView;
    private BillRecyclerViewAdapter myBillsRecyclerViewAdapter;
    private FloatingActionButton addBillButton;
    private FloatingActionButton billsMoreInfoButton;
    private Chip myBillsChip;
    private Chip paidBillsChip;
    private Chip houseBillsChip;
    private TextView nothingToDisplayLabel;
    /* class globals */
    private List<Bill> billsList;
    private boolean paidRequirement = false;
    private boolean userBillsOnly = true;

    public ViewBillsFragment() { // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* inflating the layout and setting up variables  */
        api = HousemateAPI.getInstance();
        View v = inflater.inflate(R.layout.fragment_view_bills, container, false);
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();
        billsList = new ArrayList<>();

        /* setting up layout elements */
        nothingToDisplayLabel = v.findViewById(R.id.bills_nothing_label);
        nothingToDisplayLabel.setVisibility(View.INVISIBLE);
        billsMoreInfoButton = v.findViewById(R.id.billsMoreInfoFAB);
        addBillButton = v.findViewById(R.id.billsAddBillFAB);
        myBillsChip = v.findViewById(R.id.bills_mybills_chip);
        paidBillsChip = v.findViewById(R.id.bills_paidbills_chip);
        houseBillsChip = v.findViewById(R.id.bills_housebills_chip);

        /* non-admins are not able to view house bills, making the chip non-interactive for them */
        if(!api.isAdmin()) houseBillsChip.setVisibility(View.INVISIBLE);


        /* setting up the bills recycler view, connecting it to the item touch helper for swipe to delete functionality */
        myBillsRecyclerView = v.findViewById(R.id.bills_recycler_view);
        myBillsRecyclerView.setHasFixedSize(true);
        myBillsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(myBillsRecyclerView);

        /* setting up the listeners for chips and buttons on the bottom right */

        setUpListeners();

        return v;

    }

    private void setUpListeners() {
        /* setting up listeners for chips & buttons on the bottom,
        clicks between chips change their visibility, FABs display their own fragments */
        billsMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillsMoreInfoFragment moreInfoFragment = new BillsMoreInfoFragment();
                moreInfoFragment.show(getChildFragmentManager(), "billsMoreInfoBottomSheet");
            }
        });
        addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBillFragment addBillFragment = new AddBillFragment();
                addBillFragment.show(getChildFragmentManager(), "AddBillBottomSheet");
            }
        });


        myBillsChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nothingToDisplayLabel.setVisibility(View.INVISIBLE);
                loadBills("unpaid");
                myBillsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        paidBillsChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nothingToDisplayLabel.setVisibility(View.INVISIBLE);
                loadBills("paid");
                myBillsRecyclerView.setVisibility(View.VISIBLE);

            }
        });

        if(api.isAdmin()) {
            houseBillsChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nothingToDisplayLabel.setVisibility(View.INVISIBLE);
                    loadBills("house");
                    myBillsRecyclerView.setVisibility(View.VISIBLE);

                }
            });
        }

    }


    /* loads bills into the recycler view */
    private void loadBills(String type) {
        /* type = {house || paid || unpaid}
        * "house" passed in sets up the recycler view to show all house bills
        *  "paid" passed in sets up the recycler view to show only paid bills
        *  "unpaid" passed in sets up the recycler view to show unpaid bills
        *  These lists get loaded into the billsRecyclerView. */

        paidRequirement = false;
        userBillsOnly = true;

        if (type.equals("paid")) paidRequirement = true;
        if (type.equals("house")) userBillsOnly = false;


        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        CollectionReference billsRef = familyRef.collection("bills");
        billsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("bills view", "error in load bills");
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    billsList.clear();
                    for (QueryDocumentSnapshot bills : queryDocumentSnapshots) {
                        /* getting correct bills according to the value passed in */
                        Bill bill = bills.toObject(Bill.class);
                        String billUserId = bill.getUserId();
                        if (userBillsOnly) {
                            if (billUserId.equals(api.getUserId()) && bill.getIsPaid() == paidRequirement) billsList.add(bill);
                        } else {
                            if (bill.getIsPaid() == paidRequirement) billsList.add(bill);
                        }
                    }
                    /* invoke recycler view */
                    billsList = sortByDate(billsList);
                    if(billsList.size() == 0) nothingToDisplayLabel.setVisibility(View.VISIBLE); /* when list is empty, we display "nothing to display" text view */

                    myBillsRecyclerViewAdapter = new BillRecyclerViewAdapter(billsList, getActivity());
                    myBillsRecyclerView.setAdapter(myBillsRecyclerViewAdapter);
                    myBillsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }




    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView myBillsRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            /* remove the card object from the view */

            Bill billSwiped = billsList.get(viewHolder.getAdapterPosition());

            /* update the status of this bill being paid inside firestore */

            DocumentReference familyRef = db.collection("families").document(api.getFamilyId());
            DocumentReference billRef = familyRef.collection("bills").document(billSwiped.getBillsId());

            if (billSwiped.getIsPaid() == false ) {
                /* updating the bill to paid */
                billRef.update("isPaid", true);
                billsList.remove(viewHolder.getAdapterPosition());
                myBillsRecyclerViewAdapter.notifyDataSetChanged();

                /* adding the bill paid activity into the bill activity database */

                String houseActivityId = familyRef.collection("houseActivity").document().getId();
                DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(houseActivityId);

                /* setting up the message & date which gets added to the database for the house activity database */
                String assigneeUserName = billSwiped.getAssignee().substring(0, billSwiped.getAssignee().indexOf(" "));
                String message = assigneeUserName + " paid the " + billSwiped.getTitle() + " bill.";
                SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date d = new Date();
                String cur_time = formatter.format(d);

                /* the date gets formatted to display correctly in the activity view */
                Map<String, Object> houseActivityObj = new HashMap<>();
                houseActivityObj.put("houseActivityId", houseActivityId);
                houseActivityObj.put("message", message) ;
                houseActivityObj.put("date", cur_time);
                houseActivityObj.put("type", "bill");

                houseActivityRef.set(houseActivityObj)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "bill activity success", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                /* if the bill is paid already, we are in "paid bills" view, therefore the bill gets deleted from the database entirely */
                billRef.delete();
                billsList.remove(viewHolder.getAdapterPosition());
                myBillsRecyclerViewAdapter.notifyDataSetChanged();
            }

        }


    };

    @Override
    public void onStart() {
        super.onStart();
        /* loading the bills recycler view */
        loadBills("paid");
        loadBills("house");
        loadBills("unpaid");
    }



    /* sort the bills list so it displays the earliest due date -> latest due date */
    private List<Bill> sortByDate(List<Bill> billsList) {
        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
        Collections.sort(billsList, new Comparator() {
            public int compare(Object o1, Object o2) {
                Bill bill1 = (Bill) o1;
                Bill bill2 = (Bill) o2;
                Date date1 = null, date2 = null;
                try {
                     date1=formatter1.parse(bill1.getDate());
                     date2=formatter1.parse(bill2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return date1.compareTo(date2);
            }
        });

        return billsList;
    }


}