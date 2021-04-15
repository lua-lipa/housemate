package com.example.housemate.ShoppingList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housemate.R;
import com.example.housemate.util.HousemateAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    //items from xml file for fragment_bottom_sheet
    EditText enterItem;
    ImageButton calendarButton;
    ImageButton saveItemButton;
    CalendarView calendarView;
    String date;
    Group calendarGroup;

    //firebase info variables
    private FirebaseAuth mAuth;
    //firebase entry point
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public BottomSheetFragment() {
        //required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        // references to all values in fragment_bottom_sheet
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterItem = (EditText) view.findViewById(R.id.enter_item);
        saveItemButton = view.findViewById(R.id.save_item_button);

        //entry point to firebase
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fetching info from views is ideal here, once onCreateView is done, this will run right after (when the view is already created)
        //save item button in the fragment
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setting the text in the enter item text view to a string
                String item = enterItem.getText().toString();
                //getting the username so we can store it in the shopping list
                String user = HousemateAPI.getInstance().getUserName();
                //getting the date and time
                Date c = Calendar.getInstance().getTime();

                //setting the format we want for the date
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                date = df.format(c);

                if (!TextUtils.isEmpty(item)) {
                    //getting the userid
                    String userId = mAuth.getUid();
                    DocumentSnapshot documentSnapshot = null;

                    String familyId = HousemateAPI.getInstance().getFamilyId();
                    DocumentReference familyRef = db.collection("families").document(familyId);
                    String shoppingListId = familyRef.collection("shoppingList").document().getId();
                    DocumentReference shoppingListRef = familyRef.collection("shoppingList").document(shoppingListId);

                    //mapping the tags to our data fields
                    Map<String, Object> shoppingListObj = new HashMap();
                    shoppingListObj.put("shoppingListId", shoppingListId);
                    shoppingListObj.put("item", item);
                    shoppingListObj.put("date", date);
                    shoppingListObj.put("isBought", false);
                    shoppingListObj.put("user", user);

                    addItemAddedActivity(shoppingListObj.get("item").toString());

                    //setting the shopping list reference ot the shopping list object hashmap to populate the array with the new item
                    shoppingListRef.set(shoppingListObj)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dismiss();
                                    Toast.makeText(getActivity(), "Item Added!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    //a check to ensure that we are typing something into the text field before we press the save button
                    if (item.length() == 0) {
                        enterItem.requestFocus();
                        enterItem.setError("Enter Text");
                        Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });

        //calendar button to click and view it for convenience of the user to see what date it is today
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void addItemAddedActivity(String item) {
        HousemateAPI api = HousemateAPI.getInstance();
        String familyId = api.getInstance().getFamilyId();
        String userName = api.getUserName().substring(0, api.getUserName().indexOf(" "));
        String message = userName + " added " + item + " to the shopping list.";

        DocumentReference familyRef = db.collection("families").document(familyId);
        String shoppingActivityId = familyRef.collection("houseActivity").document().getId();
        DocumentReference houseActivityRef = familyRef.collection("houseActivity").document(shoppingActivityId);
        Map<String, Object> houseActivityObj = new HashMap<>();

        //date gets formatted to display correctly in the activity view
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = new Date();
        String cur_time = formatter.format(d);

        houseActivityObj.put("shoppingActivityId", shoppingActivityId);
        houseActivityObj.put("message", message) ;
        houseActivityObj.put("date", cur_time);
        houseActivityObj.put("type", "shopping");
        houseActivityRef.set(houseActivityObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "add activity success", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}