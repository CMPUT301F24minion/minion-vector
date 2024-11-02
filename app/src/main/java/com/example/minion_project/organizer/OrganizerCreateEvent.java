/**
 * OrganizerCreateEvent.java
 *
 * Allows organizers to create new events
 * Provides a user interface for specifying event details, including the
 * title, description, date, time, and capacity, as well as an option to upload an event
 * image. This fragment integrates with Firebase Firestore for event storage and utilizes
 * an OrganizerController to manage organizer-related actions.
 *
 * Design Pattern: This class follows a Model-View-Controller (MVC) pattern, where it
 * represents the "View" component, interacting with the OrganizerController (the "Controller")
 * and Event (the "Model") for data handling and business logic.
 *
 * Outstanding Issues:
 * - The image upload functionality is currently a placeholder and needs to be implemented.
 * - The event creation process lacks input validation for certain fields (e.g., date and time).
 */

package com.example.minion_project.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.minion_project.Event;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.OrganizerController;
import com.example.minion_project.R;
import com.google.firebase.firestore.CollectionReference;

import java.util.Calendar;

public class OrganizerCreateEvent extends Fragment {

    private Button selectTime, selectDate, uploadImage, createEventButton;
    private EditText createEventTitle, createEventDetails, createEventInvitations;
    private FireStoreClass ourFirestore = new FireStoreClass();
    private String selectedDate = "";
    private String selectedTime = "";

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

        // Set listeners for buttons
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
        Toast.makeText(getContext(), "Upload Image clicked!", Toast.LENGTH_SHORT).show();
        // Placeholder for image upload logic
    }

    private void createEvent() {
        String eventTitle = createEventTitle.getText().toString().trim();
        String eventDetails = createEventDetails.getText().toString().trim();
        String eventInvitations = createEventInvitations.getText().toString().trim();

        if (eventTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an event title", Toast.LENGTH_SHORT).show();
            return;
        }
//
//        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
//            Toast.makeText(getContext(), "Please select date and time for the event", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Event newEvent = new Event();
        newEvent.setEventName(eventTitle);
        newEvent.setEventDetails(eventDetails);
        newEvent.setEventCapacity(eventInvitations);
        newEvent.setEventDate(selectedDate);
        newEvent.setEventTime(selectedTime);

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

                        })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}