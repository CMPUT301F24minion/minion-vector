package com.example.minion_project.user;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.Event;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.MainActivity;
import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityUserBinding;
import com.example.minion_project.organizer.OrganizerEvents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class UserActivity extends AppCompatActivity {
    // a basic activity

    ActivityUserBinding binding;
    public User user;
    public FireStoreClass Our_Firestore=new FireStoreClass();
    private String android_id;
    private ImageView headerImage;

    private CollectionReference usersRef,eventsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        headerImage = findViewById(R.id.headerImage);
        replaceFragment(new UserAttendingFragment());
        usersRef = Our_Firestore.getUsersRef();
        eventsRef= Our_Firestore.getEventsRef();
        // set up the user class
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Check if user exists
        usersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data!=null) {

                            String name = (String) data.get("Name");
                            String profileImageUrl = (String) data.get("profileImage");

                            // Load the profile image using Glide
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(UserActivity.this)
                                        .load(profileImageUrl)
                                        .apply(new RequestOptions()
                                                .placeholder(R.drawable.baseline_account_circle_24).circleCrop()) // Add a placeholder while loading// Add an error image if loading fails
                                        .into(headerImage);
                            }

                            // Todo: need to for loop here and make every event an Event class
                            HashMap <String,String> events = (HashMap<String, String>)data.get("Events");

                            String phoneNumber = (String) data.get("Phone_number");
                            String email = (String) data.get("Email");
                            String Location = (String) data.get("Location");
                            HashMap <String,ArrayList> Notification = (HashMap<String, ArrayList>) data.get("Notfication");

                            // Create the User object
                            User user = new User(android_id,name, email, phoneNumber,events, Location, Notification);


                        }
                    }

                } else {
                    Toast.makeText(UserActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.userBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the item ID

            if (itemId == R.id.menu_user_attending) {
                replaceFragment(new UserAttendingFragment());
                binding.textView.setText("What's Popping");
            } else if (itemId == R.id.menu_user_settings) {
                replaceFragment(new UserSettingsFragment());
                binding.textView.setText("Settings");
            } else if (itemId == R.id.menu_user_waitlisted) {
                replaceFragment(new UserWaitlistedFragment());
                binding.textView.setText("Waitlists");
            } else if (itemId == R.id.menu_user_updates) {
                replaceFragment(new UserUpdatesFragment());
                binding.textView.setText("Notifications");
            } else if (itemId == R.id.menu_user_scan_qr) {
                replaceFragment(new UserScanFragment());
                binding.textView.setText("Scan QR");
            }
            return true;
        });

    }
    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment);
        fragmentTransaction.commit();
    }

}

