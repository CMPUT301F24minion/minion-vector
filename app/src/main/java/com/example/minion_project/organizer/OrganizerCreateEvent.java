package com.example.minion_project.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.minion_project.Event;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.OrganizerController;
import com.example.minion_project.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class OrganizerCreateEvent extends Fragment {

    private Button selectTime, selectDate, uploadImage, createEventButton;
    private EditText createEventTitle, createEventDetails, createEventInvitations;
    private FireStoreClass ourFirestore = new FireStoreClass();
    private String selectedDate = "";
    private String selectedTime = "";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView profileImageView;

    // contoller
    private OrganizerController organizerController;
    //initalize controller
    public OrganizerCreateEvent(OrganizerController organizercontroller){
        this.organizerController=organizercontroller;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_create_event, container, false);


        // Find views by ID
        selectTime = view.findViewById(R.id.selectTimeButton);
        selectDate = view.findViewById(R.id.selectDateButton);
        uploadImage = view.findViewById(R.id.uploadImageButton);
        createEventButton = view.findViewById(R.id.createEventButton);
        createEventTitle = view.findViewById(R.id.createEventTitleEditText);
        createEventDetails = view.findViewById(R.id.createEventDetailsEditText);
        createEventInvitations = view.findViewById(R.id.createEventInvitationsEditText);
        profileImageView = view.findViewById(R.id.profileImageView);
        // Set listeners for buttons
        selectTime.setOnClickListener(v -> openTimePickerDialog());
        selectDate.setOnClickListener(v -> openDatePickerDialog());
        uploadImage.setOnClickListener(v -> uploadImage());
        // Change the button click listener for creating an event to start with the image upload
        createEventButton.setOnClickListener(v -> uploadImageToFirebaseAndCreateEvent());


        return view;
    }

    private void openTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    selectedTime = hourOfDay + ":" + String.format("%02d", minuteOfHour);
                    Toast.makeText(getContext(), "Selected Time: " + selectedTime, Toast.LENGTH_SHORT).show();
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void openDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void uploadImage() {
        Toast.makeText(getContext(), "Upload Image clicked!", Toast.LENGTH_SHORT).show();
        // Placeholder for image upload logic
        openFileChooser();

    }

    private void createEvent(String imageUrl) {
        String eventTitle = createEventTitle.getText().toString().trim();
        String eventDetails = createEventDetails.getText().toString().trim();
        String eventInvitations = createEventInvitations.getText().toString().trim();

        if (eventTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an event title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(getContext(), "Please select date and time for the event", Toast.LENGTH_SHORT).show();
            return;
        }

        Event newEvent = new Event();
        newEvent.setEventName(eventTitle);
        newEvent.setEventDetails(eventDetails);
        newEvent.setEventCapacity(eventInvitations);
        newEvent.setEventDate(selectedDate);
        newEvent.setEventTime(selectedTime);
        newEvent.setEventImage(imageUrl);
        newEvent.setEventOrganizer(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

        // TODO : ON CLICK WE SHOULD OPEN THE EVENT PAGE WITH THE QR OR REDIRECT TOT MY EVENTS PAGE
        //         : AND WE MUST CLEAR THE INPUT TEXT

        CollectionReference eventsRef = ourFirestore.getEventsRef();
        eventsRef.add(newEvent)
                .addOnSuccessListener(documentReference ->{
                            String eventId = documentReference.getId();

                            // we pass the event to controller
                            // the contoller updates both firestore and organizer class
                            organizerController.addEvent(eventId);
                            Log.d("OrganizerCreateEvent", "Event ID: " + eventId);
                            Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();

                        // Clear inputs or redirect to another page if necessary
                        clearInputs();

                        })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the selected image

            // Upload the image to Firebase Storage and update Firestore with the new URL
            String androidID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    private void uploadImageToFirebaseAndCreateEvent() {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Use a unique file name for each event image to avoid overwriting files
            String imageFileName = "event_images/" + System.currentTimeMillis() + ".jpg";
            StorageReference storageRef = storage.getReference(imageFileName);

            // Upload the image file to Firebase Storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Retrieve the download URL
                        String downloadUrl = uri.toString();

                        // Call the method to create an event and pass the download URL
                        createEvent(downloadUrl);

                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveProfileImageUrlToFirestore(String androidID, String downloadUrl) {
        // Update the profile image URL in getAll_UsersRef
        ourFirestore.getAll_UsersRef().document(androidID)
                .update("profileImage", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Display the new image in the ImageView
                    Glide.with(getActivity()).load(downloadUrl).into(profileImageView);
                });
    }

    private void clearInputs() {
        createEventTitle.setText("");
        createEventDetails.setText("");
        createEventInvitations.setText("");
        profileImageView.setImageURI(null);  // Clear the image preview
        selectedDate = "";
        selectedTime = "";
    }

}