package com.example.minion_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, cityEditText;
    private CheckBox organizerCheckBox, userCheckBox;
    private Button signupButton, setImageButton;
    private CollectionReference usersRef, allUsersRef, organizersRef;
    private ImageView profileImageView;
    private TextView userNotRecognized;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String androidId;

    private static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
    private static final int[] PROFILE_DRAWABLES = { R.drawable.baseline_account_circle_24 };

    public static SignUpFragment newInstance(String allUsersRefPath, String androidId,
                                             String usersRefPath, String organizersRefPath) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString("allUsersRefPath", allUsersRefPath); // Storing reference path as a string
        args.putString("usersRefPath", usersRefPath);       // Storing reference path as a string
        args.putString("organizersRefPath", organizersRefPath); // Storing reference path as a string
        args.putString("androidId", androidId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initializeViews(view);

        // Get Firestore reference paths from arguments
        Bundle args = getArguments();
        if (args != null) {
            String allUsersRefPath = args.getString("allUsersRefPath");
            String usersRefPath = args.getString("usersRefPath");
            String organizersRefPath = args.getString("organizersRefPath");
            androidId = args.getString("androidId");

            // Reconstructing CollectionReferences from paths
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            allUsersRef = db.collection(allUsersRefPath);
            usersRef = db.collection(usersRefPath);
            organizersRef = db.collection(organizersRefPath);
        }

        setImageButton.setOnClickListener(v -> openFileChooser());
        signupButton.setOnClickListener(v -> handleSignUp());
        return view;
    }

    private void initializeViews(View view) {
        nameEditText = view.findViewById(R.id.name);
        emailEditText = view.findViewById(R.id.email);
        profileImageView = view.findViewById(R.id.profileImageView);
        setImageButton = view.findViewById(R.id.setImageButton);
        userNotRecognized = view.findViewById(R.id.userNotRecognized);
        phoneEditText = view.findViewById(R.id.phone_number);
        cityEditText = view.findViewById(R.id.city);
        signupButton = view.findViewById(R.id.signup_button);
        organizerCheckBox = view.findViewById(R.id.organizer_checkbox);
        userCheckBox = view.findViewById(R.id.user_checkbox);
    }

    private void handleSignUp() {
        uploadImageToFirebase();
        randomizeProfile();
    }

    private void randomizeProfile() {
        // Randomize profile only if no image is selected
        if (imageUri == null) {
            Random random = new Random();
            profileImageView.setBackgroundColor(COLORS[random.nextInt(COLORS.length)]);
            profileImageView.setImageResource(PROFILE_DRAWABLES[random.nextInt(PROFILE_DRAWABLES.length)]);
        }
    }

    private void signUpUser(String imageUrl) {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        boolean organizerSelected = organizerCheckBox.isChecked();
        boolean userSelected = userCheckBox.isChecked();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Please, enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("city", city);
        userData.put("roles", createRolesMap(organizerSelected, userSelected));
        userData.put("imageUrl", imageUrl);
        userData.put("userId", androidId);

        // Add to Users Collection
        usersRef.document(androidId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Add to Organizers Collection if selected
                    if (organizerSelected) {
                        organizersRef.document(androidId).set(userData)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Add to All_Users Collection regardless of role
                                    allUsersRef.document(androidId).set(userData)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(getActivity(), "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                                navigateToRoleSelection();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Failed to add to All_Users collection", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to add to Organizers collection", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // If not an organizer, still add to All_Users
                        allUsersRef.document(androidId).set(userData)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(getActivity(), "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                    navigateToRoleSelection();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to add to All_Users collection", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Sign-up failed.", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToRoleSelection() {
        // Redirect to the Role Selection Fragment
        if (getActivity() != null) {
            MainActivity activity = (MainActivity) getActivity();
            activity.showRoleSelectionFragment();
        }
    }

    private Map<String, Boolean> createRolesMap(boolean organizer, boolean user) {
        Map<String, Boolean> roles = new HashMap<>();
        if (organizer) roles.put("Organizer", true);
        if (user) roles.put("User", true);
        return roles;
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Upload the image to Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profiles/" + androidId + ".jpg");
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL after successful upload
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    signUpUser(uri.toString());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to retrieve image URL.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Image upload failed.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            signUpUser(null);  // No image uploaded
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}
