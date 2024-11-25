/**
 * EventController: handles various operations related to events
 * such as fetching event details, adding users to the event waitlist, and removing users from the waitlist.
 */

package com.example.minion_project.events;

import android.util.Log;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.Lottery.Lottery;
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
    /**
     * Constructor for EventController class
     */
    public EventController() {
        this.eventsRef=Our_Firestore.getEventsRef();

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

                    ArrayList<String> userNames = new ArrayList<>();
                    for (String userId : userIds) {
                        Log.d("EventController", "Fetching user: " + userId);

                        Our_Firestore.getUsersRef().document(userId.trim()).get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String userName = userDoc.getString("Name");
                                        Log.d("EventController", "Fetched user name: " + userName);
                                        if (userName != null) {
                                            userNames.add(userName);
                                        }
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
        void onUserListFetched(ArrayList<String> userNames);

        void onError(String errorMessage);
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
