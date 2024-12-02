// AdminActivity.java
package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityAdminBinding;

/**
 * AdminActivity is the main activity for the admin.
 */
public class AdminActivity extends AppCompatActivity {

    // Binding object for activity layout views
    private ActivityAdminBinding binding;


    @Override
    /**
     * Called when the Admin activity is first created
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new AdminEvents());

        // Admin bottom navigation
        binding.adminBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the item ID

            if (itemId == R.id.menu_admin_events) {
                replaceFragment(new AdminEvents());
                 binding.adminTextView.setText("Admin Events");
            } else if (itemId == R.id.menu_admin_images) {
                replaceFragment(new AdminImages());
                 binding.adminTextView.setText("Admin Images");
            } else if (itemId == R.id.menu_admin_profiles) {
                replaceFragment(new AdminProfiles());
                 binding.adminTextView.setText("Admin Profiles");
            } else if (itemId == R.id.menu_admin_facilities) {
                replaceFragment(new AdminFacilities());
                 binding.adminTextView.setText("Admin Facilities");
            }

            return true;
        });
    }

    /**
     * Replaces the current fragment
     *
     * @param fragment the Fragment to be displayed
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutAdmin, fragment);
        fragmentTransaction.commit();
    }
}
