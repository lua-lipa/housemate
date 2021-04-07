package com.example.housemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.housemate.Bills.AddBillFragment;
import com.example.housemate.Bills.ViewBillsFragment;
import com.example.housemate.Chores.ViewChoresFragment;
import com.example.housemate.ShoppingList.ShoppingFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton choresButton;
    private ImageButton shoppingButton;
    private ImageButton billsButton;
    private ImageButton remindersButton;

    private ImageButton selectedButton;

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        choresButton = findViewById(R.id.homeChoresButton);
        shoppingButton = findViewById(R.id.homeShoppingButton);
        billsButton = findViewById(R.id.homeBillsButton);
        remindersButton = findViewById(R.id.homeRemindersButton);
        choresButton.setOnClickListener(this);
        shoppingButton.setOnClickListener(this);
        billsButton.setOnClickListener(this);
        remindersButton.setOnClickListener(this);


        selectedButton = choresButton;
        selectedButton.setBackgroundResource(R.drawable.rounded_button_highlighted_3dp);
        fragmentManager.beginTransaction()
                .add(R.id.homeFrameLayout, new ViewChoresFragment())
                .commit();
    }

    @Override
    public void onClick(View v) {
        ImageButton lastSelectedButton = selectedButton;
        Fragment fragment = new ViewChoresFragment();
        int id = v.getId();
        if (id == R.id.homeChoresButton) {
            fragment = new ViewChoresFragment();
            selectedButton = choresButton;
        } else if (id == R.id.homeShoppingButton) {
            fragment = new ShoppingFragment();
            selectedButton = shoppingButton;
        } else if (id == R.id.homeBillsButton) {
            fragment = new ViewBillsFragment();
            selectedButton = billsButton;
        } else if (id == R.id.homeRemindersButton) {
            fragment = new RemindersFragment();
            selectedButton = remindersButton;
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