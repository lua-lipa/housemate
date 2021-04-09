package com.example.housemate.Chores;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.example.housemate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddChoreBottomFragment extends BottomSheetDialogFragment {
    EditText enterChore;
    EditText enterAssignee;
    ImageButton calendarButton;
    ImageButton saveChoreButton;
    Group daysGroup;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AddChoreBottomFragment(){}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_add_chore, container, false);

        daysGroup = v.findViewById(R.id.choresDaysGroup);
        calendarButton = v.findViewById(R.id.choresCalendarButton);
        saveChoreButton = v.findViewById(R.id.choresSaveChoreButton);
        enterChore = v.findViewById(R.id.enter_chore);
        enterAssignee = v.findViewById(R.id.enter_assignee);

        Chip monday = v.findViewById(R.id.choresMonday);
        Chip tuesday = v.findViewById(R.id.choresTuesday);
        Chip wednesday = v.findViewById(R.id.choresWednesday);
        Chip thursday = v.findViewById(R.id.choresThursday);
        Chip friday = v.findViewById(R.id.choresFriday);
        Chip saturday = v.findViewById(R.id.choresSaturday);
        Chip sunday = v.findViewById(R.id.choresSunday);

        mAuth = FirebaseAuth.getInstance();

        return v;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        saveChoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choreName = enterChore.getText().toString().trim();
                String assignee = enterAssignee.getText().toString().trim();
                if(!TextUtils.isEmpty(choreName) && !TextUtils.isEmpty(assignee)){
                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String familyId = documentSnapshot.getString("familyId");
                                    DocumentReference familyRef = db.collection("families").document(familyId);
                                    String choresId = familyRef.collection("chores").document().getId();
                                    DocumentReference choresRef = familyRef.collection("chores").document(choresId);

                                    Map<String, Object> choresObj = new HashMap();
                                    choresObj.put("choresId", choresId);
                                    choresObj.put("name", choreName);
                                    choresObj.put("assignee", assignee);
                                    choresObj.put("day", "Monday");
                                    choresObj.put("isDone", false);

                                    choresRef.set(choresObj)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(), "Great success", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
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

                } else {

                }
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daysGroup.setVisibility(daysGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }
}
