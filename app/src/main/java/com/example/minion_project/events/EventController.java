package com.example.minion_project.events;

import android.util.Log;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Collections;

public class EventController {
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference eventsRef;
    private Event event;
    public EventController() {
        this.eventsRef=Our_Firestore.getEventsRef();
    }

    /* get an event with a callback
        so you can wait until the event is fetch is finished before executing next
     */
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
    public void addEventToWaitlist(Event event,String UserId){

        eventsRef.document(event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Use Firestore's arrayUnion to add "complete" to the eventWaitlist array
                    eventsRef.document(event.getEventID())
                            .update("eventWaitlist", FieldValue.arrayUnion(UserId))
                            .addOnSuccessListener(aVoid -> {
                                // Successfully added to the waitlist
                                Log.d("EventUpdate", "Successfully added 'complete' to the waitlist.");
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Log.e("EventUpdate", "Error adding 'complete' to the waitlist", e);
                            });
                } else {
                    Log.e("EventUpdate", "Event document does not exist.");
                }
            } else {
                Log.e("EventUpdate", "Failed to fetch event document", task.getException());
            }
        });

    }
    public void removeUserFromWaitlist(Event event,String UserID){
        eventsRef.document(event.getEventID()).update("eventWaitlist", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(aVoid -> {
                    // Log success message
                    Log.d("Firestore", "User " + UserID + " successfully removed from the waitlist for event " + event.getEventID());
                })
                .addOnFailureListener(e -> {
                    // Log failure message
                    Log.e("Firestore", "Error removing user " + UserID + " from the waitlist for event " + event.getEventID(), e);
                });
    }
//    public void capacityLotto(Event event) {
//        // Avoid modifying the original waitlist directly
//        ArrayList<String> copyOfWaitlist = new ArrayList<>(event.getEventWaitlist());
//
//        // Shuffle to randomize the order
//        Collections.shuffle(copyOfWaitlist);
//
//        // Determine the number of users to select based on event capacity
//        int numToSelect = Math.min(Integer.parseInt(event.getEventCapacity()), copyOfWaitlist.size());
//
//        // Select the users and add them to the invited list in the Event object
//        ArrayList<String> selectedUsers = new ArrayList<>(copyOfWaitlist.subList(0, numToSelect));
//        ArrayList<String> invitedUserIds = new ArrayList<>();
//
//        // Prepare a list of user IDs or other necessary info to store in Firestore
//        for (User user : selectedUsers) {
//            invitedUserIds.add(user.getDeviceID()); // Assuming User has a getDeviceID() method
//        }
//
//        event.setEventInvited(invitedUserIds);
//
//        // Update Firestore with the modified invited list
//        eventsRef.document(event.getEventID())
//                .update("eventInvited", invitedUserIds)
//                .addOnSuccessListener(aVoid -> {
//                    System.out.println("eventInvited field updated successfully in Firestore");
//                    // Notify users NEED TO IMPLEMENT THIS
//                    //notifySelectedUsers(selectedUsers);
//                    //notifyUnselectedUsers(event.getEventWaitlist(), selectedUsers);
//                })
//                .addOnFailureListener(e -> {
//                    System.err.println("Error updating eventInvited field in Firestore: " + e.getMessage());
//                });
//    }

//    public void declinedLottoSurveyOne(Event event) {
//        ArrayList<User> waitList = new ArrayList<>(event.getEventWaitlist());
//        Collections.shuffle(waitList);
//        User selected = waitList.get(0);
//        event.getEventInvited().add(selected.getDeviceID()); // Assuming User has a getDeviceID() method
//
//        eventsRef.document(event.getEventID())
//                .update("eventInvited", selected.getDeviceID())
//                .addOnSuccessListener(aVoid -> {
//                    System.out.println("eventInvited field updated successfully in Firestore");
//                    // Notify users NEED TO IMPLEMENT THIS
//                    //notifySelectedUsers(selectedUsers);
//                    //notifyUnselectedUsers(event.getEventWaitlist(), selectedUsers);
//                })
//                .addOnFailureListener(e -> {
//                    System.err.println("Error updating eventInvited field in Firestore: " + e.getMessage());
//                });
//
//
//
//    }
//

    // Define an interface for the callback, this allows async fetch calls to only display after fetched
    public interface EventCallback {
        void onEventFetched(Event event);
        void onError(String errorMessage);
    }
}
