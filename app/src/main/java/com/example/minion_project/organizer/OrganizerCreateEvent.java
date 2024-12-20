package com.example.minion_project.organizer;

import static java.lang.Boolean.FALSE;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * OrganizerCreateEvent is a fragment that allows an organizer to create a new event.
 */
public class OrganizerCreateEvent extends Fragment {
    private static final String TAG = "OrganizerCreateEvent";
    private static final int QR_SIZE = 100;

    private LinearLayout selectTime, selectDate, uploadImage, createEventButton;
    private EditText createEventTitle, createEventDetails, createEventInvitations;
    private FireStoreClass ourFirestore = new FireStoreClass();
    private String selectedDate = "";
    private String selectedTime = "";
    private ImageView eventImageView;
    private LinearLayout editFacilityButton;
    private Organizer Organizer;
    // Controller
    private OrganizerController organizerController;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    /**
     * Constructor for OrganizerCreateEvent
     * @param organizercontroller OrganizerController
     */
    public OrganizerCreateEvent(OrganizerController organizercontroller){
        this.organizerController = organizercontroller;
        this.Organizer = organizercontroller.getOrganizer();
    }

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_organizer_create_event, container, false);

        // Check if the facilityID is empty
        if (!organizerController.getOrganizer().getFacility()) {

            // Redirect to FacilityFragment
            OrganizerFacility facilityFragment = new OrganizerFacility(organizerController);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(container.getId(), facilityFragment)
                    .addToBackStack(null) // Add this transaction to the back stack
                    .commit();
            if (getActivity() instanceof OrganizerActivity) {
                ((OrganizerActivity) getActivity()).updateHeaderText("My Facility");
            }
        }

        // Proceed with setting up views if facilityID is not empty
        selectTime = view.findViewById(R.id.selectTimeButton);
        selectDate = view.findViewById(R.id.selectDateButton);
        uploadImage = view.findViewById(R.id.uploadImageButton);
        createEventButton = view.findViewById(R.id.createEventButton);
        createEventTitle = view.findViewById(R.id.createEventTitleEditText);
        createEventDetails = view.findViewById(R.id.createEventDetailsEditText);
        createEventInvitations = view.findViewById(R.id.createEventInvitationsEditText);
        eventImageView = view.findViewById(R.id.eventImageView);
        editFacilityButton = view.findViewById(R.id.editFacilityButton);

        // Set up listeners for buttons
        selectTime.setOnClickListener(v -> openTimePickerDialog());
        selectDate.setOnClickListener(v -> openDatePickerDialog());
        uploadImage.setOnClickListener(v -> uploadImage());
        createEventButton.setOnClickListener(v -> createEvent());

        editFacilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OrganizerActivity.class);
            intent.setAction("OPEN_FACILITY_FRAGMENT");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (getActivity() instanceof OrganizerActivity) {
                ((OrganizerActivity) getActivity()).updateHeaderText("My Facility");
            }
        });

        return view;
    }

    /**
     * Opens a time picker dialog
     */
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

    /**
     * Opens a date picker dialog
     */
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

    /**
     * Opens a file chooser to select an image
     */
    private void uploadImage() {
        openFileChooser();
    }

    /**
     * Creates an event
     */
    private void createEvent() {
        String eventTitle = createEventTitle.getText().toString().trim();
        String eventDetails = createEventDetails.getText().toString().trim();
        Integer eventInvitations = 10;

        String invitationsString = createEventInvitations.getText().toString().trim();
        try {
            eventInvitations = Integer.parseInt(invitationsString);
        } catch (NumberFormatException e) {
            // Handle exception if needed
        }
        String time = selectedTime;
        String date = selectedDate;

        if (eventTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an event title", Toast.LENGTH_SHORT).show();
            return;
        } else if (eventDetails.isEmpty()) {
            Toast.makeText(getContext(), "Please enter details to the event", Toast.LENGTH_SHORT).show();
            return;
        }
        // COMMENTED OUT TO DEVELOP EASIER
        // else if (facility.isEmpty()) {
        //     Toast.makeText(getContext(), "Please enter the name of the facility", Toast.LENGTH_SHORT).show();
        //     return;
        // } else if (time.isEmpty()) {
        //     Toast.makeText(getContext(), "Please enter the time the event starts", Toast.LENGTH_SHORT).show();
        //     return;
        // } else if (date.isEmpty()) {
        //     Toast.makeText(getContext(), "Please enter the date the event starts", Toast.LENGTH_SHORT).show();
        // }
        Event event = new Event();
        event.setEventName(eventTitle);
        event.setEventDetails(eventDetails);
        event.setEventCapacity(eventInvitations);
        event.setEventDate(selectedDate);
        event.setEventTime(selectedTime);
        event.setEventOrganizer(Organizer.getDeviceID());
        event.setEventStart(FALSE);

        CollectionReference eventsRef = ourFirestore.getEventsRef();
        eventsRef.add(event)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    event.setEventID(eventId);
                    // We pass the event to controller
                    // The controller updates both Firestore and organizer class
                    organizerController.addEvent(eventId);
                    Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                    uploadImageToFirebaseAndCreateEvent(eventId);
                    clearInputs();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Updates the facility name in Firestore
     * @param facility The new facility name
     */
    private void updateOrganizerFacilityName(String facility) {
        String organizerID = Organizer.getDeviceID();
        ourFirestore.getOrganizersRef().document(organizerID)
                .update("facilityName", facility)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility name updated successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update facility name: " + e.getMessage()));
    }

    /**
     * Generates and uploads a QR code for the event
     * @param qrContent
     */
    private void generateAndUploadQRCode(String qrContent) {
        // qrContent is the eventID
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
                        saveToFirestore("eventQrCode", qrContent, qrCodeUrl);

                    }))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload QR code: " + e.getMessage()));
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code: " + e.getMessage());
        }
    }

    /**
     * Opens a file chooser to select an image
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select event Poster"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the activity
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
            eventImageView.setImageURI(imageUri);  // Display the selected image
        }
    }

    /**
     * Uploads the image to Firebase Storage and creates the event
     * @param eventId The ID of the event
     */
    private void uploadImageToFirebaseAndCreateEvent(String eventId) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String imageFileName = "event_images/" + eventId + ".jpg";
            StorageReference storageRef = storage.getReference(imageFileName);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(imageUri -> {
                        String downloadUrl = imageUri.toString();
                        saveToFirestore("eventImage", eventId, downloadUrl);
                        // Now generate and upload QR code
                        generateAndUploadQRCode(eventId);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            generateAndUploadQRCode(eventId);
        }
    }

    /**
     * Saves the URL to Firestore
     * @param key The key to be used in the Firestore document
     * @param eventId The ID of the event
     * @param Url The URL to be saved
     */
    private void saveToFirestore(String key, String eventId, String Url) {
        CollectionReference eventsRef = ourFirestore.getEventsRef();

        // Update the event document with the qrCodeUrl
        eventsRef.document(eventId).update(key, Url)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Saved to Firestore successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save URL to Firestore: " + e.getMessage()));
        eventsRef.document(eventId).update("eventID", eventId);
    }

    /**
     * Clears the input fields
     */
    private void clearInputs() {
        createEventTitle.setText("");
        createEventDetails.setText("");
        createEventInvitations.setText("");
        eventImageView.setImageURI(null);  // Clear the image preview
        selectedDate = "";
        selectedTime = "";
    }

}
