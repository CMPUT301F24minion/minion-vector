package com.example.minion_project.events;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class EventController {
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference eventsRef;
    private Event event;
    public EventController() {
        this.eventsRef=Our_Firestore.getEventsRef();
    }

    // get an event
    public Event getEvent(String eventId, final EventCallback callback){
       eventsRef.document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    this.event = document.toObject(Event.class);
                    this.event.setEventID(eventId);
                    callback.onEventFetched(event);

                }else{
                    callback.onError("Event not found");
                }
            }else{
                callback.onError("Failed to fetch");
            }
        });
       return this.event;
    }

    public void capacityLotto(Event event) {
        // Avoid modifying the original waitlist directly
        ArrayList<User> copyOfWaitlist = new ArrayList<>(event.getEventWaitlist());

        // Shuffle to randomize the order
        Collections.shuffle(copyOfWaitlist);

        // Determine the number of users to select based on event capacity
        int numToSelect = Math.min(Integer.parseInt(event.getEventCapacity()), copyOfWaitlist.size());

        // Select the users and add them to the invited list in the Event object
        ArrayList<User> selectedUsers = new ArrayList<>(copyOfWaitlist.subList(0, numToSelect));
        ArrayList<String> invitedUserIds = new ArrayList<>();

        // Prepare a list of user IDs or other necessary info to store in Firestore
        for (User user : selectedUsers) {
            invitedUserIds.add(user.getDeviceID()); // Assuming User has a getDeviceID() method
        }

        event.setEventInvited(invitedUserIds);

        // Update Firestore with the modified invited list
        eventsRef.document(event.getEventID())
                .update("eventInvited", invitedUserIds)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("eventInvited field updated successfully in Firestore");
                    // Notify users NEED TO IMPLEMENT THIS
                    //notifySelectedUsers(selectedUsers);
                    //notifyUnselectedUsers(event.getEventWaitlist(), selectedUsers);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating eventInvited field in Firestore: " + e.getMessage());
                });
    }

    public void declinedLottoSurveyOne(Event event) {
        ArrayList<User> waitList = new ArrayList<>(event.getEventWaitlist());
        Collections.shuffle(waitList);
        User selected = waitList.get(0);
        event.getEventInvited().add(selected.getDeviceID()); // Assuming User has a getDeviceID() method

        eventsRef.document(event.getEventID())
                .update("eventInvited", selected.getDeviceID())
                .addOnSuccessListener(aVoid -> {
                    System.out.println("eventInvited field updated successfully in Firestore");
                    // Notify users NEED TO IMPLEMENT THIS
                    //notifySelectedUsers(selectedUsers);
                    //notifyUnselectedUsers(event.getEventWaitlist(), selectedUsers);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating eventInvited field in Firestore: " + e.getMessage());
                });



    }


    // Define an interface for the callback, this allows async fetch calls to only display after fetched
    public interface EventCallback {
        void onEventFetched(Event event);
        void onError(String errorMessage);
    }
}
