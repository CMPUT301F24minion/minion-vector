package com.example.minion_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText,cityEditText;
    private CheckBox organizerCheckBox, userCheckBox;
    private Button signupButton;
    private CollectionReference usersRef;
    private String android_id;
    // instantiate a fragment and set the arguments passed
    public static  SignUpFragment newInstance(CollectionReference usersRef,String android_id){
        SignUpFragment fragment=new SignUpFragment();
        Bundle args =new Bundle();
        fragment.setArguments(args);
        fragment.usersRef=usersRef;
        fragment.android_id=android_id;
        return  fragment;
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        nameEditText = view.findViewById(R.id.name);
        emailEditText = view.findViewById(R.id.email);
        phoneEditText = view.findViewById(R.id.phone_number);
        cityEditText=view.findViewById(R.id.city);
        signupButton = view.findViewById(R.id.signup_button);
        organizerCheckBox=view.findViewById(R.id.organizer_checkbox);
        userCheckBox=view.findViewById(R.id.user_checkbox);
        signupButton.setOnClickListener(v -> signUpUser());

        return view;
    }

    private void signUpUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();

        Boolean organizerSelected=organizerCheckBox.isChecked();
        Boolean userSelected=userCheckBox.isChecked();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Please, enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(organizerSelected || userSelected)) {
            Toast.makeText(getActivity(), "You forgot to select your role!", Toast.LENGTH_SHORT).show();
        }


        Map<String, Boolean> roles=new HashMap<>();
        roles.put("Admin",Boolean.FALSE);
        roles.put("Organizer",organizerSelected);
        roles.put("User",userSelected);

        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        user.put("Email", email);
        user.put("Phone_number",phone);
        user.put("City",city);
        user.put("Roles",roles);

        usersRef.document(android_id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                        if (organizerSelected){
                            // launch organizer activity
                             ;
                        } else if (userSelected) {
                            // launch user activity
                            startActivity(new Intent(getActivity(), UserActivity.class));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing document", e);
                    }
                });

    }


}
