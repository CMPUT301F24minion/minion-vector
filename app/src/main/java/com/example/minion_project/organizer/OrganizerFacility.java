package com.example.minion_project.organizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrganizerFacility extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection

    private EditText facilityNameInput;
    private ImageView facilityImagePreview;
    private Button saveButton, selectImageButton;

    private String existingImageURL; // Existing image URL if any

    private Uri imageUri; // URI for selected image
    private FireStoreClass ourFirestore = new FireStoreClass();
    private OrganizerController organizerController;

    public OrganizerFacility(OrganizerController organizerController) {
        this.organizerController = organizerController;
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facility_info, container, false);

        // Find views
        facilityNameInput = view.findViewById(R.id.facilityName);
        facilityImagePreview = view.findViewById(R.id.facilityPhoto);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        saveButton = view.findViewById(R.id.saveFacilityButton);

        // Set click listeners
        selectImageButton.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> saveFacilityDetails());

        loadExistingFacilityData();

        return view;
    }

    private void loadExistingFacilityData() {
        String organizerID = organizerController != null ? organizerController.getOrganizer().getDeviceID() : null;

        if (organizerID == null) {
            Toast.makeText(getContext(), "Organizer ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        ourFirestore.getFirestore().collection("Facility")
                .document(organizerID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facilityName = documentSnapshot.getString("facilityID");
                        existingImageURL = documentSnapshot.getString("facilityImage");

                        if (facilityName != null) {
                            facilityNameInput.setText(facilityName);
                        }

                        if (existingImageURL != null) {
                            Glide.with(this)
                                    .load(existingImageURL)
                                    .placeholder(R.drawable.baseline_image_24)
                                    .into(facilityImagePreview);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load facility data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Method to open the image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Facility Image"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image chooser
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Set the image URI
            facilityImagePreview.setImageURI(imageUri); // Display the selected image in the ImageView
        }
    }

    private void saveFacilityDetails() {
        String facilityName = facilityNameInput.getText().toString().trim();

        if (facilityName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a facility name", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerID = organizerController.getOrganizer().getDeviceID();

        if (imageUri != null) {
            // User selected a new image, upload it
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("Facility/" + organizerID + "_facility.jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String facilityImageURL = downloadUri.toString();

                        // Save facility data
                        saveFacilityDataToFirestore(facilityName, facilityImageURL);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else if (existingImageURL != null) {
            // No new image selected, use existing image URL
            saveFacilityDataToFirestore(facilityName, existingImageURL);
        } else {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFacilityDataToFirestore(String facilityName, String facilityImageURL) {
        String organizerID = organizerController.getOrganizer().getDeviceID();

        // Prepare the data to be saved
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityID", facilityName);
        facilityData.put("facilityImage", facilityImageURL);

        // Save facility details to Facilities collection
        ourFirestore.getFirestore().collection("Facility")
                .document(organizerID)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    // Update the "facility" field in the organizer's document to true
                    ourFirestore.getOrganizersRef().document(organizerID)
                            .update("Facility", true)
                            .addOnSuccessListener(updateVoid -> {
                                Toast.makeText(getContext(), "Facility details saved successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate back to OrganizerCreateEvent fragment
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.frameLayoutOrganizer, new OrganizerCreateEvent(organizerController))
                                        .commit();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update organizer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





}
