package com.example.minion_project.user;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.Event;
import com.example.minion_project.FireStore;
import com.example.minion_project.MainActivity;
import com.example.minion_project.R;
import com.example.minion_project.databinding.ActivityUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    // a basic activity

    ActivityUserBinding binding;
    public User user;
    public FireStore Our_Firestore=new FireStore();
    private String android_id;

    private CollectionReference usersRef,eventsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

                            // Todo: need to for loop here and make every event an Event class
                            HashMap <String,String> events = (HashMap<String, String>)data.get("Events");

                            String phoneNumber = (String) data.get("Phone_number");
                            String email = (String) data.get("Email");

                            // Create the User object
                            User user = new User(android_id,name, email, phoneNumber,events);
                        }
                    }
                } else {
                    Toast.makeText(UserActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

