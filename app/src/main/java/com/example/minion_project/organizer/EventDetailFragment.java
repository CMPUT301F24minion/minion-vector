package com.example.minion_project.organizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    TextView eventDescriptionTextView;
    TextView eventCapacityTextView;
    TextView eventWaitlistCount ;
    TextView eventAcceptedCount ;
    TextView eventDeclinedCount ;
    TextView eventPendingCount ;
    EditText eventNumberOfApplicants;
    Button eventRunLottery;
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
        eventDescriptionTextView = view.findViewById(R.id.eventDescription);
        eventCapacityTextView = view.findViewById(R.id.eventCapacity);
        eventRunLottery=view.findViewById(R.id.eventRunLottery);
        eventWaitlistCount = view.findViewById(R.id.eventWaitlistcount);
        eventAcceptedCount = view.findViewById(R.id.eventAcceptedCount);
        eventDeclinedCount = view.findViewById(R.id.eventDeclinedCount);
        eventPendingCount = view.findViewById(R.id.eventPendingCount);
        eventNumberOfApplicants=view.findViewById(R.id.eventNumberOfApplicants);
        if (getArguments() != null) {
            String eventId = (String) getArguments().getSerializable("event");
            fetchEventData(eventId);
        }

        // Set click listener on the event image
        eventImage.setOnClickListener(v -> openImagePicker());

        eventRunLottery.setOnClickListener(v->{
            String applicantsStr = eventNumberOfApplicants.getText().toString();
            if (!applicantsStr.isEmpty()) {
                int numberOfApplicants = Integer.parseInt(applicantsStr);
                handleLottery(numberOfApplicants);
            }
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update event image", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload new image", Toast.LENGTH_SHORT).show();
                });
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
                    } else {
                        // Show a "+" icon if no image exists
                        eventImage.setImageResource(R.drawable.baseline_add); // Placeholder drawable
                    }

                    eventNameTextView.setText(event.getEventName());
                    eventDescriptionTextView.setText("Event Description ✏\uFE0F: "+event.getEventDescription());
                    eventDateTextView.setText("Event Date \uD83D\uDCC5: "+event.getEventDate());
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
}

