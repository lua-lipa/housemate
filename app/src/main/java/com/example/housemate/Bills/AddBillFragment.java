package com.example.housemate.Bills;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBillFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText dateText;
    EditText titleEditText;
    EditText amountEditText;
    Spinner assigneeSpinner;
    
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

        String[] family_members = api.getMemberNames();

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, family_members);
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
                    /* fields are not empty so we add the bill */

                    /* get family id from the database */


                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    String familyId = documentSnapshot.getString("familyId");
                                    DocumentReference familyRef = db.collection("families").document(familyId);
                                    String billsId = familyRef.collection("bills").document().getId();
                                    DocumentReference billsRef = familyRef.collection("bills").document(billsId);

                                    Map<String, Object> billsObj = new HashMap();
                                    billsObj.put("billsId", billsId);
                                    billsObj.put("title", title);
                                    billsObj.put("amount", amount);
                                    billsObj.put("assignee", assignee);
                                    billsObj.put("date", date);

                                    
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

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                    EditText titleEditText;
                    EditText amountEditText;
                    Spinner assigneeSpinner;

                } else {
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
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        dateText.setText(date);
    }
}