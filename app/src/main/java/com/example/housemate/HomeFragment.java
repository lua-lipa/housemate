package com.example.housemate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.housemate.Bills.AddBillFragment;
import com.example.housemate.Bills.ViewBillsFragment;
import com.example.housemate.ShoppingList.ShoppingFragment;
import com.example.housemate.Chores.ViewChoresFragment;
import com.example.housemate.chat.ChatFragment;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private ImageButton choresButton;
    private ImageButton shoppingButton;
    private ImageButton billsButton;
    private ImageButton activityButton;
    private ImageButton selectedButton;

    FragmentManager fragmentManager;

    public HomeFragment() {
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
        View view =  inflater.inflate(R.layout.activity_home, container, false);

        fragmentManager = getChildFragmentManager();
        choresButton = view.findViewById(R.id.homeChoresButton);
        shoppingButton = view.findViewById(R.id.homeShoppingButton);
        billsButton = view.findViewById(R.id.homeBillsButton);
        activityButton = view.findViewById(R.id.homeActivityButton);
        choresButton.setOnClickListener(this);
        shoppingButton.setOnClickListener(this);
        billsButton.setOnClickListener(this);
        activityButton.setOnClickListener(this);

        selectedButton = choresButton;
        selectedButton.setBackgroundResource(R.drawable.rounded_button_highlighted_3dp);
        fragmentManager.beginTransaction()
                .add(R.id.homeFrameLayout, new ViewChoresFragment())
                .commit();

        return view;
    }

    @Override
    public void onClick(View v) {
        ImageButton lastSelectedButton = selectedButton;
        Fragment fragment = new ViewChoresFragment();
        int id = v.getId();
        //if we select the chores button, we create a new chores fragment
        if (id == R.id.homeChoresButton) {
            fragment = new ViewChoresFragment();
            selectedButton = choresButton;
        } else if (id == R.id.homeShoppingButton) {
            fragment = new ShoppingFragment();
            selectedButton = shoppingButton;
        } else if (id == R.id.homeBillsButton) {
            fragment = new ViewBillsFragment();
            selectedButton = billsButton;
        } else if (id == R.id.homeActivityButton) {
            fragment = new RemindersFragment();
            selectedButton = activityButton;
        }

        if (lastSelectedButton != selectedButton) {
            lastSelectedButton.setBackgroundResource(R.drawable.rounded_button_black);
            selectedButton.setBackgroundResource(R.drawable.rounded_button_highlighted_3dp);
        }
        
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.homeFrameLayout, fragment, null)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }
}