package com.example.minion_project.organizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * OrganizerEvents is a fragment that displays a list of events organized by an organizer.
 */
public class OrganizerEvents extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for image picker

    private RecyclerView organizerEventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;
    private FireStoreClass ourFirestore;
    private OrganizerController organizerController;

    private Event selectedEvent;  // Keep track of which event's image is being replaced

    /**
     * Constructor for OrganizerEvents
     * @param organizerController OrganizerController
     */
    public OrganizerEvents(OrganizerController organizerController) {
        this.organizerController = organizerController;
        this.ourFirestore = new FireStoreClass();
    }

    /**
     * Creates the view for the fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_events, container, false);

        organizerEventsRecyclerView = view.findViewById(R.id.organizerEventsRecyclerView);
        organizerEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        organizerEventsRecyclerView.setAdapter(eventsAdapter);

        // Set image select listener
        eventsAdapter.setOnImageSelectListener(event -> {
            selectedEvent = event;  // Set the selected event
            openImagePicker();     // Open image picker
        });

        fetchEvents();
        eventsAdapter.setOnItemClickListener(event -> {
            openEventDetailFragment(event);
        });
        return view;
    }

    /**
     * Opens the EventDetailFragment with the selected event
     * @param event The selected event
     */
    private void openEventDetailFragment(Event event) {
        EventDetailFragment eventDetailFragment = new EventDetailFragment();

        // Pass the event data to the new fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("event",  event.getEventID());
        eventDetailFragment.setArguments(bundle);

        // Replace the current fragment with the EventDetailFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.organizerEvent, eventDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Opens the image picker
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri newImageUri = data.getData();

            if (selectedEvent != null && newImageUri != null) {
                replaceEventImage(selectedEvent, newImageUri);
            } else {
                Toast.makeText(getContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Replace's the event image with the new image
     * @param event The event to update
     * @param newImageUri The new image URI
     */
    private void replaceEventImage(Event event, Uri newImageUri) {
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
                                eventsAdapter.notifyDataSetChanged();  // Refresh the list

                                // Optionally delete old image
                                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                                    StorageReference oldImageRef = storage.getReferenceFromUrl(oldImageUrl);
                                    oldImageRef.delete()
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(getContext(), "Old image deleted", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Failed to delete old image", Toast.LENGTH_SHORT).show();
                                            });
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
     * Fetches the events from Firestore
     */
    private void fetchEvents() {
        ArrayList<String> eventIds = organizerController.getOrganizer().getAllEvents();
        FirebaseFirestore db = ourFirestore.getFirestore();

        for (String eventId : eventIds) {
            db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setEventID(document.getId());
                            eventList.add(event);
                            eventsAdapter.notifyItemInserted(eventList.size() - 1);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
