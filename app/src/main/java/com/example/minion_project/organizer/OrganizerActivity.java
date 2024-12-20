/**
 * OrganizerActivity.java
 *
 * Main activity for the organizer interface
 * Includes organizer navigation which includes create events, my events fragments
 *
 * Outstanding Issues:
 * - None
 */

package com.example.minion_project.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityOrganizerBinding;
import com.example.minion_project.user.UserAttendingFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class OrganizerActivity extends AppCompatActivity {
    ActivityOrganizerBinding binding;
    public Organizer organizer;
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private String android_id;
    private CollectionReference organizersRef;

    public OrganizerController organizerController;

    /**
     * Called when the activity receives a new intent.
     * @param intent The new intent to be handled.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handles the intent received by the activity.
     * @param intent The intent to be handled.
     */
    private void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if ("OPEN_FACILITY_FRAGMENT".equals(action)) {
                replaceFragment(new OrganizerFacility(organizerController));
            } else if ("OPEN_CREATE_EVENT_FRAGMENT".equals(action)) {
                replaceFragment(new OrganizerCreateEvent(organizerController));
            }
        }
    }

    /**
     * Initializes the activity, sets up the binding, and fetches the organizer data from Firestore.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        organizersRef = Our_Firestore.getOrganizersRef();

        organizersRef.document(android_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        String name = (String) data.get("Name");
                        ArrayList<String> events = (ArrayList<String>) data.get("Events");
                        String phoneNumber = (String) data.get("Phone_number");
                        String email = (String) data.get("Email");
                        boolean facility = (boolean) data.get("Facility");

                        organizer = new Organizer(events, phoneNumber, email, name, android_id, facility);
                        organizerController = new OrganizerController(organizer);

                        String navigateTo = getIntent().getStringExtra("navigate_to");
                        if ("create_event".equals(navigateTo)) {
                            replaceFragment(new OrganizerCreateEvent(organizerController));
                            binding.organizerBottomNavigationView.setSelectedItemId(R.id.menu_organizer_create_event);
                        } else {
                            // Default to My Events page
                            replaceFragment(new OrganizerEvents(organizerController));
                            binding.organizerBottomNavigationView.setSelectedItemId(R.id.menu_organizer_events);
                            binding.organizerTextView.setText("My Events");
                        }
                    }
                }
            } else {
                Toast.makeText(OrganizerActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });

        binding.organizerBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_organizer_events) {
                replaceFragment(new OrganizerEvents(organizerController));
                binding.organizerTextView.setText("My Events");
            } else if (itemId == R.id.menu_organizer_create_event) {
                replaceFragment(new OrganizerCreateEvent(organizerController));
                binding.organizerTextView.setText("Create Event");
            }
            return true;
        });
    }

    /**
     * Replaces the current fragment with a new fragment.
     * @param fragment The fragment to be replaced.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutOrganizer, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Updates the header text of the activity.
     * @param text The new text to be displayed in the header.
     */
    public void updateHeaderText(String text) {
        binding.organizerTextView.setText(text);
    }

}