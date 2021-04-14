package com.example.housemate.Bills;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private EditText dateText;
    EditText titleEditText;
    EditText amountEditText;
    Spinner assigneeSpinner;
    private int assigneeIndex;
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
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_bills, container, false);
//        v.setContentView(R.layout.activity_addbill);
        mAuth = FirebaseAuth.getInstance();
        Activity activity = getActivity();
        HousemateAPI api = HousemateAPI.getInstance();

        dateText = (EditText) v.findViewById(R.id.billsBillDateInput);
        titleEditText = (EditText) v.findViewById(R.id.billsBillNameInput);
        amountEditText = (EditText) v.findViewById(R.id.billsBillAmountInput);
        assigneeSpinner = (Spinner) v.findViewById(R.id.billsBillAssignInput);

        /* getting family member names from the db, and adding it to spinner along with "all members" option */
        String[] family_members = api.getMemberNames();
        String[] spinner_members = new String[family_members.length + 1];
        if(api.isAdmin()) {
            for (int i = 0; i < family_members.length; i++) spinner_members[i] = family_members[i];
            spinner_members[spinner_members.length - 1] = "All members";
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

        v.findViewById(R.id.billsBillDateInput).setOnClickListener(new View.OnClickListener() {
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

        Button addBillButton = (Button) v.findViewById(R.id.billsAddBillButton);
        addBillButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView selectedView = (TextView) assigneeSpinner.getSelectedView();


                String title = titleEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String assignee = selectedView.getText().toString();
                String date = dateText.getText().toString();

                if (title.length() != 0 && amount.length() != 0 && assignee.length() != 0 && date.length() != 0) {
                    dismiss(); /* minimize the pop up fragment*/
                    /* fields are not empty so we add the bill */


                    /* get family id from the database */


                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if (assignee.equals("All members")) {
                                        HousemateAPI api = new HousemateAPI().getInstance(); /* need to make this global*/

                                        String[] members = api.getMemberNames();
                                        double divided_amount = Integer.parseInt(amount) / ((double) members.length) ;
                                        for(int i = 0; i < members.length; i++) {
                                            assigneeIndex = i;
                                            addBill(title, members[i], date, divided_amount + "", activity, documentSnapshot);
                                        }
                                    } else {
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
    return v;
    }

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


        /* getting the user id of the assignee*/
        HousemateAPI api = HousemateAPI.getInstance();
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
                        Toast.makeText(activity, "add bill success", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        });

        /* adding the activity to the bills activity database */


        String billActivityId = familyRef.collection("billsActivity").document().getId();
        DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(billActivityId);

        Map<String, Object> houseActivityObj = new HashMap<>();
        String assignerUserName = api.getUserName().substring(0, api.getUserName().indexOf(" "));
        String assigneeUserName = assignee.substring(0, assignee.indexOf(" "));
        String message = assignerUserName + " assigned the " + title + " bill to " + assigneeUserName + ".";

        /* the date gets formatted to display correctly in the activity view */
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date();
        String cur_time = formatter.format(d);


        Date currentTime = Calendar.getInstance().getTime();
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