package com.example.minion_project.user;
import com.example.minion_project.MainActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.provider.Settings.Secure;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.organizer.OrganizerActivity;
import com.example.minion_project.user.UserActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends Fragment {
    private TextView name;
    private TextView email;
    private TextView num;
    private TextView city;
    private Button save;
    private CheckBox changeRole;
    public FireStoreClass fire =new FireStoreClass();

    //get the android id of the current user
    private String android_id;
    public static UserSettingsFragment newInstance(String param1, String param2) {
        UserSettingsFragment fragment = new UserSettingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        num = view.findViewById(R.id.phone_number);
        city = view.findViewById(R.id.city);
        save = view.findViewById(R.id.save);
        changeRole = view.findViewById(R.id.role);
        String androidID = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);

        showExistingData(androidID);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save or update user information
                updateUserInfo(androidID);
            };

        });


        // Inflate the layout for this fragment
        return view;
    }

    private void updateUserInfo(String androidID) {
        // Retrieve the current values from the UI
        String nameV = name.getText().toString().trim();
        String emailV = email.getText().toString().trim();
        String phoneV = num.getText().toString().trim();
        String cityV = city.getText().toString().trim();

        // Validate if any of the fields are empty
        if (nameV.isEmpty()) {
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        if (emailV.isEmpty()) {
            Toast.makeText(getActivity(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        if (phoneV.isEmpty()) {
            Toast.makeText(getActivity(), "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        if (cityV.isEmpty()) {
            Toast.makeText(getActivity(), "City cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        // Prepare the userData to be updated in Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("City", cityV);
        userData.put("Email", emailV);
        userData.put("Name", nameV);
        userData.put("Phone_number", phoneV);

        Map<String, Object> Roles = new HashMap<>();
        Roles.put("Admin", false);  // Set the Admin role
        Roles.put("Organizer", changeRole.isChecked());  // Set Organizer role based on CheckBox
        Roles.put("User", true);  // Set User role

        // Add the Roles map to userData
        userData.put("Roles", Roles);

        // Update Firestore document with the new userData
        fire.getAll_UsersRef().document(androidID).set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Success message
                    Toast.makeText(getActivity(), "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failure handling
                    Toast.makeText(getContext(), "Error updating user information", Toast.LENGTH_SHORT).show();
                });
    }


    private void showExistingData(String androidID) {
        // Fetch the user document from Firestore using the Android ID
        fire.getAll_UsersRef().document(androidID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Extract data from the document and update the UI
                            String nameValue = document.getString("Name");
                            String emailValue = document.getString("Email");
                            String phoneValue = document.getString("Phone_number");
                            String cityValue = document.getString("City");
                            Boolean isOrganizer = document.getBoolean("Roles.Organizer");

                            // Set the UI elements with retrieved data
                            name.setText(nameValue);
                            email.setText(emailValue);
                            num.setText(phoneValue);
                            city.setText(cityValue);

                            // Check or uncheck the Organizer CheckBox
                            if (isOrganizer != null && isOrganizer) {
                                changeRole.setChecked(true);
                            } else {
                                changeRole.setChecked(false);
                            }
                        } else {
                            // Document does not exist
                            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle errors
                        Toast.makeText(getActivity(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}