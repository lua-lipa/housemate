package com.example.housemate;

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

import com.example.housemate.Bills.AddBillFragment;
import com.example.housemate.Bills.Bill;
import com.example.housemate.Bills.BillsMoreInfoFragment;
import com.example.housemate.adapter.BillRecyclerViewAdapter;
import com.example.housemate.adapter.HouseActivityRecyclerViewAdapter;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class HouseActivityFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView houseActivityRecyclerView;
    private HouseActivityRecyclerViewAdapter houseActivityRecyclerViewAdapter;
    private List<HouseActivity> houseActivityList;
    private TextView nothingToDisplayLabel;

    public HouseActivityFragment() {
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
        HousemateAPI api = HousemateAPI.getInstance();
        View v = inflater.inflate(R.layout.fragment_house_activity, container, false);

        Activity activity = getActivity();

        nothingToDisplayLabel = v.findViewById(R.id.house_activity_nothing_label);
        nothingToDisplayLabel.setVisibility(View.INVISIBLE);

        houseActivityList = new ArrayList<>();


        /* set up  bills  activity recycler view */
        houseActivityRecyclerView = v.findViewById(R.id.house_activity_recycler_view);
        houseActivityRecyclerView.setHasFixedSize(true);
        houseActivityRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        return v;

    }










    @Override
    public void onStart() {

        super.onStart();
        HousemateAPI api = HousemateAPI.getInstance();

//activity
        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        CollectionReference houseActivityRef = familyRef.collection("houseActivity");
        houseActivityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("view bills activity", "Listen failed.", error);
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    houseActivityList.clear();
                    for (QueryDocumentSnapshot houseActivity : queryDocumentSnapshots) {
                        /* making only the bills belonging to current user are being viewed */
                        HouseActivity house_activity = houseActivity.toObject(HouseActivity.class);
                        String houseActivityId = house_activity.getHouseActivityId();
                        houseActivityList.add(house_activity);
                    }
                    /* invoke recycler view*/
                    if(houseActivityList.size() == 0) nothingToDisplayLabel.setVisibility(View.VISIBLE);
                    houseActivityList = sortByTime(houseActivityList);
                    houseActivityRecyclerViewAdapter = new HouseActivityRecyclerViewAdapter(houseActivityList, getActivity());
                    houseActivityRecyclerView.setAdapter(houseActivityRecyclerViewAdapter);
                    houseActivityRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    /* display "no bills" text view */
                }
            }
        });


    }

    private List<HouseActivity> sortByTime(List<HouseActivity> activityList) {
        SimpleDateFormat formatter1= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Collections.sort(activityList, new Comparator() {
            public int compare(Object o1, Object o2) {
                HouseActivity bill1 = (HouseActivity) o1;
                HouseActivity bill2 = (HouseActivity) o2;
                Date date1 = null, date2 = null;
                try {
                    date1=formatter1.parse(bill1.getDate());
                    date2=formatter1.parse(bill2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return date2.compareTo(date1);
            }
        });

        return activityList;

    }


}
