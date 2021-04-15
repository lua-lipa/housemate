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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddChoreBottomFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener{
    //declaring variables
    private EditText dateText;
    EditText nameEditText;
    Spinner assigneeSpinner;
    private int assigneeIndex;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AddChoreBottomFragment(){} //compulsory empty constructor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_chore, container, false);
        //initiate the variables
        dateText = (EditText) v.findViewById(R.id.choresChoreDueDateInput);
        nameEditText = (EditText) v.findViewById(R.id.choresChoreNameInput);
        assigneeSpinner = (Spinner) v.findViewById(R.id.choresChoreAssigneeInput);
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();
        HousemateAPI api = HousemateAPI.getInstance();
        //get the family members
        String[] family_members = api.getMemberNames();
        String[] spinner_members = new String[family_members.length];
        //add the family members names to the spinner
        for(int i = 0; i < family_members.length; i++)  spinner_members[i] = family_members[i];
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, spinner_members);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assigneeSpinner.setAdapter(aa);
        //on click listener for clicking on date
        v.findViewById(R.id.choresChoreDueDateInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        //on click listener for clicking on assignee
        assigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assigneeIndex = position; //get the position of the user picked
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //initiate the assign chore button
        Button assignChoreButton = (Button) v.findViewById(R.id.choresAssignChoreButton);
        //on click listener for when assign chore is clicked
        assignChoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fill in selected items
                TextView selectedView = (TextView) assigneeSpinner.getSelectedView();
                String name = nameEditText.getText().toString();
                String assignee = selectedView.getText().toString();
                String date = dateText.getText().toString();
                //make sure all information is filled in
                if (name.length() != 0 && assignee.length() != 0 && date.length() != 0) {
                    dismiss(); //dismiss the bottom sheet fragment after user is done
                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);
                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    assignChore(name, assignee, date, activity, documentSnapshot);
                                    addChoreAddedActivity(name, assignee);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                } else { //notify user is they don't fill in fields
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

    private void showDatePickerDialog() {
        //date picker to display for the user
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        //only allowing future dates to be selected
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        //can only assign a chore a week in advance
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ (86400 * 7 * 1000));
        dialog.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        dateText.setText(date);
    }

    private void addChoreAddedActivity(String name, String assignee) {
        HousemateAPI api = HousemateAPI.getInstance();
        String userId = mAuth.getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        String familyId = api.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        String billActivityId = familyRef.collection("houseActivity").document().getId();
        DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(billActivityId);
        Map<String, Object> houseActivityObj = new HashMap<>();
        String assignerUserName = api.getUserName().substring(0, api.getUserName().indexOf(" "));
        String assigneeUserName = assignee.substring(0, assignee.indexOf(" "));
        String message = assignerUserName + " assigned the " + name + " chore to " + assigneeUserName + ".";
        /* the date gets formatted to display correctly in the activity view */
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date();
        String cur_time = formatter.format(d);
        houseActivityObj.put("billActivityId", billActivityId);
        houseActivityObj.put("message", message) ;
        houseActivityObj.put("date", cur_time);
        houseActivityObj.put("type", "chore");
        houseActivityRef.set(houseActivityObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getActivity(), "add bill activity success", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void assignChore(String name, String assignee, String date, Activity activity, DocumentSnapshot documentSnapshot) {

        String familyId = documentSnapshot.getString("familyId");
        DocumentReference familyRef = db.collection("families").document(familyId);
        String choresId = familyRef.collection("chores").document().getId();
        DocumentReference choresRef = familyRef.collection("chores").document(choresId);

        //getting the user id of the assignee
        HousemateAPI api = HousemateAPI.getInstance();
        List<Map<String, Object>> membersInfo = api.getMembersList();
        Map<String, Object> userInfo = membersInfo.get(assigneeIndex);
        Object userId = userInfo.get("userId");

        //add the items of the chore
        Map<String, Object> choresObj = new HashMap();
        choresObj.put("choresId", choresId);
        choresObj.put("name", name);
        choresObj.put("assignee", assignee);
        choresObj.put("date", date);
        choresObj.put("creator", userId);
        choresObj.put("isDone", false);

        //let user know whether the chore was assigned successfully or not
        choresRef.set(choresObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "Chore assigned", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
