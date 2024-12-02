package com.example.minion_project.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.minion_project.Notification;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.example.minion_project.user.UserAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for image picker
    private Uri newImageUri;  // To store the selected image URI
    private Notification notification;
    private ListenerRegistration eventListenerRegistration;

    ImageView eventImage;
    ImageView eventQrCode;
    TextView eventNameTextView;
    TextView eventDateTextView;
    TextView eventTimeTextView;
    TextView eventDescriptionTextView;
    TextView eventCapacityTextView;
    TextView eventWaitlistCount ;
    TextView eventAcceptedCount ;
    TextView eventDeclinedCount ;
    TextView eventPendingCount ;
    TextView eventStartInfo;
    TextView eventRejectedCount;
    EditText eventNumberOfApplicants;
    Button showWaitlistMap;
    Button eventRunLottery;
    Button removeImageButton;
    Button eventStartButton;
    EventController eventController;

    /**
     * Required empty public constructor
     */
    public EventDetailFragment() {
        this.eventController=new EventController();
    }

    /**
     *
     * @param param1
     * @param param2
     * @return
     */
    public static EventDetailFragment newInstance(String param1, String param2) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Event event;
    private Lottery lottery;

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        // Use the 'event' object to populate the UI
        eventImage = view.findViewById(R.id.eventImage);
        eventQrCode = view.findViewById(R.id.eventQrCode);
        eventNameTextView = view.findViewById(R.id.eventTitle);
        eventDateTextView = view.findViewById(R.id.eventDate);
        eventTimeTextView = view.findViewById(R.id.eventTime);
        eventDescriptionTextView = view.findViewById(R.id.eventDescription);
        eventCapacityTextView = view.findViewById(R.id.eventCapacity);
        eventRunLottery=view.findViewById(R.id.eventRunLottery);
        eventStartButton=view.findViewById(R.id.eventStart);
        eventStartInfo=view.findViewById(R.id.eventStartInfo);
        eventWaitlistCount = view.findViewById(R.id.eventWaitlistcount);
        eventAcceptedCount = view.findViewById(R.id.eventAcceptedCount);
        eventDeclinedCount = view.findViewById(R.id.eventDeclinedCount);
        eventPendingCount = view.findViewById(R.id.eventPendingCount);
        eventRejectedCount= view.findViewById(R.id.eventRejectedCount);
        eventNumberOfApplicants=view.findViewById(R.id.eventNumberOfApplicants);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        showWaitlistMap=view.findViewById(R.id.showWaitlistMap);
        notification=new Notification();

        if (getArguments() != null) {
            String eventId = (String) getArguments().getSerializable("event");

            fetchEventData(eventId);
        }
        eventRunLottery.setOnClickListener(v->{
            String applicantsStr = eventNumberOfApplicants.getText().toString();
            if (!applicantsStr.isEmpty()) {
                int numberOfApplicants = Integer.parseInt(applicantsStr);
                Log.d("EventDetailFragment", "Number of applicants: " + numberOfApplicants);
                handleLottery(numberOfApplicants);
            }
        });

        //show map
        showWaitlistMap.setOnClickListener(v->{
            //open the mapactivity fragment
            ArrayList<String> waitlisted=event.getEventWaitlist();
            eventController.getLocation(waitlisted, new EventController.LocationCallback() {

                /**
                 * Callback method to handle the fetched locations.
                 * @param locations The fetched locations
                 */
                @Override
                public void onLocationFetched(ArrayList<HashMap<String, Double>> locations) {

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("waitlisted", locations); // Pass the fetched data
                    startActivity(intent);
                }
            });

        });

        // Set click listener on the lists of users
        eventWaitlistCount.setOnClickListener(v -> showUserListDialog("eventWaitlist", "Waitlisted Users"));
        eventAcceptedCount.setOnClickListener(v -> showUserListDialog("eventEnrolled", "Accepted Users"));
        eventDeclinedCount.setOnClickListener(v -> showUserListDialog("eventDeclined", "Declined Users"));
        eventPendingCount.setOnClickListener(v -> showUserListDialog("eventInvited", "Invited Users"));
        eventRejectedCount.setOnClickListener(v -> showUserListDialog("eventRejected", "Rejected Users"));

        // Set click listener on the event image
        eventImage.setOnClickListener(v -> openImagePicker());

        // Set click listener on the remove image button
        removeImageButton.setOnClickListener(v -> removeImage());

        eventNameTextView.setOnClickListener(v -> showEditEventTitleDialog());
        eventDateTextView.setOnClickListener(v -> openDatePickerDialog());
        eventTimeTextView.setOnClickListener(v -> openTimePickerDialog());
        eventDescriptionTextView.setOnClickListener(v -> showEditEventDetailsDialog());
        eventStartButton.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Accept Invite")
                    .setMessage("Do you want to start the event(This cannot be undone)?")
                    .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getContext(), "You have started the event!", Toast.LENGTH_SHORT).show();
                            eventController.startEvent(event);

                        }
                    })
                    .setNegativeButton("Don't start", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .create()
                    .show();
        });
        return view;
    }

    /**
     * Handle the lottery
     * @param num Number of applicants
     */
    private void  handleLottery(Integer num){
        Log.d("EventDetailFragment", "Handling lottery with num: " + num);
        lottery.poolApplicants(num);

    };

    /**
     * Fetch event data from Firestore
     * @param eventID Event ID
     */
    private void fetchEventData(String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(eventID);

        // Remove existing listener if any
        if (eventListenerRegistration != null) {
            eventListenerRegistration.remove();
        }

        eventListenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("EventDetailFragment", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Event event = snapshot.toObject(Event.class);
                if (event != null) {
                    EventDetailFragment.this.event = event;

                    // Clean invalid user IDs from event lists
                    cleanInvalidUserIds(event);

                    // Update the UI with the new data
                    updateUI(event);
                } else {
                    Log.d("EventDetailFragment", "Current data: null");
                }
            } else {
                Log.d("EventDetailFragment", "Current data: null");
            }
        });
    }


    /**
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

    /**
     * Open image picker
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Replace event image
     * @param newImageUri New image URI
     */
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

    /**
     * Remove image
     */
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

    /**
     * Show edit event details dialog
     */
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

    /**
     * Show edit event title dialog
     */
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

    /**
     * Open time picker dialog
     */
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

    /**
     * Open date picker dialog
     */
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

    /**
     * Show user list dialog
     * @param listField Name of the list field
     * @param title Title of the dialog
     */
    private void showUserListDialog(String listField, String title) {
        if (event == null) {
            Toast.makeText(getContext(), "Event data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.fetchUserNamesFromList(event.getEventID(), listField, new EventController.UserListCallback() {
            /**
             * Callback method to handle the fetched user names.
             * @param userNames List of user names
             */
            @Override
            public void onUserListFetched(ArrayList<Map.Entry<String, String>> userNames) {
                // Check if no users were found
                if (userNames.isEmpty()) {
                    Toast.makeText(getContext(), "No users found in " + listField, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Extract user names from the Map.Entry objects
                ArrayList<String> userNamesList = new ArrayList<>();
                for (Map.Entry<String, String> entry : userNames) {
                    userNamesList.add(entry.getKey());  // Extract the user name
                }

                // Inflate the dialog layout
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_list, null);
                RecyclerView recyclerView = dialogView.findViewById(R.id.userRecyclerView);

                // Set up the RecyclerView
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                UserAdapter adapter = new UserAdapter(getContext(), userNamesList);  // Pass only the user names
                recyclerView.setAdapter(adapter);

                // Create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);
                builder.setView(dialogView);

                // Handle the positive button action (Send Notification)
                builder.setPositiveButton("Send Notification", (dialog, which) -> {
                    Set<String> selectedUsers = adapter.getSelectedUsers();  // Get the selected users

                    if (selectedUsers.isEmpty()) {
                        Toast.makeText(getContext(), "No users selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Loop through the selected users and send them to the notification document
                    for (String user : selectedUsers) {
                        // Find the corresponding user ID for the selected user name
                        String userId = null;
                        for (Map.Entry<String, String> entry : userNames) {
                            if (entry.getKey().equals(user)) {
                                userId = entry.getValue();  // Get the userId from the Map.Entry
                                break;
                            }
                        }


                        if (userId != null) {
                            // Send the notification for the selected user
                            if (title.equals("Waitlisted Users")){
                                notification.addUserToNotificationDocument("waitlistlist_entrants", userId);

                            }
                            if (title.equals("Declined Users")){
                                notification.addUserToNotificationDocument("cancelled_event", userId);

                            }
                            if (title.equals("Accepted Users")){
                                notification.addUserToNotificationDocument("chosen_entrant", userId);

                            }
                            if (title.equals("Invited Users")){
                                notification.addUserToNotificationDocument("Won_lottery", userId);

                            }
                            if (title.equals("Rejected Users")){
                                notification.addUserToNotificationDocument("lost_lottery", userId);

                            }
                            Log.d("EventDetailFragment", "Sending notification to user: " + user);  // Log the notification
                        }
                    }

                    Toast.makeText(getContext(), "Notifications sent to selected users", Toast.LENGTH_SHORT).show();
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.create().show();
            }

            /**
             * Callback method to handle the error.
             * @param errorMessage Error message
             */
            @Override
            public void onError(String errorMessage) {
                // Handle the error callback
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("EventDetailFragment", "Error fetching user list: " + errorMessage);  // Log the error for debugging
            }
        });
    }

    /**
     * Clean invalid user IDs from event lists
     * @param event Event to clean user IDs from
     */
    private void cleanInvalidUserIds(Event event) {
        Log.d("EventDetailFragment", "cleanInvalidUserIds called for event ID: " + event.getEventID());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(event.getEventID());

        Map<String, List<String>> userLists = new HashMap<>();
        userLists.put("eventWaitlist", event.getEventWaitlist());
        userLists.put("eventEnrolled", event.getEventEnrolled());
        userLists.put("eventInvited", event.getEventInvited());
        userLists.put("eventDeclined", event.getEventDeclined());
        userLists.put("eventRejected", event.getEventRejected());

        for (Map.Entry<String, List<String>> entry : userLists.entrySet()) {
            String listName = entry.getKey();
            List<String> userIds = entry.getValue();

            Log.d("EventDetailFragment", "Processing list: " + listName + " with user IDs: " + userIds);

            if (userIds == null || userIds.isEmpty()) {
                Log.d("EventDetailFragment", "No user IDs in list: " + listName);
                continue;
            }

            List<String> invalidUserIds = new ArrayList<>();
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            List<String> trimmedUserIds = new ArrayList<>();

            // Trim user IDs and collect tasks
            for (String userId : userIds) {
                String trimmedUserId = userId.trim();
                trimmedUserIds.add(trimmedUserId);
                DocumentReference userRef = db.collection("Users").document(trimmedUserId);
                tasks.add(userRef.get());
            }

            // Wait for all tasks to complete
            Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener(taskList -> {
                        Log.d("EventDetailFragment", "All tasks completed for list: " + listName);
                        for (int i = 0; i < tasks.size(); i++) {
                            Task<DocumentSnapshot> task = tasks.get(i);
                            String userId = trimmedUserIds.get(i);
                            if (task.isSuccessful()) {
                                DocumentSnapshot userSnapshot = task.getResult();
                                if (!userSnapshot.exists()) {
                                    invalidUserIds.add(userId);
                                    Log.d("EventDetailFragment", "Invalid user ID: " + userId + " in list: " + listName);
                                } else {
                                    Log.d("EventDetailFragment", "Valid user ID: " + userId + " in list: " + listName);
                                }
                            } else {
                                Log.e("EventDetailFragment", "Error fetching user: " + userId, task.getException());
                            }
                        }

                        // Remove invalid user IDs from the event list in Firestore
                        if (!invalidUserIds.isEmpty()) {
                            Log.d("EventDetailFragment", "Removing invalid user IDs from list: " + listName + " IDs: " + invalidUserIds);
                            for (String invalidUserId : invalidUserIds) {
                                eventRef.update(listName, FieldValue.arrayRemove(invalidUserId))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("EventDetailFragment", "Removed invalid user ID: " + invalidUserId + " from list: " + listName);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("EventDetailFragment", "Failed to remove invalid user ID: " + invalidUserId + " from list: " + listName, e);
                                        });
                            }
                        } else {
                            Log.d("EventDetailFragment", "No invalid user IDs to remove in list: " + listName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EventDetailFragment", "Failed to complete user ID validation tasks for list: " + listName, e);
                    });
        }
    }


    /**
     * Update UI with event data
     * @param event Event to update UI with
     */
    private void updateUI(Event event) {
        // Load the event image
        String eventImageUrl = event.getEventImage();
        if (eventImageUrl != null && !eventImageUrl.isEmpty()) {
            Glide.with(getActivity())
                    .load(eventImageUrl)
                    .into(eventImage);
            removeImageButton.setVisibility(View.VISIBLE);
        } else {
            eventImage.setImageResource(R.drawable.baseline_add);
            removeImageButton.setVisibility(View.GONE);
        }

        // Load the QR code
        String eventQrCodeUrl = event.getEventQrCode();
        if (eventQrCodeUrl != null && !eventQrCodeUrl.isEmpty()) {
            eventQrCode.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(eventQrCodeUrl)
                    .into(eventQrCode);
        } else {
            eventQrCode.setVisibility(View.GONE); // Hide the QR code if not available
        }

        // Update text views
        eventNameTextView.setText(event.getEventName());
        eventDescriptionTextView.setText("Event Description ✏️: " + event.getEventDetails());
        eventDateTextView.setText("Event Date 📅: " + event.getEventDate());
        eventTimeTextView.setText("Event Time ⏰: " + event.getEventTime());
        eventCapacityTextView.setText("Event Capacity🧢: " + event.getEventCapacity());

        // Update counts
        int waitlistCount = event.getEventWaitlist().size();
        int acceptedCount = event.getEventEnrolled().size();
        int declinedCount = event.getEventDeclined().size();
        int invitedCount = event.getEventInvited().size();
        int rejectedCount = event.getEventRejected().size();

        eventWaitlistCount.setText("Users on waitlist ⌛: " + waitlistCount);
        eventAcceptedCount.setText("Users accepted ✅: " + acceptedCount);
        eventDeclinedCount.setText("Users declined ❌: " + declinedCount);
        eventPendingCount.setText("Users invited count 📩: " + invitedCount);
        eventRejectedCount.setText("Users rejected ✖️: " + rejectedCount);

        this.lottery = new Lottery(event);

        // Update lottery button visibility
        if (event.getEventEnrolled().size() >= event.getEventCapacity() || event.getEventWaitlist().isEmpty()) {
            eventRunLottery.setVisibility(View.INVISIBLE);
            eventNumberOfApplicants.setVisibility(View.INVISIBLE);
        } else {
            eventRunLottery.setVisibility(View.VISIBLE);
            eventNumberOfApplicants.setVisibility(View.VISIBLE);
        }

        // Update event start button visibility
        if (event.getEventStart()) {
            eventStartButton.setVisibility(View.INVISIBLE);
            eventStartInfo.setVisibility(View.VISIBLE);
        } else {
            eventStartButton.setVisibility(View.VISIBLE);
            eventStartInfo.setVisibility(View.GONE);
        }
    }

    /**
     * Destroy view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (eventListenerRegistration != null) {
            eventListenerRegistration.remove();
            eventListenerRegistration = null;
        }
    }


}

