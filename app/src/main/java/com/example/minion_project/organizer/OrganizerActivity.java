package com.example.minion_project.organizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityOrganizerBinding;

public class OrganizerActivity extends AppCompatActivity {
    // a basic activity
    // TODO: IMPLEMENT the bottom naviagtion for organizer


    ActivityOrganizerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new OrganizerEvents());

        binding.organizerBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the item ID

            if (itemId == R.id.organizerEvents) {
                replaceFragment(new OrganizerEvents());
                binding.organizerTextView.setText("My Events");
            } else if (itemId == R.id.organizerCreateEvent) {
                replaceFragment(new OrganizerCreateEvent());
                binding.organizerTextView.setText("Create Event");
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutOrganizer, fragment);
        fragmentTransaction.commit();
    }
}
