package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housemate.util.customToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText fullname, emailId, password;
    Button btnSignUp;
    TextView tvSignUp;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener; //used for any changes in authenitcation state such as signing in - this listener is invoked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }


//        emailId = findViewById(R.id.enterEmailPlaceholder);
//        password = findViewById(R.id.enterPasswordPlaceholder);
//        fullname = findViewById(R.id.enterFullName);
//        btnSignUp = findViewById(R.id.logInButton);
//
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
//                if(mFirebaseUser!=null) { // check the current user
//                    moveToHomeActivity(mFirebaseUser);
//                }else{
//                    customToast.createToast(SignupActivity.this, "Please Login", false);
//                }
//            }
//        };

//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = emailId.getText().toString();
//                String pwd = password.getText().toString();
//                String fname = fullname.getText().toString();
//
//                if(email.isEmpty()){
//                    emailId.setError("Please provide an email");
//                    emailId.requestFocus();
//                }else if(pwd.isEmpty()){
//                    password.setError("Please provide a password");
//                    password.requestFocus();
//                }else if(email.isEmpty() && pwd.isEmpty()){
//                    Toast.makeText(SignupActivity.this, "Please fill in the empty fields", Toast.LENGTH_LONG).show();
//                }else if(!(email.isEmpty() && pwd.isEmpty())){
//                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd)
//                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if(!task.isSuccessful()){
//                                        Toast.makeText(SignupActivity.this ,"Login error, log in", Toast.LENGTH_LONG).show();
//                                    }else{
//                                        moveToHomeActivity(task.getResult().getUser());
//                                    }
//                                }
//                            });
//                }else{
//                    Toast.makeText(SignupActivity.this, "error occurred", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        // if user not registered yet
//        tvSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent inLogin = new Intent(SignupActivity.this, MainActivity.class);
//                startActivity(inLogin);
//            }
//        });
//
//    }
//
//    //remain login
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
//    }
//
//
//    private void moveToHomeActivity(FirebaseUser mFirebaseUser) {
//        firebaseDatabase.getReference().child(mFirebaseUser.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        UserData userData = snapshot.getValue(UserData.class);
//                        String name = userData.getFullname();
//                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//                        customToast.createToast(getApplicationContext(), "Login Successful", false);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        i.putExtra("name", name);
//                        startActivity(i);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//        });
//    }
}

/* Create Account / Sign up
*
* final String email = emailId.getText().toString();
                final String pwd = password.getText().toString();

                if (email.isEmpty()) {
                    emailId.setError("Please enter an email address");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter a password");
                    password.requestFocus();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        customToast.createToast(MainActivity.this,
                                                "Login Unsuccessful, Try again!"
                                                        + task.getException().getMessage(), true);
                                    } else {
                                        UserData userdata = new UserData(email);
                                        String userId = task.getResult().getUser().getUid();
                                        firebaseDatabase.getReference(userId).setValue(userdata)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK); //This clears any existing task that would be associated with the Activity before the Activity is started.
                                                        intent.putExtra("email ", email);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                }
                            });
                } else {
                    customToast.createToast(MainActivity.this, "Error occurred", true);
                }*/