package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //activity_login.xml variables
    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //referencing the variables in activity_login
        loginButton = findViewById(R.id.loginButton);
        emailInput = findViewById(R.id.loginEmailInput);
        passwordInput = findViewById(R.id.loginPasswordInput);
        signUpButton = findViewById(R.id.loginSignUpButton);
        mAuth = FirebaseAuth.getInstance();

        //Bring to sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpActivityIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpActivityIntent);
            }
        });

        //Attempt to login with email and password
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> signIn())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
    }

    private void signIn() {
        String userId = mAuth.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //getting references to the family and user id along with the username
                        String familyId = documentSnapshot.getString("familyId");
                        String userId = documentSnapshot.getString("userId");
                        String userName = documentSnapshot.getString("name");

                        HousemateAPI housemateAPI = HousemateAPI.getInstance();
                        housemateAPI.setUserId(userId);
                        housemateAPI.setUserName(userName);
                        housemateAPI.setFamilyId(familyId);

                        if (familyId != null) {
                            DocumentReference familyRef= db.collection("families").document(familyId);
                            familyRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            /* get the member map from firebase */
                                            List<Map<String, Object>> membersList = (List<Map<String, Object>>) documentSnapshot.get("members");
                                            String familyOwnerId = documentSnapshot.getString("familyOwnerId");
                                            String familyId = documentSnapshot.getString("familyId");
                                            String familyName = documentSnapshot.getString("familyName");

                                            housemateAPI.setFamilyId(familyId);
                                            housemateAPI.setFamilyName(familyName);
                                            housemateAPI.setMembersList(membersList);
                                            housemateAPI.setFamilyOwnerId(familyOwnerId);
                                            housemateAPI.setIsAdmin((Boolean) housemateAPI.getMemberFromUserId(userId).get("isAdmin"));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                            Intent homeActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(homeActivityIntent);
                        } else {
                            Intent familyActivityIntent = new Intent(LoginActivity.this, FamilyActivity.class);
                            startActivity(familyActivityIntent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            signIn();
        }
    }


}