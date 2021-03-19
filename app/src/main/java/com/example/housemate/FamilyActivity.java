package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FamilyActivity extends AppCompatActivity {
    //Define UI
    private TextView subtitleTextView;
    private ImageButton createFamilyButton;
    private ImageButton joinFamilyButton;
    private TextView inputTitle;
    private EditText inputEditText;
    private Button continueButton;

    //Current option chosen
    private String currentOption = ""; //createFamily || joinFamily

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        mAuth = FirebaseAuth.getInstance();

        //Initalise UI variables
        subtitleTextView = findViewById(R.id.familySubtitleText);
        createFamilyButton = findViewById(R.id.familyCreateButton);
        joinFamilyButton = findViewById(R.id.familyJoinButton);
        inputTitle = findViewById(R.id.familyInputTitle);
        inputEditText = findViewById(R.id.familyInputEditText);
        continueButton = findViewById(R.id.familyContinueButton);

        //Set current option to family and highlight it
        currentOption = "createFamily";
        createFamilyButton.setBackgroundResource(R.drawable.rounded_button_highlighted);
        String text = "<font color=#FF8A00>Create</font> or join a family";
        subtitleTextView.setText(Html.fromHtml(text));

        createFamilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If already selected don't do anything
                if (currentOption == "createFamily") return;

                //Highlight selection and unhighlight the other option
                currentOption = "createFamily";
                createFamilyButton.setBackgroundResource(R.drawable.rounded_button_highlighted);
                joinFamilyButton.setBackgroundResource(R.drawable.rounded_button_black);

                //Change subtitle colour
                String text = "<font color=#FF8A00>Create</font> or join a family";
                subtitleTextView.setText(Html.fromHtml(text));

                //Change the name of the inputs
                inputTitle.setText(R.string.family_name);
                inputEditText.setHint(R.string.enter_a_name_for_your_family);
            }
        });

        joinFamilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If already selected don't do anything
                if (currentOption == "joinFamily") return;

                //Highlight selection and unhighlight the other option
                currentOption = "joinFamily";
                joinFamilyButton.setBackgroundResource(R.drawable.rounded_button_highlighted);
                createFamilyButton.setBackgroundResource(R.drawable.rounded_button_black);

                //Change subtitle colour
                String text = "Create or <font color=#FF8A00>join</font> a family";
                subtitleTextView.setText(Html.fromHtml(text));

                //Change the name of the inputs
                inputTitle.setText(R.string.invite_code);
                inputEditText.setHint(R.string.enter_your_invite_code);
            }
        });

        /*
        When you click the continue button it checks you are creating or joining a family
        -Creating a family:
         */
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOption == "createFamily") {
                    //make sure input is not empty
                    //add family to database
                    //Create a user map so we can create a user in the user collection
                    currentUser = mAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    String familyName = inputEditText.getText().toString().trim();
                    String familyId = db.collection("families").document().getId();
                    Map<String, Object> familyObj = new HashMap();
                    familyObj.put("familyName", familyName);
                    familyObj.put("familyId", familyId);
                    familyObj.put("members", Arrays.asList(currentUserId));

                    //save to our firestore database
                    db.collection("families").add(familyObj)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //go to success activity
                                    Intent successActivity = new Intent(FamilyActivity.this, SuccessActivity.class);
                                    startActivity(successActivity);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error message
                                }
                            });

                } else if (currentOption == "joinFamily") {
                    //make sure input is not empty
                    //add user to family
                    String inviteCode = inputEditText.getText().toString().trim();
                    CollectionReference familiesRef = db.collection("families");
                    Query query = familiesRef.whereEqualTo("familyId", inviteCode);


                    db.collection("families").whereEqualTo("familyId", inviteCode)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.exists()) {
                                                //valid invitecode
                                                //add user to family
                                                currentUser = mAuth.getCurrentUser();
                                                String currentUserId = currentUser.getUid();

                                                Map<String, Object> familyObj = new HashMap();
                                                familyObj.put("members", FieldValue.arrayUnion(currentUser));


                                                DocumentReference docRef = document.getReference();
                                                docRef
                                                        .update("members", FieldValue.arrayUnion(currentUserId))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            } else {
                                                //invalid invitecode
                                            }
                                        }
                                    } else {

                                    }
                                }
                            });



                }
            }
        });
    }
}