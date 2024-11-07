package com.example.minion_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.provider.Settings.Secure;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.organizer.OrganizerActivity;
import com.example.minion_project.user.UserActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CollectionReference usersRef, allUsersRef, organizersRef;
    private String android_id;
    public FireStoreClass Our_Firestore = new FireStoreClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore collection references
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        allUsersRef = db.collection("All_Users");
        usersRef = db.collection("Users");
        organizersRef = db.collection("Organizers");

        // Get device ID
        android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        // Show loading fragment
        showProgressFragment();

        // Check user status
        checkUserStatus(android_id);
    }

    public void showSignUpFragment() {
        SignUpFragment signUpFragment = SignUpFragment.newInstance("All_Users", android_id, "Users", "Organizers");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, signUpFragment);
        transaction.commit();
    }

    private void showProgressFragment() {
        ProgressFragment progressFragment = new ProgressFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, progressFragment);
        transaction.commit();
    }

    private void checkUserStatus(String deviceId) {
        allUsersRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // User is recognized
                        String userName = document.getString("name");
                        showWelcomeKnownUserFragment(userName);
                    } else {
                        // New user, show sign-up form
                        showWelcomeUnknownUserFragment();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showWelcomeKnownUserFragment(String userName) {
        WelcomeKnownUserFragment fragment = WelcomeKnownUserFragment.newInstance(userName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, fragment);
        transaction.commit();
    }

    private void showWelcomeUnknownUserFragment() {
        WelcomeUnknownUserFragment fragment = WelcomeUnknownUserFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, fragment);
        transaction.commit();
    }

    public void showRoleSelectionFragment() {
        RoleSelectionFragment fragment = RoleSelectionFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, fragment);
        transaction.commit();
    }

    public void navigateToAttendeeActivity() {
        startActivity(new Intent(this, UserActivity.class));
    }

    public void navigateToOrganizerActivity() {
        startActivity(new Intent(this, OrganizerActivity.class));
    }
}
