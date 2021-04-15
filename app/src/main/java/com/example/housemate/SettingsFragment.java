package com.example.housemate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends Fragment {
    private HousemateAPI housemateAPI = HousemateAPI.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private Button changeNameButton;
    private Button changePasswordButton;
    private Button deleteAccountButton;
    private Button logOutButton;

    public SettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();

        changeNameButton = view.findViewById(R.id.settingsChangeNameButton);
        changePasswordButton = view.findViewById(R.id.settingsChangePasswordButton);
        deleteAccountButton = view.findViewById(R.id.settingsDeleteAccountButton);
        logOutButton = view.findViewById(R.id.settingsLogOutButton);

        changeNameButton.setOnClickListener(v -> {
            showChangeNameDialog();
        });

        changePasswordButton.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivityIntent = new Intent(getContext(), LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });

        return view;
    }

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_password, null);

        EditText oldPasswordEditText = view.findViewById(R.id.changePasswordOldPasswordInput);
        EditText newPasswordEditText = view.findViewById(R.id.changePasswordNewPasswordInput);
        Button updatePasswordButton = view.findViewById(R.id.changePasswordUpdateButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
        dialog.show();

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                FirebaseUser user = mAuth.getCurrentUser();

                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                user.reauthenticate(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void showChangeNameDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_name, null);

        EditText changeNameEditText = view.findViewById(R.id.changeNameInput);
        Button updateNameButton = view.findViewById(R.id.changeNameButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
        dialog.show();

        updateNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = changeNameEditText.getText().toString().trim();

                String userId = housemateAPI.getUserId();
                DocumentReference userRef = db.collection("users").document(userId);

                userRef.update("name", newName)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Name Updated", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}