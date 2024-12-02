package com.example.minion_project.user;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityUserBinding;
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
    ActivityUserBinding binding;
    public User user;
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private String android_id;
    private ImageView headerImage;
    private UserController userController;
    private CollectionReference usersRef, eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        headerImage = findViewById(R.id.headerImage);
        replaceFragment(new UserAttendingFragment());
        usersRef = Our_Firestore.getUsersRef();
        eventsRef = Our_Firestore.getEventsRef();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        headerImage.setOnClickListener(v -> {
            replaceFragment(new UserSettingsFragment());
            binding.textView.setText("Settings");
        });

        usersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            String name = (String) data.get("Name");
                            String profileImageUrl = (String) data.get("profileImage");

                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(UserActivity.this)
                                        .load(profileImageUrl)
                                        .apply(new RequestOptions()
                                                .placeholder(R.drawable.baseline_account_circle_24).circleCrop())
                                        .into(headerImage);
                            }

                            HashMap<String, String> events = (HashMap<String, String>) data.get("Events");

                            String phoneNumber = (String) data.get("Phone_number");
                            String email = (String) data.get("Email");
                            String location = (String) data.get("Location");
                            HashMap<String, ArrayList> notification = (HashMap<String, ArrayList>) data.get("Notification");
                            User user = new User(android_id, name, email, phoneNumber, events, location, notification);
                            userController = new UserController(user);
                        }
                    }
                } else {
                    Toast.makeText(UserActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.userBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_user_attending) {
                replaceFragment(new UserAttendingFragment());
                binding.textView.setText("What's Popping");
                loadUserProfileImage();
            } else if (itemId == R.id.menu_user_settings) {
                replaceFragment(new UserSettingsFragment());
                binding.textView.setText("Settings");
                loadUserProfileImage();
            } else if (itemId == R.id.menu_user_updates) {
                replaceFragment(new UserEventStatusFragment(userController));
                binding.textView.setText("Events Interacted");
                loadUserProfileImage();
            } else if (itemId == R.id.menu_user_scan_qr) {
                // Replace with UserScanFragment and launch QR scan after fragment is active
                UserScanFragment scanFragment = new UserScanFragment(userController);
                replaceFragment(scanFragment);
                binding.textView.setText("Scan QR");
                binding.fragmentContainerView2.post(() -> {
                    // Ensure fragment is ready before invoking scan
                    if (scanFragment.isAdded() && scanFragment.isVisible()) {
                        scanFragment.scanQr();
                    }
                });
            }
            return true;
        });
    }

    private void loadUserProfileImage() {
        usersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String profileImageUrl = document.getString("profileImage");

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(UserActivity.this)
                                    .load(profileImageUrl)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.baseline_account_circle_24).circleCrop())
                                    .into(headerImage);
                        } else {
                            headerImage.setImageResource(R.drawable.baseline_account_circle_24);
                        }
                    }
                }
            }
        });
    }

    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment);
        fragmentTransaction.commit();
    }
}
