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

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityOrganizerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        organizersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            String name = (String) data.get("Name");
                            ArrayList<String> events = (ArrayList<String>) data.get("Events");
                            String phoneNumber = (String) data.get("Phone_number");
                            String email = (String) data.get("Email");

                            organizer = new Organizer(events, phoneNumber, email, name, android_id);
                            organizerController = new OrganizerController(organizer);

                            // Pass the controller to the initial fragment
                            replaceFragment(new OrganizerEvents(organizerController));
                        }
                    }
                } else {
                    Toast.makeText(OrganizerActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
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
     * Replaces the current fragment with a new fragment
     * @param fragment the fragment to be displayed
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutOrganizer, fragment);
        fragmentTransaction.commit();
    }
}
