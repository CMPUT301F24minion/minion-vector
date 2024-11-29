/**
 * EventController: handles various operations related to events
 * such as fetching event details, adding users to the event waitlist, and removing users from the waitlist.
 */

package com.example.minion_project.events;

import static java.lang.Boolean.TRUE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.Lottery.Lottery;
import com.example.minion_project.Notification;
import com.example.minion_project.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EventController {
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private Event event;
    private Notification notification;
    /**
     * Constructor for EventController class
     */
    public EventController() {
        this.eventsRef=Our_Firestore.getEventsRef();
        this.usersRef=Our_Firestore.getUsersRef();
        notification=new Notification();

    }

    /**
     * getEvent: fetches an event from Firestore based on the provided event ID and calls the callback
     * @param eventId
     * @param callback callback to be executed once event is fetched
     * @return the fetched event
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

    /**
     * Adds a user to a waitlist of a event
     * @param event event that user will be added to
     * @param UserId userID that will be added to event
     */
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
    /**
     * addToEventInvited:  a method to add a user to event inivted list
     * @param event
     * @param userId
     * @return None
     */
    public void addToEventInvited(Event event,String userid){
        eventsRef.document(event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Use Firestore's arrayUnion to add "complete" to the eventWaitlist array
                    eventsRef.document(event.getEventID())
                            .update("eventInvited", FieldValue.arrayUnion(userid))
                            .addOnSuccessListener(aVoid -> {
                                // Successfully added to the waitlist
                                Log.d("EventUpdate", "Successfully added  to the invited.");
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Log.e("EventUpdate", "Error adding 'complete' to the invited", e);
                            });
                } else {
                    Log.e("EventUpdate", "Event document does not exist.");
                }
            } else {
                Log.e("EventUpdate", "Failed to fetch event document", task.getException());
            }
        });
    }
    /**
     * addToEventsRejected:  a method to add a user to event rejected list+ pool from lottery
     * @param event
     * @param userId
     * @return None
     */
    public void addToEventsRejected(Event event,String userid){

        eventsRef.document(event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Use Firestore's arrayUnion to add "complete" to the eventWaitlist array
                    eventsRef.document(event.getEventID())
                            .update("eventDeclined", FieldValue.arrayUnion(userid))
                            .addOnSuccessListener(aVoid -> {
                                // Successfully added to the waitlist
                                Lottery lottery=new Lottery(event);
                                lottery.poolApplicants(1); // pool an additionalApplicant
                                Log.d("EventUpdate", "Successfully added  to the declined.");
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Log.e("EventUpdate", "Error adding 'complete' to the declined", e);
                            });
                } else {
                    Log.e("EventUpdate", "Event document does not exist.");
                }
            } else {
                Log.e("EventUpdate", "Failed to fetch event document", task.getException());
            }
        });
    }
    /**
     * Removes a user from a waitlist of a event
     * @param event event that user will be removed from
     * @param UserID userID that will be removed from event
     */
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
    /**
     * add Events to Enrolled
     * @param event event that user will be removed from
     * @param UserID userID that will be removed from event
     */
    public void addToEventsEnrolled(Event event, String userid) {
        eventsRef.document(event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Use Firestore's arrayUnion to add "complete" to the eventWaitlist array
                    eventsRef.document(event.getEventID())
                            .update("eventEnrolled", FieldValue.arrayUnion(userid))
                            .addOnSuccessListener(aVoid -> {
                                // Successfully added to the waitlist
                                Log.d("EventUpdate", "Successfully added  to the enrolled.");
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Log.e("EventUpdate", "Error adding 'complete' to the enrolled", e);
                            });
                } else {
                    Log.e("EventUpdate", "Event document does not exist.");
                }
            } else {
                Log.e("EventUpdate", "Failed to fetch event document", task.getException());
            }
        });
    }
    /**
     * remove Events from invited
     * @param event event that user will be removed from
     * @param UserID userID that will be removed from event
     */
    public void removeFromInvited(Event event, String UserID) {
        eventsRef.document(event.getEventID()).update("eventInvited", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(aVoid -> {
                    // Log success message
                    Log.d("Firestore", "User " + UserID + " successfully removed from the invited for event " + event.getEventID());
                })
                .addOnFailureListener(e -> {
                    // Log failure message
                    Log.e("Firestore", "Error removing user " + UserID + " from the invited for event " + event.getEventID(), e);
                });
    }

    public void fetchUserNamesFromList(String eventId, String listField, UserListCallback callback) {
        eventsRef.document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> userIds = (ArrayList<String>) document.get(listField);
                    Log.d("EventController", "User IDs: " + userIds);

                    if (userIds == null || userIds.isEmpty()) {
                        callback.onError("No users found in " + listField);
                        return;
                    }

                    ArrayList<Map.Entry<String, String>> userNames = new ArrayList<>();
                    for (String userId : userIds) {
                        Log.d("EventController", "Fetching user: " + userId);

                        Our_Firestore.getUsersRef().document(userId.trim()).get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String userName = userDoc.getString("Name");
                                        Log.d("EventController", "Fetched user name: " + userName);
                                        if (userName != null) {
                                            userNames.add(new AbstractMap.SimpleEntry<>(userName, userId));                                         }
                                    } else {
                                        Log.d("EventController", "User not found: " + userId);
                                    }

                                    // Check if all users are processed
                                    if (userNames.size() == userIds.size()) {
                                        Log.d("EventController", "All user names fetched: " + userNames);
                                        callback.onUserListFetched(userNames);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EventController", "Failed to fetch user: " + userId, e);
                                    callback.onError("Failed to fetch user: " + userId);
                                });
                    }
                } else {
                    callback.onError("Event not found");
                }
            } else {
                callback.onError("Failed to fetch event data: " + task.getException().getMessage());
            }
        });
    }

    // Callback interface for user list fetching
    public interface UserListCallback {
        void onUserListFetched(ArrayList<Map.Entry<String, String>>  userNames);

        void onError(String errorMessage);
    }
    /**
     * Function to start the event
     */
    public  void startEvent(Event event){
        //set the event start to true
        eventsRef.document(event.getEventID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set the event start to true
                        eventsRef.document(event.getEventID()).update("eventStart", TRUE);

                        // Move everyone in the waitlist to rejected and clear waitlist
                        ArrayList<String> waitlist = event.getEventWaitlist();
                        ArrayList<String> rejected = event.getEventRejected();
                        rejected.addAll(waitlist);  // Add all waitlisted users to rejected
                        waitlist.clear();  // Clear the waitlist

                        // Move everyone in the invited list to rejected and clear invited
                        ArrayList<String> invited = event.getEventInvited();
                        rejected.addAll(invited);  // Add all invited users to rejected
                        invited.clear();  // Clear the invited list

                        // Update the Firestore document with the updated lists
                        eventsRef.document(event.getEventID()).update(
                                "eventWaitlist", waitlist,
                                "eventInvited", invited,
                                "eventRejected", rejected
                        );


                        for (String userId : rejected) {
                            rejecetUser(event.getEventID(),userId);
                            notification.addUserToNotificationDocument("lost_lottery", userId);
                        }
                    } else {
                        Log.e("Event", "Event not found in Firestore");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Event", "Error getting event data", e);
                });
    }


public void rejecetUser(String eventId, String userId){
    usersRef.document(userId).get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();

            if (document.exists()) {
                // Retrieve current user data
                Map<String, Object> userData = document.getData();

                // Check if the "Events" field exists and is a map
                if (userData != null && userData.containsKey("Events") && userData.get("Events") instanceof Map) {
                    Map<String, String> eventsMap = (Map<String, String>) userData.get("Events");

                    // Check if the user has the event in their list
                    if (eventsMap.containsKey(eventId)) {
                        // Update the event status to "rejected"
                        eventsMap.put(eventId, "rejected");

                        // Update the document with the new status
                        usersRef.document(userId)
                                .update("Events", eventsMap)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("User Update", "Event status updated to rejected for user " + userId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("User Update Error", "Failed to update event status: ", e);
                                });
                    } else {
                        Log.d("User Update", "Event not found in user's events");
                    }
                }
            } else {
                // User document not found
                Log.e("User Update Error", "User not found in database");
            }
        } else {
            Log.e("User Fetch Error", "Error fetching user data: ", task.getException());
        }
    });
}
    public void getLocation(ArrayList<String> waitlistUsers, LocationCallback callback) {
        ArrayList<HashMap<String, Double>> ans = new ArrayList<>();
        int totalUsers = waitlistUsers.size();
        AtomicInteger completedRequests = new AtomicInteger(0); // To track completed requests

        for (String uid : waitlistUsers) {

            usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();

                            if (data != null) {

                                Double latitude = (Double) data.get("latitude");
                                Double longitude = (Double) data.get("longitude");

                                if (latitude != null && longitude != null) {
                                    // Create a map to store the location data
                                    HashMap<String, Double> locationData = new HashMap<>();
                                    locationData.put("latitude", latitude);
                                    locationData.put("longitude", longitude);

                                    // Add the map to the result list
                                    ans.add(locationData);
                                    Log.d("MapsActivity", "Location Data: " + locationData.toString());
                                }
                            }
                        }
                    } else {
                        Log.e("MapsActivity", "Error fetching data for user: " + uid, task.getException());
                    }

                    // Increment the completed requests counter
                    if (completedRequests.incrementAndGet() == totalUsers) {
                        // Once all requests are completed, call the callback
                        Log.d("MapsActivity", ans.toString());

                        callback.onLocationFetched(ans);
                    }
                }
            });
        }

    }

    public interface LocationCallback {
        void onLocationFetched(ArrayList<HashMap<String, Double>> locations);
    }

/**
     * EventCallback interface for handling event-related callbacks
     */
    public interface EventCallback {
        /**
         * Called when an event is successfully fetched
         * @param event
         */
        void onEventFetched(Event event);

        /**
         * Called when an error occurs during event fetching
         * @param errorMessage
         */
        void onError(String errorMessage);
    }
}
