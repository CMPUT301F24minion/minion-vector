/**
 * AdminActivity.java
 *
 * Main activity for the admin interface
 * Includes admin navigation which includes events, images, and profiles fragments
 *
 * Outstanding Issues:
 * - None
 */

package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityAdminBinding;
import com.example.minion_project.organizer.OrganizerEvents;

/**
 * AdminActivity class manages the main UI components for the admin role
 * Sets up the fragment navigation and binding the view with the corresponding data
 */
public class AdminActivity extends AppCompatActivity {

    // Binding object for activity layout views
    ActivityAdminBinding binding;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default fragment when the admin activity starts
        replaceFragment(new AdminEvents());
        binding.adminTextView.setText("Admin Events");

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

