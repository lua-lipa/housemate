package com.example.housemate.Bills;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housemate.R;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBillFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HousemateAPI api;
    private EditText dateText;
    private EditText titleEditText;
    private EditText amountEditText;
    private Spinner assigneeSpinner;
    private int assigneeIndex;
    private Button addBillButton;

    public AddBillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* setting up database globals */
        View v =  inflater.inflate(R.layout.fragment_bills, container, false);
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();
        api = HousemateAPI.getInstance();

        /* setting up layout elements*/
        dateText = (EditText) v.findViewById(R.id.billsBillDateInput);
        titleEditText = (EditText) v.findViewById(R.id.billsBillNameInput);
        amountEditText = (EditText) v.findViewById(R.id.billsBillAmountInput);
        assigneeSpinner = (Spinner) v.findViewById(R.id.billsBillAssignInput);
        addBillButton = (Button) v.findViewById(R.id.billsAddBillButton);


        /* setting up the spinner: getting family member names from the db, and adding it to spinner along with "all members" option */
        setUpSpinner();

        /* setting the listeners to the layout elements */
        setUpListeners(v, activity);

        return v;
    }

    private void setUpListeners(View v, Activity activity) {
        /* date input */
        v.findViewById(R.id.billsBillDateInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(); /* displaying date picker */
            }
        });

        /* assignee menu */
        assigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assigneeIndex = position; /* keeping track of the assignee index */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* when add bill clicked, check all fields are filled correctly, add the new bill into the database */
        addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* set up elements */
                TextView selectedView = (TextView) assigneeSpinner.getSelectedView();
                String title = titleEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String assignee = selectedView.getText().toString();
                String date = dateText.getText().toString();

                if (title.length() != 0 && amount.length() != 0 && assignee.length() != 0 && date.length() != 0) {
                    dismiss(); /* minimize the pop up fragment*/
                    /* fields are not empty so we add the bill */

                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    /* if assigning to all members, the amount gets divided evenly between them and a bill gets added for each
                                    * of the members into the database */
                                    if (assignee.equals("All members")) {
                                        HousemateAPI api = new HousemateAPI().getInstance(); /* need to make this global*/

                                        String[] members = api.getMemberNames();
                                        double divided_amount = Integer.parseInt(amount) / ((double) members.length) ;
                                        for(int i = 0; i < members.length; i++) {
                                            assigneeIndex = i;
                                            addBill(title, members[i], date, divided_amount + "", activity, documentSnapshot);
                                        }
                                    } else { /* otherwise add individual bill */
                                        addBill(title, assignee, date, amount, activity, documentSnapshot);
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                } else { /* fields are not filled, notify the user */
                    if (title.length() == 0) {
                        titleEditText.requestFocus();
                        titleEditText.setError("Enter Text");
                        Toast.makeText(activity, "Enter Text", Toast.LENGTH_SHORT).show();
                    }

                    if (amount.length() == 0) {
                        amountEditText.requestFocus();
                        amountEditText.setError("Enter Text");
                        Toast.makeText(activity, "Enter Amount", Toast.LENGTH_SHORT).show();
                    }

                    if (date.length() == 0) {
                        dateText.requestFocus();
                        dateText.setError("Enter Text");
                        Toast.makeText(activity, "Enter Date", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /* setting up the spinners to display all family members if the user is an admin
    * if the user is not an admin, they are only able to assign a bill to themselves
    * and the spinner only shows their name */

    private void setUpSpinner() {
        String[] family_members = api.getMemberNames();
        String[] spinner_members = new String[family_members.length + 1];
        if(api.isAdmin()) {
            for (int i = 0; i < family_members.length; i++) spinner_members[i] = family_members[i];
            spinner_members[spinner_members.length - 1] = "All members"; /* option to assign bill to all members */
        } else { /* if you are an admin you can only assign a bill to yourself */
            spinner_members = new String[1];
            spinner_members[0] = api.getUserName();
            for(int i = 0; i < family_members.length; i++) {
                if (family_members[i].equals(api.getUserName())) {
                    assigneeIndex = i;
                    break;
                }
            }

        }

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, spinner_members);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        assigneeSpinner.setAdapter(aa);
    }

    private void showDatePickerDialog() { /* displays the date picker */

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        /* only allowing future dates to be selected */
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        dateText.setText(date);
    }

    private void addBill(String title, String assignee, String date, String amount, Activity activity, DocumentSnapshot documentSnapshot) {

        String familyId = documentSnapshot.getString("familyId");
        DocumentReference familyRef = db.collection("families").document(familyId);
        String billsId = familyRef.collection("bills").document().getId();
        DocumentReference billsRef = familyRef.collection("bills").document(billsId);


        /* adding the new bill into the database */
        api = HousemateAPI.getInstance();
        List<Map<String, Object>> membersInfo = api.getMembersList();
        Map<String, Object> userInfo = membersInfo.get(assigneeIndex);
        Object userId = userInfo.get("userId");

        Map<String, Object> billsObj = new HashMap();
        billsObj.put("billsId", billsId);
        billsObj.put("title", title);
        billsObj.put("amount", amount);
        billsObj.put("assignee", assignee);
        billsObj.put("date", date);
        billsObj.put("userId", userId);
        billsObj.put("isPaid", false);

        billsRef.set(billsObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "New bill added", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        });

        /* adding the "someone assigned the bill to someone else" activity to the activity database */

        String billActivityId = familyRef.collection("billsActivity").document().getId();
        DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(billActivityId);

        String assignerUserName = api.getUserName().substring(0, api.getUserName().indexOf(" "));
        String assigneeUserName = assignee.substring(0, assignee.indexOf(" "));
        String message = assignerUserName + " assigned the " + title + " bill to " + assigneeUserName + ".";

        /* the date gets formatted to display correctly in the activity view */
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date();
        String cur_time = formatter.format(d);

        Map<String, Object> houseActivityObj = new HashMap<>();
        houseActivityObj.put("billActivityId", billActivityId);
        houseActivityObj.put("message", message) ;
        houseActivityObj.put("date", cur_time);
        houseActivityObj.put("type", "bill");
        houseActivityRef.set(houseActivityObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "add bill activity success", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}