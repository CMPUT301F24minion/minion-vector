/**
 * Controller class for managing user interactions with events and Firestore.
 */

package com.example.minion_project.user;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.Notification;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.example.minion_project.organizer.Organizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserController {

    public User user;
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference usersRef;
    private EventController eventController;
    private Notification notification;
    /**
     * Constructor for UserController.
     * @param user The User object associated with this controller.
     */
    public UserController(User user) {
        this.user = user;
        this.usersRef = Our_Firestore.getUsersRef();
        this.eventController=new EventController();
        this.notification=new Notification();
    }

    /**
     * Gets the status of a user's event.
     * @param eventId
     * @return The status of the user's event as a string if it exists if not return null
     */
    public String getUserEventStatus(String eventId){
        Map<String,String> events=user.getAllEvents();
       if(events.containsKey(eventId)){
           return events.get(eventId);
       };
       return null;

    }

    /**
     * Attempts to join an event.
     * @param event event to be joined
     * @return True if joined successfully false if already joined or did not join
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
    public Boolean AcceptInvite(String eventId) {
        //fetch eventfirst
        Event event = eventController.getEvent(eventId, new EventController.EventCallback() {
            @Override
            public void onEventFetched(Event event) {

                    // Prepare the data to be added to Firestore
                    Map<String, String> eventData = new HashMap<>();

                    // Check that event data is valid and not empty
                    if (event.getEventID() != null && !event.getEventID().isEmpty() &&
                            event.getEventName() != null && !event.getEventName().isEmpty()) {


                        // Update the user's document with the new event
                        usersRef.document(user.getDeviceID())
                                .update("Events."+event.getEventID(), "enrolled")
                                .addOnSuccessListener(aVoid -> {
                                    // If the Firestore update is successful, add the event to the local user object
                                    user.addEvent(event, "enrolled");
                                });
                        eventController.addToEventsEnrolled(event,user.getDeviceID());
                        eventController.removeFromInvited(event,user.getDeviceID());
                        notification.addUserToNotificationDocument("Won_lottery", user.getDeviceID());

                    } else {
                        Log.e("UserController", "Event data is invalid: Event ID or Event Name is empty.");
                }
            }

            @Override
            public void onError(String errorMessage) {
            }

            ;
        });

    return FALSE;
    };
    public Boolean DeclineInvite(String eventID) {
        //fetch eventfirst
        Event event = eventController.getEvent(eventID, new EventController.EventCallback() {
            @Override
            public void onEventFetched(Event event) {

                // Prepare the data to be added to Firestore
                Map<String, String> eventData = new HashMap<>();

                // Check that event data is valid and not empty
                if (event.getEventID() != null && !event.getEventID().isEmpty() &&
                        event.getEventName() != null && !event.getEventName().isEmpty()) {


                    // Update the user's document with the new event
                    usersRef.document(user.getDeviceID())
                            .update("Events."+event.getEventID(), "declined")
                            .addOnSuccessListener(aVoid -> {
                                // If the Firestore update is successful, add the event to the local user object
                                user.addEvent(event, "declined");
                            });
                    // add to events rejected also pools one random user for lottery
                    eventController.addToEventsRejected(event,user.getDeviceID());
                    eventController.removeFromInvited(event,user.getDeviceID());
                    notification.addUserToNotificationDocument("cancelled_event", user.getDeviceID());

                } else {
                    Log.e("UserController", "Event data is invalid: Event ID or Event Name is empty.");
                }
            }

            @Override
            public void onError(String errorMessage) {
            }

            ;
        });

        return FALSE;
    }

    //fetch user infor with new info
    public void fetchUser(){
        usersRef.document(user.getDeviceID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            /**
             * onComplete method for fetching user data
             * @param task
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data!=null) {

                            String name = (String) data.get("Name");
                            String profileImageUrl = (String) data.get("profileImage");


                            HashMap <String,String> events = (HashMap<String, String>)data.get("Events");

                            String phoneNumber = (String) data.get("Phone_number");
                            String email = (String) data.get("Email");
                            String Location = (String) data.get("Location");
                            HashMap <String, ArrayList> Notification = (HashMap<String, ArrayList>) data.get("Notfication");
                            // Create the User object
                            String deviceId=user.getDeviceID();
                            user = new User(deviceId,name, email, phoneNumber,events, Location, Notification);


                        }
                    }

                }
            }
        });
    }
    /**
     * Removes the user from an event if their status is "joined".
     * This also removes the user from the event's waitlist.
     *
     * @param event The event to be unjoined.
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
