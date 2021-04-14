package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    //firebase variables
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //activity_signup.xml variables
    private Button signUpButton;
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //activity_signup.xml references
        signUpButton = findViewById(R.id.signUpButton);
        nameInput = findViewById(R.id.signUpNameInput);
        emailInput = findViewById(R.id.signUpEmailInput);
        passwordInput = findViewById(R.id.signUpPasswordInput);

        //firebase entry point entry point
        mAuth = FirebaseAuth.getInstance();

        //once we click the sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reference all of the textviews required
                String fullName = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                //create a user with email and password in firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            //Get userId and set reference
                            String uid = mAuth.getUid();
                            DocumentReference ref = db.collection("users").document(uid);

                            //Populate the document
                            Map<String, Object> userObj = new HashMap();
                            userObj.put("userId", uid);
                            userObj.put("name", fullName);

                            //Save document to firestore
                            ref.set(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Go to family activity
                                            HousemateAPI housemateAPI = HousemateAPI.getInstance();
                                            housemateAPI.setUserId(uid);
                                            housemateAPI.setUserName(fullName);

                                            Intent familyActivityIntent = new Intent(SignUpActivity.this, FamilyActivity.class);
                                            startActivity(familyActivityIntent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });
    }



}

