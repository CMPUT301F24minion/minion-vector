package com.example.minion_project.user;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.util.Log;
import android.view.LayoutInflater;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.organizer.Organizer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserController {

    public User user;
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference usersRef;

    public UserController(User user) {
        this.user = user;
        this.usersRef = Our_Firestore.getUsersRef();
    }

    /* returns True if joined else false

     */
    public Boolean join_event(Event event) {
        // Check if the event is already in the user's list of events
        if (user.getAllEvents().containsKey(event.getEventID())) {
            // If the event is already in the list, return FALSE
            return FALSE;
        } else {
            // Prepare the data to be added to Firestore
            Map<String, String> eventData = new HashMap<>();

            // Check that event data is valid and not empty
            if (event.getEventID() != null && !event.getEventID().isEmpty() &&
                    event.getEventName() != null && !event.getEventName().isEmpty()) {


                // Update the user's document with the new event
                usersRef.document(user.getDeviceID())
                        .update("Events."+event.getEventID(), "joined")
                        .addOnSuccessListener(aVoid -> {
                            // If the Firestore update is successful, add the event to the local user object
                            user.addEvent(event, "joined");
                        });


                return TRUE;  // Return TRUE after attempting to add the event
            } else {
                Log.e("UserController", "Event data is invalid: Event ID or Event Name is empty.");
                return FALSE;  // Return FALSE if event data is invalid
            }
        }
    }

}
