package com.example.housemate.ShoppingList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.housemate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    EditText enterItem;
    ImageButton calendarButton;
    ImageButton saveItemButton;
    CalendarView calendarView;
    Group calendarGroup;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public BottomSheetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterItem = view.findViewById(R.id.enter_item);
        saveItemButton = view.findViewById(R.id.save_item_button);

        Chip today = view.findViewById(R.id.today_chip);
        Chip tomorrow = view.findViewById(R.id.tomorrow_chip);
        Chip nextWeek = view.findViewById(R.id.next_week_chip);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fetching info from views is ideal here, once onCreateView is done, this will run right after (when the view is already created)

        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = enterItem.getText().toString().trim();
                if (!TextUtils.isEmpty(item)) {
                    // ShoppingItem myItem = new ShoppingItem(item, Calendar.getInstance().getTime(), false);
                    // add to db

                    String userId = mAuth.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    //get family id
                                    //create sub collection in that family doc using the id
                                    //after collection, add data
                                    //save data with this button
                                    String familyId = documentSnapshot.getString("family");
                                    DocumentReference familyRef = db.collection("families").document(familyId);
                                    String shoppingListId = familyRef.collection("shoppingList").document().getId();
                                    DocumentReference shoppingListRef = familyRef.collection("shoppingList").document(shoppingListId);

                                    Map<String, Object> shoppingListObj = new HashMap();
                                    shoppingListObj.put("shoppingListId", shoppingListId);
                                    shoppingListObj.put("item", item);
                                    //date

                                    shoppingListRef.set(shoppingListObj)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(), "Great success", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                } else {
                    // type smth pls
                }

            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }
}