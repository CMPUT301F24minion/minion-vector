package com.example.minion_project.organizer;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.FireStore;
import com.example.minion_project.OrganizerController;
import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityOrganizerBinding;
import com.example.minion_project.user.User;
import com.example.minion_project.user.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerActivity extends AppCompatActivity {
    // a basic activity
    // TODO: IMPLEMENT the bottom naviagtion for organizer


    ActivityOrganizerBinding binding;
    public Organizer organizer;
    public FireStore Our_Firestore=new FireStore();
    private String android_id;
    private CollectionReference organizersRef;

    public OrganizerController organizerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get android id
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        replaceFragment(new OrganizerEvents());
        organizersRef = Our_Firestore.getOrganizersRef();

        //set up organizer class
        organizersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data!=null) {

                            String name = (String) data.get("Name");

                            // Todo: need to for loop here and make every event an Event class
                            ArrayList<String> events = (ArrayList<String>)data.get("Events");

                            String phoneNumber = (String) data.get("Phone_number");
                            String email = (String) data.get("Email");

                            // Create the User object
                            Organizer organizer = new Organizer(events,phoneNumber,email,name,android_id );
                            organizerController=new OrganizerController(organizer);

                        }
                    }
                } else {
                    Toast.makeText(OrganizerActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.organizerBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the item ID

            if (itemId == R.id.organizerEvents) {
                replaceFragment(new OrganizerEvents());
            } else if (itemId == R.id.organizerCreateEvent) {
                // nb we now pass organizerController to each fragment
                replaceFragment(new OrganizerCreateEvent(organizerController));
            }

            return true;
        });

        //set up controller

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutOrganizer, fragment);
        fragmentTransaction.commit();
    }

}
