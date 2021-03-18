package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.housemate.util.customToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnLogin;
    TextView tvSignIn;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailId = findViewById(R.id.enterEmailPlaceholder);
        password = findViewById(R.id.enterPasswordPlaceholder);
        btnLogin = findViewById(R.id.logInButton);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
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
                                                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("email ", email);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                }
                            });
                } else {
                    customToast.createToast(MainActivity.this, "Error occurred", true);
                }
            }
        });


//        tvSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, SignupActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//            }
//        });
//        }


    }
}
