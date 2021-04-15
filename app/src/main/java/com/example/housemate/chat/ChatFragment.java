package com.example.housemate.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housemate.R;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HousemateAPI housemateAPI = HousemateAPI.getInstance();


    /* */
    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    private List<Chat> chatList;
    private EditText chatInputEditText;
    private Button chatSendButton;
    private Boolean firstLoad = true;

    public ChatFragment() {
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();

        chatList = new ArrayList<>();

        chatInputEditText = view.findViewById(R.id.chatTextInput);
        chatSendButton = view.findViewById(R.id.chatSendButton);

        recyclerView = view.findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String familyId = housemateAPI.getFamilyId();
                DocumentReference familyRef = db.collection("families").document(familyId);
                CollectionReference chatRef = familyRef.collection("chat");

                String sender = housemateAPI.getUserName();
                String message = chatInputEditText.getText().toString().trim();
                Date date = new Date();


                Map<String, Object> ChatObj = new HashMap<>();
                ChatObj.put("sender", sender);
                ChatObj.put("message", message);
                ChatObj.put("date", date);
                ChatObj.put("userId", housemateAPI.getUserId());

                chatRef.document().set(ChatObj)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                chatInputEditText.setText("");
                                //hide keyboard
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(),
                                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
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


        return view;
    }

    public void sortItems(){
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Collections.sort(chatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = housemateAPI.getUserId();
        DocumentReference userRef = db.collection("users").document(userId);

        String familyId = housemateAPI.getFamilyId();
        DocumentReference familyRef = db.collection("families").document(familyId);
        CollectionReference chatRef = familyRef.collection("chat");
        chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    //error message
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    chatList.clear();
                    for (QueryDocumentSnapshot chatObj : queryDocumentSnapshots) {
                        /* making only the bills belonging to current user are being viewed */
                        Chat newChat = chatObj.toObject(Chat.class);
                        chatList.add(newChat);
                    }

                    sortItems();

                    if (firstLoad == true) {
                        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(chatList, getActivity());
                        recyclerView.setAdapter(chatRecyclerViewAdapter);
                        firstLoad = false;
                    }

                    chatRecyclerViewAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatList.size() - 1);
                } else {
                    /* display "no bills" text view */
                }
            }
        });
    }
}