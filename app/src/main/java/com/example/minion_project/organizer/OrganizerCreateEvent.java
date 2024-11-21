/**
 * Fragment handles the display of one of the organizer functions (generating an event)
 * Displays the create event layout and maintains all input handling features and
 * communication (via storing) with the database (text upload, image upload, data selection)
 * Has connectings to the users, organizers, events collections
 */

package com.example.minion_project.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.example.minion_project.events.Event;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;


public class OrganizerCreateEvent extends Fragment {
    private static final String TAG = "OrganizerCreateEvent";
    private static final int QR_SIZE = 100;

    private Button selectTime, selectDate, uploadImage, createEventButton;
    private EditText createEventTitle, createEventDetails, createEventInvitations, facilityName;
    private FireStoreClass ourFirestore = new FireStoreClass();
    private String selectedDate = "";
    private String selectedTime = "";
    private ImageView eventImageView;
    private Organizer Organizer;
    // contoller
    private OrganizerController organizerController;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;
    //initalize controller
    public OrganizerCreateEvent(OrganizerController organizercontroller){
        this.organizerController=organizercontroller;
        this.Organizer=organizercontroller.getOrganizer();
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
        eventImageView = view.findViewById(R.id.eventImageView);
        facilityName = view.findViewById(R.id.facilityName);
        // Set listeners for buttons

        if (organizerController.getOrganizer() != null) {
            facilityName.setText(organizerController.getOrganizer().getFacilityName());
        }

        selectTime.setOnClickListener(v -> openTimePickerDialog());
        selectDate.setOnClickListener(v -> openDatePickerDialog());
        uploadImage.setOnClickListener(v -> uploadImage());
        createEventButton.setOnClickListener(v -> createEvent());

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
        openFileChooser();
    }

    /**
     * Creates an event
     *
     *
     */
    private void createEvent() {
        String eventTitle = createEventTitle.getText().toString().trim();
        String eventDetails = createEventDetails.getText().toString().trim();
        Integer eventInvitations = 10;

        String invitationsString = createEventInvitations.getText().toString().trim();
        try {
            eventInvitations = Integer.parseInt(invitationsString);
        } catch (NumberFormatException e) {

        }
        String facility = facilityName.getText().toString().trim();
        String time = selectedTime;
        String date = selectedDate;

        if (eventTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an event title", Toast.LENGTH_SHORT).show();
            return;
        } else if (eventDetails.isEmpty()) {
            Toast.makeText(getContext(), "Please enter details to the event", Toast.LENGTH_SHORT).show();
            return;
        }
        //COMMENTED OUT TO DEVELOP EASIER
//        else if (facility.isEmpty()) {
//            Toast.makeText(getContext(), "Please enter the name of the facility", Toast.LENGTH_SHORT).show();
//            return;
//        }else if (time.isEmpty()) {
//            Toast.makeText(getContext(), "Please enter the time the event starts", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (date.isEmpty()) {
//            Toast.makeText(getContext(), "Please enter the date the event starts", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Event event = new Event();
        event.setEventName(eventTitle);
        event.setEventDetails(eventDetails);
        event.setEventCapacity(eventInvitations);
        event.setEventDate(selectedDate);
        event.setEventTime(selectedTime);
        event.setEventOrganizer(Organizer.getDeviceID());
        event.setFacilityName(facility);

        updateOrganizerFacilityName(facility);



        CollectionReference eventsRef = ourFirestore.getEventsRef();
        eventsRef.add(event)
                .addOnSuccessListener(documentReference ->{
                    String eventId = documentReference.getId();
                    event.setEventID(eventId);
                    // we pass the event to controller
                    // the controller updates both firestore and organizer class
                    organizerController.addEvent(eventId);
                    Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                    uploadImageToFirebaseAndCreateEvent(eventId);
                    clearInputs();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    private void updateOrganizerFacilityName(String facility) {
        String organizerID = Organizer.getDeviceID(); // Assuming this is the ID used in Firestore
        ourFirestore.getOrganizersRef().document(organizerID)
                .update("facilityName", facility)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility name updated successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update facility name: " + e.getMessage()));
    }

    private void generateAndUploadQRCode(String qrContent) {
        // qrcontent is the eventID
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            // Convert bitmap to a Firebase-friendly format and upload
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Save QR code with a unique filename
            StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference("qr_codes/" + qrContent + ".png");
            qrCodeRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(qrUri -> {
                        String qrCodeUrl = qrUri.toString();
                        saveToFirestore("eventQrCode",qrContent,qrCodeUrl);

                    }))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload QR code: " + e.getMessage()));
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code: " + e.getMessage());
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select event Poster"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            eventImageView.setImageURI(imageUri);  // Display the selected image
        }
    }

    private void uploadImageToFirebaseAndCreateEvent(String eventId) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String imageFileName = "event_images/" + eventId + ".jpg";
            StorageReference storageRef = storage.getReference(imageFileName);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(imageUri -> {
                        String downloadUrl = imageUri.toString();
                        saveToFirestore("eventImage",eventId,downloadUrl);
                        // Now generate and upload QR code
                        generateAndUploadQRCode(eventId);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            generateAndUploadQRCode( eventId);

        }
    }
    private void saveToFirestore(String key,String eventId, String Url) {
        CollectionReference eventsRef = ourFirestore.getEventsRef();

        // Update the event document with the qrCodeUrl
        eventsRef.document(eventId).update(key, Url)
                .addOnSuccessListener(aVoid -> Log.d(TAG, " saved to Firestore successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save URL to Firestore: " + e.getMessage()));
        eventsRef.document(eventId).update("eventID", eventId);
    }
    private void clearInputs() {
        createEventTitle.setText("");
        createEventDetails.setText("");
        createEventInvitations.setText("");
        eventImageView.setImageURI(null);  // Clear the image preview
        selectedDate = "";
        selectedTime = "";
    }

}