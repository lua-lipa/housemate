package com.example.housemate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BillsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private EditText dateText;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    public BillsFragment() {
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
        dateText = (EditText) v.findViewById(R.id.billsBillDateInput);

        v.findViewById(R.id.billsBillDateInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button addBillButton = (Button) v.findViewById(R.id.billsAddBillButton);
        addBillButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                EditText title_field = (EditText) v.findViewById(R.id.billsBillNameInput);
                EditText amount_field = (EditText) v.findViewById(R.id.billsBillAmountInput);
                EditText assignee_field = (EditText) v.findViewById(R.id.billsBillAssignInput);

                String title = title_field.getText().toString();
                String amount = amount_field.getText().toString();
                String assignee = assignee_field.getText().toString();
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

                                    String familyId = documentSnapshot.getString("family");
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


                } else {
                    if (title.length() == 0) {
                        title_field.requestFocus();
                        title_field.setError("Enter Text");
                        Toast.makeText(activity, "Enter Text", Toast.LENGTH_SHORT).show();
                    }

                    if (amount.length() == 0) {
                        amount_field.requestFocus();
                        amount_field.setError("Enter Text");
                        Toast.makeText(activity, "Enter Amount", Toast.LENGTH_SHORT).show();
                    }

                    if (assignee.length() == 0) {
                        assignee_field.requestFocus();
                        assignee_field.setError("Enter Text");
                        Toast.makeText(activity, "Enter Assignee", Toast.LENGTH_SHORT).show();
                    }

                    if (date.length() == 0) {
                        assignee_field.requestFocus();
                        assignee_field.setError("Enter Text");
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