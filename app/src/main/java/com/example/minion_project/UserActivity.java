package com.example.minion_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.databinding.ActivityUserBinding;

public class UserActivity extends AppCompatActivity {
    // a basic activity
    // TODO: IMPLEMENT the bottom naviagtion for user

    ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new UserAttendingFragment());

        binding.userBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the item ID

            if (itemId == R.id.user_attending) {
                replaceFragment(new UserAttendingFragment());
            } else if (itemId == R.id.user_waitlisted) {
                replaceFragment(new UserWaitlistedFragment());
            } else if (itemId == R.id.user_updates) {
                replaceFragment(new UserUpdatesFragment());
            } else if (itemId == R.id.user_scan_qr) {
                replaceFragment(new UserScanFragment());
            }
            return true;
        });

    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment);
        fragmentTransaction.commit();
    }

}

