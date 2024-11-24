package com.example.minion_project.user;
import com.example.minion_project.MainActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.Settings.Secure;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Manages user settings, profile updates and role changes
 */
public class UserSettingsFragment extends Fragment {
    private TextView name;
    private TextView email;
    private TextView num;
    private TextView city;
    private Button save;
    private CheckBox changeRole;
    public FireStoreClass fire =new FireStoreClass();

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private ImageView profileImageView;
    private Button changeImageButton;
    private Button removeImageButton;

    private SwitchCompat notificationToggle;

    /**
     * Create a new instance of UserSettingsFragment
     * @param param1
     * @param param2
     * @return new instance of UserSettingsFragment
     */
    public static UserSettingsFragment newInstance(String param1, String param2) {
        UserSettingsFragment fragment = new UserSettingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    /**
     * Called when fragment created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout and initializes the views for the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view for fragments UI
     */
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
        profileImageView = view.findViewById(R.id.profileImageView);
        changeImageButton = view.findViewById(R.id.changeImageButton);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        notificationToggle = view.findViewById(R.id.notificationToggle);

        String androidID = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);

        showExistingData(androidID);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save or update user information
                updateUserInfo(androidID);
            };

        });
        changeImageButton.setOnClickListener(view12 -> openFileChooser()); // Opens the gallery to pick a new image

        removeImageButton.setOnClickListener(view13 -> removeProfileImage(androidID)); // Removes the profile image

        notificationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateAllowNotification(androidID, isChecked);
        });


        // Inflate the layout for this fragment
        return view;
    }

    // The updateAllowNotification method
    private void updateAllowNotification(String androidID, boolean isChecked) {
        Map<String, Object> data = new HashMap<>();
        data.put("allowNotifications", isChecked);

        fire.getAll_UsersRef().document(androidID)
                .update(data);
    }

    /**
     * Updates user info in firebase
     * @param androidID the unique devide ID of a user
     */
    private void updateUserInfo(String androidID) {
        // Retrieve the current values from the UI
        String nameV = name.getText().toString().trim();
        String emailV = email.getText().toString().trim();
        String phoneV = num.getText().toString().trim();
        String cityV = city.getText().toString().trim();
        boolean notif = notificationToggle.isChecked();

        // Validate if any of the fields are empty
        if (nameV.isEmpty()) {
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailV.isEmpty()) {
            Toast.makeText(getActivity(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneV.isEmpty()) {
            Toast.makeText(getActivity(), "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cityV.isEmpty()) {
            Toast.makeText(getActivity(), "City cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the user data to be updated in Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("City", cityV);
        userData.put("Email", emailV);
        userData.put("Name", nameV);
        userData.put("Phone_number", phoneV);
        userData.put("allowNotifications", notif);

        Map<String, Object> Roles = new HashMap<>();
        Roles.put("Admin", false);
        Roles.put("Organizer", changeRole.isChecked());
        Roles.put("User", true);

        userData.put("Roles", Roles);

        // Check if the profileImage field exists in Firestore
        fire.getAll_UsersRef().document(androidID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getString("profileImage") == null) {
                        // If no profile image exists, generate one with the first letter of the name
                        char firstLetter = nameV.charAt(0);
                        generateLetterProfile(androidID, firstLetter, userData);
                    } else {
                        // Update Firestore directly without generating an image
                        fire.getAll_UsersRef().document(androidID).set(userData);
                    }
                });
    }

    private void generateLetterProfile(String androidID, char firstLetter, Map<String, Object> userData) {
        // Create a Bitmap with the first letter
        int imageSize = 200; // Size of the profile picture
        Bitmap bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Set background color and draw text
        canvas.drawColor(Color.WHITE); // Background color
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(100); // Text size
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        // Draw the letter in the center of the canvas
        float x = imageSize / 2f;
        float y = imageSize / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f;
        canvas.drawText(String.valueOf(firstLetter).toUpperCase(), x, y, paint);

        // Convert Bitmap to ByteArray
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the generated image to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("profile_images/" + androidID + ".jpg");

        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    // Update Firestore with the new profile image URL in All_UsersRef
                    userData.put("profileImage", downloadUrl);
                    fire.getAll_UsersRef().document(androidID).set(userData);

                    // Update Firestore with the new profile image URL in UsersRef
                    fire.getUsersRef().document(androidID).update("profileImage", downloadUrl);
                }));
    }





    /**
     * Gets existing data from Firestore and updates the UI to display
     * @param androidID the unique devide ID of a user
     */
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
                            String profileImageUrl = document.getString("profileImage"); // Get the profile image URL
                            Boolean showNotifications = document.getBoolean("allowNotifications");

                            // Set the UI elements with retrieved data
                            name.setText(nameValue);
                            email.setText(emailValue);
                            num.setText(phoneValue);
                            city.setText(cityValue);
                            notificationToggle.setChecked(Boolean.TRUE.equals(showNotifications));

                            // Check or uncheck the Organizer CheckBox
                            if (isOrganizer != null && isOrganizer) {
                                changeRole.setChecked(true);
                            } else {
                                changeRole.setChecked(false);
                            }

                            // Load the profile image using Glide
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(requireActivity())
                                        .load(profileImageUrl)
                                        .circleCrop()
                                        .into(profileImageView); // Replace with your actual ImageView ID
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

    /**
     * Gallery opener to pick a new image
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the file chooser, uploads the selected image to Firebase
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the selected image

            // Upload the image to Firebase Storage and update Firestore with the new URL
            String androidID = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
            uploadImageToFirebase(androidID);
        }
    }

    /**
     * Removes the profile image from Firebase Storage and Firestore
     * @param androidID the unique devide ID of a user
     */
    private void removeProfileImage(String androidID) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("profile_images/" + androidID + ".jpg");

        // Delete the image from Firebase Storage
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the profile image URL from getAll_UsersRef
                    fire.getAll_UsersRef().document(androidID).update("profileImage", null)
                            .addOnSuccessListener(aVoid1 -> {
                                profileImageView.setImageResource(R.drawable.baseline_account_circle_24);
                            });

                    // Remove the profile image URL from getUsersRef
                    fire.getUsersRef().document(androidID).update("profileImage", null)
                            .addOnSuccessListener(aVoid2 -> {
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete image from Storage", Toast.LENGTH_SHORT).show());
    }

    /**
     * Uploads the selected image to Firebase Storage and updates Firestore with the new URL
     * @param androidID the unique devide ID of a user
     */
    private void uploadImageToFirebase(String androidID) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("profile_images/" + androidID + ".jpg");

            // Upload the image file to Firebase Storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        // Save the new profile image URL to Firestore in both references
                        saveProfileImageUrlToFirestore(androidID, downloadUrl);
                        Toast.makeText(getActivity(), "Profile image updated successfully!", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileImageUrlToFirestore(String androidID, String downloadUrl) {
        // Prepare update tasks for both collections
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("profileImage", downloadUrl);

        // Update the profile image URL in All_UsersRef
        fire.getAll_UsersRef().document(androidID)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated in All_UsersRef
                    Glide.with(getActivity()).load(downloadUrl).into(profileImageView);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });

        // Update the profile image URL in UsersRef
        fire.getUsersRef().document(androidID)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated in UsersRef
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

}