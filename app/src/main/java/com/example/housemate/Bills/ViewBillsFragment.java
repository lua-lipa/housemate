
package com.example.housemate.Bills;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.R;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.example.housemate.adapter.BillsActivityRecyclerViewAdapter;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewBillsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /* */
    private RecyclerView myBillsRecyclerView;
    private BillRecyclerViewAdapter myBillsRecyclerViewAdapter;
    private RecyclerView billsActivityRecyclerView;
    private BillsActivityRecyclerViewAdapter billsActivityRecyclerViewAdapter;
    private List<Bill> billsList;
    private List<BillActivity> billsActivityList;
    private FloatingActionButton addBillButton;
    private FloatingActionButton billsMoreInfoButton;
    private Chip myBillsChip;
    private Chip activityChip;

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
        View v = inflater.inflate(R.layout.fragment_view_bills, container, false);

        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();

        billsMoreInfoButton = v.findViewById(R.id.billsMoreInfoFAB);
        billsMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillsMoreInfoFragment moreInfoFragment = new BillsMoreInfoFragment();
                moreInfoFragment.show(getChildFragmentManager(), "billsMoreInfoBottomSheet");
            }
        });

        addBillButton = v.findViewById(R.id.billsAddBillFAB);
        addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBillFragment addBillFragment = new AddBillFragment();
                addBillFragment.show(getChildFragmentManager(), "AddBillBottomSheet");
            }
        });

        billsList = new ArrayList<>();
        billsActivityList = new ArrayList<>();

        /* set up my bills recycler view */
        myBillsRecyclerView = v.findViewById(R.id.bills_recycler_view);
        myBillsRecyclerView.setHasFixedSize(true);
        myBillsRecyclerView.setLayoutManager(new LinearLayoutManager(activity)); /* activity meant to be .this */
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(myBillsRecyclerView);

        /* set up  bills  activity recycler view */
        billsActivityRecyclerView = v.findViewById(R.id.bills_activity_recycler_view);
        billsActivityRecyclerView.setHasFixedSize(true);
        billsActivityRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        billsActivityRecyclerView.setVisibility(View.INVISIBLE);

        myBillsChip = v.findViewById(R.id.bills_mybills_chip);
        activityChip = v.findViewById(R.id.bills_activity_chip);

        myBillsChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBillsRecyclerView.setVisibility(View.VISIBLE);
                billsActivityRecyclerView.setVisibility(View.INVISIBLE);
                Log.d("bills", "mybills");
            }
        });

        activityChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bills", "act");
                myBillsRecyclerView.setVisibility(View.INVISIBLE);
                billsActivityRecyclerView.setVisibility(View.VISIBLE);
            }
        });




        return v;

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


            HousemateAPI api = HousemateAPI.getInstance();

            DocumentReference familyRef = db.collection("families").document(api.getFamilyId());
            DocumentReference billRef = familyRef.collection("bills").document(billSwiped.getBillsId());
            billRef.update("isPaid", true);

            billsList.remove(viewHolder.getAdapterPosition());
            myBillsRecyclerViewAdapter.notifyDataSetChanged();

            /* adding the bill paid activity into the bill activity database */
            String billActivityId = familyRef.collection("billsActivity").document().getId();
            DocumentReference billsActivityRef = familyRef.collection("billsActivity").document(billActivityId);
            Map<String, Object> billsActivityObj = new HashMap<>();
            String assignerUserName = api.getUserName().substring(0, api.getUserName().indexOf(" "));
            Date currentTime = Calendar.getInstance().getTime();
            String message = assignerUserName + " paid the " + billSwiped.getTitle() + " bill.";
            billsActivityObj.put("billActivityId", billActivityId);
            billsActivityObj.put("message", message) ;
            billsActivityObj.put("date", currentTime.toString());

            billsActivityRef.set(billsActivityObj)
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
        }


    };

    @Override
    public void onStart() {

        super.onStart();
        HousemateAPI api = HousemateAPI.getInstance();
        String userId = mAuth.getUid();
        DocumentReference userRef = db.collection("users").document(userId);


        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        CollectionReference billsActivityRef = familyRef.collection("billsActivity");
        billsActivityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("view bills activity", "Listen failed.", error);
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    billsActivityList.clear();
                    for (QueryDocumentSnapshot billsActivity : queryDocumentSnapshots) {
                        /* making only the bills belonging to current user are being viewed */

                        BillActivity billActivity = billsActivity.toObject(BillActivity.class);
                        String billActivityId = billActivity.getBillActivityId();
                        billsActivityList.add(billActivity);
                    }
                    /* invoke recycler view*/

                    billsActivityRecyclerViewAdapter = new BillsActivityRecyclerViewAdapter(billsActivityList, getActivity());
                    billsActivityRecyclerView.setAdapter(billsActivityRecyclerViewAdapter);
                    billsActivityRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    /* display "no bills" text view */
                }
            }
        });



        CollectionReference billsRef = familyRef.collection("bills");

        billsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("view bills", "Listen failed.", error);
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    billsList.clear();
                    for (QueryDocumentSnapshot bills : queryDocumentSnapshots) {
                        /* making only the bills belonging to current user are being viewed */
                        Bill bill = bills.toObject(Bill.class);
                        String billId = bill.getBillsId();
                        String billUserId = bill.getUserId();
                        if (billUserId.equals(api.getUserId()) && bill.getIsPaid() == false)
                            billsList.add(bill);
                    }
                    /* invoke recycler view*/
                    billsList = sortByDate(billsList);
                    myBillsRecyclerViewAdapter = new BillRecyclerViewAdapter(billsList, getActivity());
                    myBillsRecyclerView.setAdapter(myBillsRecyclerViewAdapter);
                    myBillsRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    /* display "no bills" text view */
                }
            }
        });
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