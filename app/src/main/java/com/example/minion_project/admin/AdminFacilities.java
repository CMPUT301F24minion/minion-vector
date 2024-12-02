package com.example.minion_project.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.facility.Facility;
import com.example.minion_project.facility.FacilitiesAdapter;
import com.example.minion_project.events.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * AdminFacilities is a fragment that displays a list of facilities
 */
public class AdminFacilities extends Fragment implements FacilitiesAdapter.OnFacilityClickListener {

    private RecyclerView recyclerView;
    private FacilitiesAdapter adapter;
    private ArrayList<Facility> facilityList;
    private FireStoreClass ourFirestore;
    private CollectionReference facilitiesRef;

    /**
     * AdminFacilities required empty public constructor
     */
    public AdminFacilities() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment
     * @return A new instance of fragment AdminFacilities
     */
    public static AdminFacilities newInstance() {
        return new AdminFacilities();
    }

    /**
     * Called to initialize the fragment when it is created.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourFirestore = new FireStoreClass();
        facilitiesRef = ourFirestore.getFacilitiesRef();
        facilityList = new ArrayList<>();
    }

    /**
     * Inflate the layout for this fragment
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_facilities, container, false);

        recyclerView = view.findViewById(R.id.adminFacilitiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FacilitiesAdapter(getContext(), facilityList);
        adapter.setOnFacilityClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);

        fetchFacilities();

        return view;
    }

    /**
     * Fetch facilities from Firestore
     */
    private void fetchFacilities() {
        facilitiesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilityList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Facility facility = document.toObject(Facility.class);
                        if (facility != null) {
                            facility.setDocumentID(document.getId()); // Set document ID
                            facilityList.add(facility);
                            Log.d("AdminFacilities", "Added Facility: " + facility.toString());
                        }
                    }

                    if (facilityList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        if (getView() != null) {
                            getView().findViewById(R.id.noFacilitiesTextView).setVisibility(View.VISIBLE);
                        }
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        if (getView() != null) {
                            getView().findViewById(R.id.noFacilitiesTextView).setVisibility(View.GONE);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminFacilities", "Error fetching facilities: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch facilities.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Handle facility click
     * @param facility
     */
    @Override
    public void onFacilityClick(Facility facility) {
        showDeleteConfirmationDialog(facility);
    }

    /**
     * Show a confirmation dialog before deleting a facility
     * @param facility Facility to be deleted
     */
    private void showDeleteConfirmationDialog(Facility facility) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Facility")
                .setMessage("Are you sure you want to delete this facility and all associated events?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteFacilityAndEvents(facility);
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Delete a facility and its associated events
     * @param facility Facility to be deleted
     */
    private void deleteFacilityAndEvents(Facility facility) {
        // Delete associated events first
        deleteAssociatedEvents(facility.getDocumentID(), () -> {
            // After deleting events, delete the facility
            facilitiesRef.document(facility.getDocumentID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility and associated events deleted successfully.", Toast.LENGTH_SHORT).show();
                        // Remove facility from local list and notify adapter
                        facilityList.remove(facility);
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminFacilities", "Error deleting facility: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to delete facility.", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    /**
     * Delete associated events for a facility
     * @param facilityDocumentID Facility document ID
     * @param onComplete Callback to be executed after deletion
     */
    private void deleteAssociatedEvents(String facilityDocumentID, Runnable onComplete) {
        // Reference to the Events collection
        CollectionReference eventsRef = ourFirestore.getEventsRef();

        // Query events where eventOrganizer matches the facilityID
        eventsRef.whereEqualTo("eventOrganizer", facilityDocumentID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Batch delete events
                        WriteBatch batch = ourFirestore.getFirestore().batch();
                        int eventsToDelete = queryDocumentSnapshots.size();
                        int[] deletionsCompleted = {0}; // To keep track of deletions

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                String eventImage = event.getEventImage();
                                String eventQrCode = event.getEventQrCode();
                                String eventID = document.getId(); // Use document ID for eventID

                                // Delete the event image from Firebase Storage
                                deleteFileFromStorage(eventImage);

                                // Delete the event QR code from Firebase Storage
                                deleteFileFromStorage(eventQrCode);

                                // Delete references from Users and Organizers
                                deleteEventReferencesFromUsersAndOrganizers(eventID, event);

                                // Delete the event document
                                batch.delete(document.getReference());
                            }

                            // After processing each event
                            deletionsCompleted[0]++;
                            if (deletionsCompleted[0] == eventsToDelete) {
                                // Commit the batch after all events are processed
                                batch.commit()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("AdminFacilities", "Associated events and their files deleted successfully.");
                                            onComplete.run();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AdminFacilities", "Error deleting associated events: " + e.getMessage());
                                            Toast.makeText(getContext(), "Failed to delete associated events.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    } else {
                        // No associated events found
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminFacilities", "Error fetching associated events: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch associated events.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Delete a file from Firebase Storage
     * @param fileUrl URL of the file to be deleted
     */
    private void deleteFileFromStorage(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(fileUrl);

            storageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AdminFacilities", "File deleted successfully: " + fileUrl);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminFacilities", "Error deleting file: " + fileUrl + " Error: " + e.getMessage());
                    });
        }
    }

    /**
     * Delete event references from Users and Organizers
     * @param eventID Event ID
     * @param event Event object
     */
    private void deleteEventReferencesFromUsersAndOrganizers(String eventID, Event event) {
        // Reference to Users and Organizers collections
        CollectionReference usersRef = ourFirestore.getUsersRef();
        CollectionReference organizersRef = ourFirestore.getOrganizersRef();

        // Remove the event from users in eventEnrolled, eventWaitlist, eventInvited, etc.
        ArrayList<String> userIds = new ArrayList<>();
        if (event.getEventEnrolled() != null) userIds.addAll(event.getEventEnrolled());
        if (event.getEventWaitlist() != null) userIds.addAll(event.getEventWaitlist());
        if (event.getEventInvited() != null) userIds.addAll(event.getEventInvited());
        if (event.getEventDeclined() != null) userIds.addAll(event.getEventDeclined());
        if (event.getEventRejected() != null) userIds.addAll(event.getEventRejected());

        // Remove event from each user's Events array
        for (String userId : userIds) {
            usersRef.document(userId)
                    .update("Events", FieldValue.arrayRemove(eventID))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AdminFacilities", "Removed event " + eventID + " from user " + userId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminFacilities", "Error removing event from user: " + e.getMessage());
                    });
        }

        // Remove event from the organizer's Events array
        String organizerId = event.getEventOrganizer();
        if (organizerId != null && !organizerId.isEmpty()) {
            organizersRef.document(organizerId)
                    .update("Events", FieldValue.arrayRemove(eventID))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AdminFacilities", "Removed event " + eventID + " from organizer " + organizerId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminFacilities", "Error removing event from organizer: " + e.getMessage());
                    });
        }
    }
}
