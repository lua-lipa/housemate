package com.example.housemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.housemate.chat.ChatFragment;
import com.example.housemate.family.FamilyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment homeFragment = new HomeFragment();
        Fragment familyFragment = new FamilyFragment();
        Fragment settingsFragment = new SettingsFragment();
        Fragment chatFragment = new ChatFragment();

        BottomNavigationView navigationView = findViewById(R.id.navbarBottomNavigation);
        setCurrentFragment(homeFragment);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navHome) {
                    setCurrentFragment(homeFragment);
                } else if (id == R.id.navChat) {
                    setCurrentFragment(chatFragment);
                } else if (id == R.id.navFamily) {
                    setCurrentFragment(familyFragment);
                } else if (id == R.id.navSettings) {
                    setCurrentFragment(settingsFragment);
                }
                return true;
            }
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navbarFrameLayout, fragment)
                .commit();

        }
}