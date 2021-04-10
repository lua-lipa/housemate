package com.example.housemate.Chores;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.example.housemate.R;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddChoreBottomFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener{
    private EditText dateText;
    EditText nameEditText;
    Spinner assigneeSpinner;
    private int assigneeIndex;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AddChoreBottomFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_add_chore, container, false);

        dateText = (EditText) v.findViewById(R.id.choresChoreDueDateInput);
        nameEditText = (EditText) v.findViewById(R.id.choresChoreNameInput);
        assigneeSpinner = (Spinner) v.findViewById(R.id.choresChoreAssigneeInput);

        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();
        HousemateAPI api = HousemateAPI.getInstance();

        String[] family_members = api.getMemberNames();
        String[] spinner_members = new String[family_members.length];
        for(int i = 0; i < family_members.length; i++)  spinner_members[i] = family_members[i];

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, spinner_members);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        assigneeSpinner.setAdapter(aa);

        v.findViewById(R.id.choresChoreDueDateInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        assigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assigneeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button assignChoreButton = (Button) v.findViewById(R.id.choresAssignChoreButton);
        assignChoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView selectedView = (TextView) assigneeSpinner.getSelectedView();


                String name = nameEditText.getText().toString();
                String assignee = selectedView.getText().toString();
                String date = dateText.getText().toString();

                if (name.length() != 0 && assignee.length() != 0 && date.length() != 0) {
                    dismiss();

                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    assignChore(name, assignee, date, activity, documentSnapshot);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                } else { /* fields are not filled, notify the user */
                    if (name.length() == 0) {
                        nameEditText.requestFocus();
                        nameEditText.setError("Enter chore");
                        Toast.makeText(activity, "Enter chore", Toast.LENGTH_SHORT).show();
                    }

                    if (date.length() == 0) {
                        dateText.requestFocus();
                        dateText.setError("Enter Date");
                        Toast.makeText(activity, "Enter Date", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return v;
    }

//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//
//        saveChoreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String choreName = enterChore.getText().toString().trim();
//                String assignee = enterAssignee.getText().toString().trim();
//                if(!TextUtils.isEmpty(choreName) && !TextUtils.isEmpty(assignee)){
//                    String userId = mAuth.getUid();
//                    DocumentReference userRef = db.collection("users").document(userId);
//
//                    userRef.get()
//                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    String familyId = documentSnapshot.getString("familyId");
//                                    DocumentReference familyRef = db.collection("families").document(familyId);
//                                    String choresId = familyRef.collection("chores").document().getId();
//                                    DocumentReference choresRef = familyRef.collection("chores").document(choresId);
//
//                                    Map<String, Object> choresObj = new HashMap();
//                                    choresObj.put("choresId", choresId);
//                                    choresObj.put("name", choreName);
//                                    choresObj.put("assignee", assignee);
//                                    choresObj.put("day", "Monday");
//                                    choresObj.put("isDone", false);
//
//                                    choresRef.set(choresObj)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Toast.makeText(getActivity(), "Great success", Toast.LENGTH_LONG).show();
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
//                                                }
//                                            });
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                } else {
//
//                }
//            }
//        });
//
//        calendarButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                daysGroup.setVisibility(daysGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//            }
//        });
//    }

    private void showDatePickerDialog() {

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        /* only allowing future dates to be selected */
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ (86400 * 7 * 1000));
        dialog.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        dateText.setText(date);
    }

    private void assignChore(String name, String assignee, String date, Activity activity, DocumentSnapshot documentSnapshot) {

        String familyId = documentSnapshot.getString("familyId");
        DocumentReference familyRef = db.collection("families").document(familyId);
        String choresId = familyRef.collection("chores").document().getId();
        DocumentReference choresRef = familyRef.collection("chores").document(choresId);

        /* getting the user id of the assignee*/
        HousemateAPI api = HousemateAPI.getInstance();
        List<Map<String, Object>> membersInfo = api.getMembersList();
        Map<String, Object> userInfo = membersInfo.get(assigneeIndex);
        Object userId = userInfo.get("userId");

        Map<String, Object> choresObj = new HashMap();
        choresObj.put("choresId", choresId);
        choresObj.put("name", name);
        choresObj.put("assignee", assignee);
        choresObj.put("date", date);
        choresObj.put("creator", userId);
        choresObj.put("isDone", false);

        choresRef.set(choresObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "assign chore success", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
