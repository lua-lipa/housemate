package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChoresActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button addChoreButton;
    private EditText choreNameInput;
    private EditText choreDateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);

        addChoreButton = findViewById(R.id.choresAddChoreButton);
        choreNameInput = findViewById(R.id.choresChoreNameInput);
        choreDateInput = findViewById(R.id.choresChoreDateInput);

        addChoreButton.setOnClickListener(v -> {
            //get the chore name
            //get the chore date
            //add the chore to database

            String choreName = choreNameInput.getText().toString().trim();
            String choreDate = choreDateInput.getText().toString().trim();

            String choresId = db.collection("chores").document().getId();
            DocumentReference choresRef = db.collection("chores").document(choresId);

            //Populate the document
            Map<String, Object> choreObj = new HashMap();
            choreObj.put("choresId", choresId);
            choreObj.put("choreName", choreName);
            choreObj.put("choreDate", choreDate);

            choresRef.set(choreObj)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChoresActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}