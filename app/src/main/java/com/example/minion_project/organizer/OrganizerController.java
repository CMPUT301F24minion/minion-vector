package com.example.minion_project.organizer;

import android.util.Log;
import android.widget.Toast;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;


/**
 * OrganizerController: provides methods for adding events to the organizer's list of events and
 * interacting with Firestore to update data.
 */
public class OrganizerController {
    public Organizer organizer;
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference organizersRef;

    /**
     * Constructs an OrganizerController for a specific organizer.
     * Initializes Firestore references needed to manage organizer data.
     *
     * @param organizer organizer instance to be managed by OrganizerController
     */
    public OrganizerController(Organizer organizer) {
        this.organizer = organizer;
        this.organizersRef = Our_Firestore.getOrganizersRef();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId
     * @return A new instance of fragment user_waitlisted.
     */
    public void addEvent(String eventId) {
        organizersRef.document(organizer.getDeviceID()).update("Events",
                        FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    // Only add to the organizer if the Firestore update was successful
                    this.organizer.addEvent(eventId);
                    Log.e("OrganizerController", "SUCCESS to add event: " + eventId);

                })
                .addOnFailureListener(e -> {
                    // Log failure
                    Log.e("OrganizerController", "Failed to add event: " + eventId + ", Error: " + e.getMessage());
                });
        ;

    }


    public Organizer getOrganizer() {
        return this.organizer;
    }
}
