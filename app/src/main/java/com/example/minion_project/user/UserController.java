package com.example.minion_project.user;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.util.Log;
import android.view.LayoutInflater;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
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
    private EventController eventController;
    public UserController(User user) {
        this.user = user;
        this.usersRef = Our_Firestore.getUsersRef();
        this.eventController=new EventController();
    }

    /*
    get the status of an event
     */
    public String getUserEventStatus(String eventId){
        Map<String,String> events=user.getAllEvents();
       if(events.containsKey(eventId)){
           return events.get(eventId);
       };
       return null;

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
                eventController.addEventToWaitlist(event,user.getDeviceID());
                return TRUE;  // Return TRUE after attempting to add the event
            } else {
                Log.e("UserController", "Event data is invalid: Event ID or Event Name is empty.");
                return FALSE;  // Return FALSE if event data is invalid
            }
        }
    }
    /*
    Only remove users with status "joined" all other users remain
    This removes them from the waitlist for that event so
    only use for that scenrario
     */
    public void unjoin_event(Event event){

        if (user.getAllEvents().containsKey(event.getEventID()) && user.getAllEvents().get(event.getEventID()).equals("joined")) {
            // Proceed with removing the event only if user is joined status

            // Log the operation for debugging
            Log.d("UserController", "Removing user from event: " + event.getEventID());

            // Remove the event from Firestore by deleting the event's entry from the "Events" map
            usersRef.document(user.getDeviceID())
                    .update("Events." + event.getEventID(), FieldValue.delete())
                    .addOnSuccessListener(aVoid -> {
                        // On success, remove the event from the local user object
                        user.removeEvent(event);  // Assuming you have this method in the user object
                        Log.d("UserController", "Successfully removed user from event: " + event.getEventID());
                    })
                    .addOnFailureListener(e -> {
                        // On failure, log the error
                        Log.e("UserController", "Failed to remove user from event: " + event.getEventID(), e);
                    });
            eventController.removeUserFromWaitlist(event,user.getDeviceID());
        } else {
            // Log that the event isn't in the user's list
            Log.e("UserController", "Event not found in user's list: " + event.getEventID());
        }
    }

}
