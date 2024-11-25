package com.example.minion_project.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.minion_project.Lottery.Lottery;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for image picker
    private Uri newImageUri;  // To store the selected image URI

    ImageView eventImage;
    TextView eventNameTextView;
    TextView eventDateTextView;
    TextView eventTimeTextView;
    TextView eventDescriptionTextView;
    TextView eventCapacityTextView;
    TextView eventWaitlistCount ;
    TextView eventAcceptedCount ;
    TextView eventDeclinedCount ;
    TextView eventPendingCount ;
    EditText eventNumberOfApplicants;
    Button eventRunLottery;
    Button removeImageButton;
    EventController eventController;

    public EventDetailFragment() {
        this.eventController=new EventController();
    }

    public static EventDetailFragment newInstance(String param1, String param2) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private Event event;
    private Lottery lottery;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        // Use the 'event' object to populate the UI
        eventImage = view.findViewById(R.id.eventImage);
        eventNameTextView = view.findViewById(R.id.eventTitle);
        eventDateTextView = view.findViewById(R.id.eventDate);
        eventTimeTextView = view.findViewById(R.id.eventTime);
        eventDescriptionTextView = view.findViewById(R.id.eventDescription);
        eventCapacityTextView = view.findViewById(R.id.eventCapacity);
        eventRunLottery=view.findViewById(R.id.eventRunLottery);
        eventWaitlistCount = view.findViewById(R.id.eventWaitlistcount);
        eventAcceptedCount = view.findViewById(R.id.eventAcceptedCount);
        eventDeclinedCount = view.findViewById(R.id.eventDeclinedCount);
        eventPendingCount = view.findViewById(R.id.eventPendingCount);
        eventNumberOfApplicants=view.findViewById(R.id.eventNumberOfApplicants);
        removeImageButton = view.findViewById(R.id.removeImageButton);


        if (getArguments() != null) {
            String eventId = (String) getArguments().getSerializable("event");
            fetchEventData(eventId);
        }
        eventRunLottery.setOnClickListener(v->{
            String applicantsStr = eventNumberOfApplicants.getText().toString();
            if (!applicantsStr.isEmpty()) {
                int numberOfApplicants = Integer.parseInt(applicantsStr);
                handleLottery(numberOfApplicants);
            }
        });

        // Set click listener on the lists of users
        eventWaitlistCount.setOnClickListener(v -> showUserListDialog("eventWaitlist", "Waitlisted Users"));
        eventAcceptedCount.setOnClickListener(v -> showUserListDialog("eventEnrolled", "Accepted Users"));
        eventDeclinedCount.setOnClickListener(v -> showUserListDialog("eventDeclined", "Declined Users"));
        eventPendingCount.setOnClickListener(v -> showUserListDialog("eventInvited", "Invited Users"));

        // Set click listener on the event image
        eventImage.setOnClickListener(v -> openImagePicker());

        // Set click listener on the remove image button
        removeImageButton.setOnClickListener(v -> removeImage());

        eventNameTextView.setOnClickListener(v -> showEditEventTitleDialog());
        eventDateTextView.setOnClickListener(v -> openDatePickerDialog());
        eventTimeTextView.setOnClickListener(v -> openTimePickerDialog());
        eventDescriptionTextView.setOnClickListener(v -> showEditEventDetailsDialog());

        return view;
    }

    private void  handleLottery(Integer num){
        lottery.poolApplicants(num);

    };
    private void fetchEventData(String eventID) {
        eventController.getEvent(eventID, new EventController.EventCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null) {
                    EventDetailFragment.this.event = event;

                    // instantiate the lottery
                    EventDetailFragment.this.lottery=new Lottery(event);
                    if (event.getEventEnrolled().size()>=event.getEventCapacity() ||event.getEventWaitlist().size()==0){
                        // hide the button to pool if cannot pool more
                        // or has no users in waitlist
                        eventRunLottery.setVisibility(View.INVISIBLE);
                        eventNumberOfApplicants.setVisibility(View.INVISIBLE);

                    }
                    // Check if the event image exists
                    String eventImageUrl = event.getEventImage();

                    if (eventImageUrl != null && !eventImageUrl.isEmpty()) {
                        // Load the event image using Glide
                        Glide.with(getActivity())
                                .load(eventImageUrl)
                                .into(eventImage);
                        removeImageButton.setVisibility(View.VISIBLE);
                    } else {
                        // Show a "+" icon if no image exists
                        eventImage.setImageResource(R.drawable.baseline_add); // Placeholder drawable
                        removeImageButton.setVisibility(View.GONE);
                    }

                    eventNameTextView.setText(event.getEventName());
                    eventDescriptionTextView.setText("Event Description ✏\uFE0F: "+event.getEventDetails());
                    eventDateTextView.setText("Event Date \uD83D\uDCC5: "+event.getEventDate());
                    eventTimeTextView.setText("Event Time \uD83D\uDD53: "+event.getEventTime());
                    eventCapacityTextView.setText("Event Capacity\uD83E\uDDE2: "+event.getEventCapacity());

                    int waitlistCount = event.getEventWaitlist().size();  // Number of users on the waitlist
                    int acceptedCount = event.getEventEnrolled().size(); // Number of users accepted
                    int declinedCount = event.getEventDeclined().size(); // Number of users declined
                    int invitedCount = event.getEventInvited().size(); // Number of users invited

                    // Set the waitlist, accepted, declined, and pending counts
                    eventWaitlistCount.setText("Users on waitlist ⌛: " + waitlistCount);
                    eventAcceptedCount.setText("Users accepted ✅: " + acceptedCount);
                    eventDeclinedCount.setText("Users declined ❌: " + declinedCount);
                    eventPendingCount.setText("Users invited count 📩: " + invitedCount);

                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            if (event != null && newImageUri != null) {
                replaceEventImage(newImageUri);
            } else {
                Toast.makeText(getContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void replaceEventImage(Uri newImageUri) {
        String oldImageUrl = event.getEventImage();
        String newImageName = "event_images/" + event.getEventID() + "_new.jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference newImageRef = storage.getReference(newImageName);

        newImageRef.putFile(newImageUri)
                .addOnSuccessListener(taskSnapshot -> newImageRef.getDownloadUrl().addOnSuccessListener(newImageUrl -> {
                    // Update Firestore with the new image URL
                    FirebaseFirestore.getInstance()
                            .collection("Events")
                            .document(event.getEventID())
                            .update("eventImage", newImageUrl.toString())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Image updated successfully!", Toast.LENGTH_SHORT).show();
                                event.setEventImage(newImageUrl.toString());
                                // Update the displayed image
                                Glide.with(getActivity())
                                        .load(newImageUrl.toString())
                                        .into(eventImage);

                                // Show the "Remove Image" button
                                if (removeImageButton != null) {
                                    removeImageButton.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update event image", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload new image", Toast.LENGTH_SHORT).show();
                });
    }

    private void removeImage() {
        if (event.getEventImage() != null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(event.getEventImage())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Image removed successfully!", Toast.LENGTH_SHORT).show();
                        FirebaseFirestore.getInstance()
                                .collection("Events")
                                .document(event.getEventID())
                                .update("eventImage", null) // Remove the image URL from Firestore
                                .addOnSuccessListener(unused -> {
                                    event.setEventImage(null); // Update local event object
                                    eventImage.setImageResource(R.drawable.baseline_add); // Set placeholder image
                                    removeImageButton.setVisibility(View.GONE); // Hide the "Remove Image" button
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Failed to update Firestore", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to remove image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No image to remove!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditEventDetailsDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Event Details");

        // Add an EditText to the dialog
        final EditText input = new EditText(getContext());
        input.setText(event.getEventDetails()); // Pre-fill with the current event details
        input.setSelection(input.getText().length()); // Move cursor to the end
        input.setLines(3);
        input.setMaxLines(5);
        input.setPadding(40, 16, 40, 16);
        input.setBackgroundColor(getResources().getColor(android.R.color.white));
        builder.setView(input);

        // Add "Save" and "Cancel" buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedDetails = input.getText().toString().trim();

            // Update Firebase
            FirebaseFirestore.getInstance()
                    .collection("Events")
                    .document(event.getEventID())
                    .update("eventDetails", updatedDetails) // Update eventDetails field
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Event details updated successfully!", Toast.LENGTH_SHORT).show();
                        event.setEventDetails(updatedDetails); // Update local object
                        eventDescriptionTextView.setText("Event Description ✏️: " + updatedDetails); // Update UI
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update event details", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    private void showEditEventTitleDialog() {
        if (event == null) {
            Toast.makeText(getContext(), "Event data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Event Title");

        // Add an EditText to the dialog
        final EditText input = new EditText(getContext());
        input.setText(event.getEventName()); // Pre-fill with the current event title
        input.setSelection(input.getText().length()); // Move cursor to the end
        input.setPadding(40, 16, 40, 16);
        input.setBackgroundColor(getResources().getColor(android.R.color.white));
        builder.setView(input);

        // Add "Save" and "Cancel" buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedTitle = input.getText().toString().trim();

            if (!updatedTitle.isEmpty()) {
                // Update Firebase
                FirebaseFirestore.getInstance()
                        .collection("Events")
                        .document(event.getEventID())
                        .update("eventName", updatedTitle)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Event title updated successfully!", Toast.LENGTH_SHORT).show();
                            event.setEventName(updatedTitle); // Update local object
                            eventNameTextView.setText(updatedTitle); // Update UI
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update event title", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Event title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    private void openTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    eventTimeTextView.setText("Event Time ⏰: " + selectedTime);

                    // Update Firebase
                    FirebaseFirestore.getInstance()
                            .collection("Events")
                            .document(event.getEventID())
                            .update("eventTime", selectedTime)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getContext(), "Event time updated successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to update event time", Toast.LENGTH_SHORT).show());
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    eventDateTextView.setText("Event Date 📅: " + selectedDate);

                    // Update Firebase
                    FirebaseFirestore.getInstance()
                            .collection("Events")
                            .document(event.getEventID())
                            .update("eventDate", selectedDate)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getContext(), "Event date updated successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to update event date", Toast.LENGTH_SHORT).show());
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showUserListDialog(String listField, String title) {
        if (event == null) {
            Toast.makeText(getContext(), "Event data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.fetchUserNamesFromList(event.getEventID(), listField, new EventController.UserListCallback() {
            @Override
            public void onUserListFetched(ArrayList<String> userNames) {
                if (userNames.isEmpty()) {
                    Toast.makeText(getContext(), "No users found in " + listField, Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("EventDetailFragment", "User Names Fetched: " + userNames);

                // Create a dialog with the fetched user names
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_list, null);
                ListView userListView = dialogView.findViewById(R.id.userListView);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, userNames);
                userListView.setAdapter(adapter);

                builder.setView(dialogView);
                builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

