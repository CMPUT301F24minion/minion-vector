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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class OrganizerFacility extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText facilityNameInput;
    private ImageView facilityImagePreview;
    private LinearLayout saveButton, selectImageButton;

    private String existingImageURL;
    private Uri imageUri;
    private FireStoreClass firestore = new FireStoreClass();
    private OrganizerController organizerController;

    public OrganizerFacility(OrganizerController organizerController) {
        this.organizerController = organizerController;
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facility_info, container, false);

        facilityNameInput = view.findViewById(R.id.facilityName);
        facilityImagePreview = view.findViewById(R.id.facilityPhoto);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        saveButton = view.findViewById(R.id.saveFacilityButton);

        selectImageButton.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> saveFacilityDetails());

        loadExistingFacilityData();

        return view;
    }

    private void loadExistingFacilityData() {
        String organizerID = organizerController.getOrganizer().getDeviceID();
        firestore.getFirestore().collection("Facility")
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
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load facility data", Toast.LENGTH_SHORT).show());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Facility Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            facilityImagePreview.setImageURI(imageUri);
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
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("Facility/" + organizerID + "_facility.jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        saveFacilityDataToFirestore(facilityName, downloadUri.toString());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else if (existingImageURL != null) {
            saveFacilityDataToFirestore(facilityName, existingImageURL);
        } else {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFacilityDataToFirestore(String facilityName, String facilityImageURL) {
        String organizerID = organizerController.getOrganizer().getDeviceID();
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityID", facilityName);
        facilityData.put("facilityImage", facilityImageURL);

        firestore.getFirestore().collection("Facility")
                .document(organizerID)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    firestore.getOrganizersRef().document(organizerID)
                            .update("Facility", true)
                            .addOnSuccessListener(updateVoid -> {
                                Toast.makeText(getContext(), "Facility details saved successfully!", Toast.LENGTH_SHORT).show();
                                navigateBackToCreateEvent();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save facility details", Toast.LENGTH_SHORT).show());
    }

    private void navigateBackToCreateEvent() {
        // Navigate back to OrganizerActivity with intent
        Intent intent = new Intent(requireActivity(), OrganizerActivity.class);
        intent.putExtra("navigate_to", "create_event"); // Pass the target fragment
        startActivity(intent);
        requireActivity().finish();
    }
}
