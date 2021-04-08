package com.example.housemate.family;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.FamilyActivity;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyBottomSheetFragment extends BottomSheetDialogFragment {
    private TextView titleTextView;
    private Switch adminSwitch;
    private Button removeMemberButton;
    private FamilyRecyclerViewAdapter familyRecyclerViewAdapter = FamilyRecyclerViewAdapter.getInstance();

    private HousemateAPI housemateAPI = HousemateAPI.getInstance();
    private FamilyMember familyMember;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FamilyBottomSheetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family_member_settings, container, false);
        adminSwitch = view.findViewById(R.id.familyMemberAdminSwitch);
        removeMemberButton = view.findViewById(R.id.familyMemberRemoveMemberButton);
        titleTextView = view.findViewById(R.id.familyMemberSettingsTitleTextView);

        familyMember = housemateAPI.getSelectedMember();
        if (familyMember.getIsAdmin()) adminSwitch.setChecked(true);

        titleTextView.setText(familyMember.getName());

        if (!housemateAPI.getFamilyOwnerId().equals(housemateAPI.getUserId())) {
            adminSwitch.setVisibility(View.INVISIBLE);
        }



        adminSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String, Object>> currentMembersList = housemateAPI.getMembersList();

                if (adminSwitch.isChecked()) {
                    adminSwitch.setChecked(false);

                    new AlertDialog.Builder(getContext())
                            .setTitle("Make Member Admin")
                            .setMessage(Html.fromHtml("Make <b>" + familyMember.getName() + "</b> an admin?"))

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Set isAdmin to true
                                    DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
                                    db.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(familyRef);
                                            if (snapshot.exists()) {
                                                List<Map<String, Object>> membersList = (List<Map<String, Object>>) snapshot.get("members");

                                                for(int i = 0; i < membersList.size(); i++) {
                                                    Map<String, Object> memberMap = membersList.get(i);
                                                    String memberUserId = (String) memberMap.get("userId");
                                                    if (memberUserId.equals(familyMember.getUserId())) {
                                                        //make member admin
                                                        membersList.get(i).put("isAdmin", true);
                                                    }
                                                }

                                                housemateAPI.setMembersList(membersList);

                                                transaction.update(familyRef, "members", membersList);
                                            } else {
                                                Toast.makeText(getContext(), "Invalid Invite Code", Toast.LENGTH_LONG).show();
                                            }

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "great success", Toast.LENGTH_LONG).show();
                                            familyMember.setAdmin(true);
                                            if (adminSwitch.isChecked()) {
                                                adminSwitch.setChecked(false);
                                            } else {
                                                adminSwitch.setChecked(true);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            housemateAPI.setMembersList(currentMembersList);
                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                } else {
                    adminSwitch.setChecked(true);

                    new AlertDialog.Builder(getContext())
                            .setTitle("Remove Member as an Admin")
                            .setMessage(Html.fromHtml("Remove <b>" + familyMember.getName() + "</b> as an admin?"))

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Set isAdmin to true
                                    DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
                                    db.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(familyRef);
                                            if (snapshot.exists()) {
                                                List<Map<String, Object>> membersList = (List<Map<String, Object>>) snapshot.get("members");

                                                for(int i = 0; i < membersList.size(); i++) {
                                                    Map<String, Object> memberMap = membersList.get(i);
                                                    String memberUserId = (String) memberMap.get("userId");
                                                    if (memberUserId.equals(familyMember.getUserId())) {
                                                        //make member admin
                                                        memberMap.put("isAdmin", false);
                                                    }
                                                }

                                                housemateAPI.setMembersList(membersList);

                                                transaction.update(familyRef, "members", membersList);
                                            } else {
                                                Toast.makeText(getContext(), "Invalid Invite Code", Toast.LENGTH_LONG).show();
                                            }

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "great success", Toast.LENGTH_LONG).show();
                                            familyMember.setAdmin(false);
                                            if (adminSwitch.isChecked()) {
                                                adminSwitch.setChecked(false);
                                            } else {
                                                adminSwitch.setChecked(true);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            housemateAPI.setMembersList(currentMembersList);
                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }


            }
        });

        removeMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String, Object>> currentMembersList = housemateAPI.getMembersList();
                new AlertDialog.Builder(getContext())
                        .setTitle("Remove member")
                        .setMessage(Html.fromHtml("Are you sure you want to remove <b>" + familyMember.getName() + "</b> from the family?"))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Set isAdmin to true
                                DocumentReference familyRef = db.collection("families").document(housemateAPI.getFamilyId());
                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot snapshot = transaction.get(familyRef);
                                        if (snapshot.exists()) {
                                            List<Map<String, Object>> membersList = (List<Map<String, Object>>) snapshot.get("members");

                                            for(int i = 0; i < membersList.size(); i++) {
                                                Map<String, Object> memberMap = membersList.get(i);
                                                String memberUserId = (String) memberMap.get("userId");
                                                if (memberUserId.equals(familyMember.getUserId())) {
                                                    //make member admin
                                                    membersList.remove(i);
                                                }
                                            }

                                            housemateAPI.setMembersList(membersList);

                                            transaction.update(familyRef, "members", membersList);
                                        } else {
                                            Toast.makeText(getContext(), "Invalid Invite Code", Toast.LENGTH_LONG).show();
                                        }

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "great success", Toast.LENGTH_LONG).show();
                                        familyRecyclerViewAdapter.updateData(housemateAPI.getMembersList());
                                        familyMember.setAdmin(true);
                                        if (adminSwitch.isChecked()) {
                                            adminSwitch.setChecked(false);
                                        } else {
                                            adminSwitch.setChecked(true);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        housemateAPI.setMembersList(currentMembersList);
                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}