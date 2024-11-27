// Admin.java

package com.example.minion_project.admin;

import android.util.Log;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.minion_project.user.User;


import java.util.ArrayList;

public class Admin {
    public FireStoreClass db;
    public String deviceID;
    public FirebaseStorage storage;

    /**
     * Constructor for Admin class
     *
     * @param deviceID unique identifier for the admin
     * @param db       FireStoreClass instance for Firestore operations
     */
    public Admin(String deviceID, FireStoreClass db) {
        this.deviceID = deviceID;
        this.db = db;
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Get the deviceID of the admin
     *
     * @return deviceID
     */
    public String getDeviceID() {
        return this.deviceID;
    }

    /**
     * Set the deviceID of the admin
     *
     * @param deviceID new deviceID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Remove an event from Firestore, Firebase Storage, and from users and organizers
     *
     * @param event         the event to remove
     * @param eventList     local list of events
     * @param eventsAdapter adapter to notify UI changes
     */
    public void removeEvent(Event event, ArrayList<Event> eventList, EventsAdapter eventsAdapter) {
        CollectionReference eventsRef = db.getEventsRef();

        // Step 1: Delete the event document from Firestore
        eventsRef.document(event.getEventID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Admin", "Event deleted from Firestore: " + event.getEventID());

                    // Step 2: Remove the event image and QR code from Firebase Storage
                    removeEventImageFromStorage(event);
                    removeQRCodeFromStorage(event);

                    // Step 3: Remove event references from users and organizers
                    removeEventFromUsers(event);
                    removeEventFromOrganizer(event);

                    // Step 4: Update eventList and notify the adapter
                    eventList.remove(event);
                    eventsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Admin", "Error deleting event: " + e.getMessage());
                    // Optionally, notify the user via UI
                });
    }

    /**
     * Remove event references from all users
     *
     * @param event the event to remove from users
     */
    private void removeEventFromUsers(Event event) {
        CollectionReference usersRef = db.getUsersRef();

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot userDoc : task.getResult()) {
                    String userId = userDoc.getId();

                    // Check if the user has this event in their events list
                    if (userDoc.contains("Events." + event.getEventID())) {
                        // Remove the event reference from the user's document
                        usersRef.document(userId)
                                .update("Events." + event.getEventID(), FieldValue.delete())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Admin", "Removed event " + event.getEventID() + " from user " + userId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Admin", "Error removing event " + event.getEventID() + " from user " + userId + ": " + e.getMessage());
                                });
                    }
                }
            } else {
                Log.e("Admin", "Error fetching users: " + task.getException());
            }
        });
    }

    /**
     * Remove event reference from the organizer's event list
     *
     * @param event the event to remove from the organizer
     */
    private void removeEventFromOrganizer(Event event) {
        CollectionReference organizersRef = db.getOrganizersRef();

        organizersRef.document(event.getEventOrganizer())
                .update("Events", FieldValue.arrayRemove(event.getEventID()))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Admin", "Removed event " + event.getEventID() + " from organizer " + event.getEventOrganizer());
                })
                .addOnFailureListener(e -> {
                    Log.e("Admin", "Error removing event " + event.getEventID() + " from organizer " + event.getEventOrganizer() + ": " + e.getMessage());
                });
    }

    /**
     * Remove event image from Firebase Storage
     *
     * @param event the event whose image is to be removed
     */
    public void removeEventImageFromStorage(Event event) {
        String imagePath = "event_images/" + event.getEventID() + ".jpg";
        StorageReference imageRef = storage.getReference().child(imagePath);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("Admin", "Event image deleted from Storage: " + imagePath);
        }).addOnFailureListener(exception -> {
            Log.e("Admin", "Error deleting event image: " + exception.getMessage());
        });
    }

    /**
     * Remove QR code from Firebase Storage
     *
     * @param event the event whose QR code is to be removed
     */
    public void removeQRCodeFromStorage(Event event) {
        String qrCodePath = "qr_codes/" + event.getEventID() + ".png";
        StorageReference qrCodeRef = storage.getReference().child(qrCodePath);

        qrCodeRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("Admin", "QR code deleted from Storage: " + qrCodePath);
        }).addOnFailureListener(exception -> {
            Log.e("Admin", "Error deleting QR code: " + exception.getMessage());
        });
    }

    /**
     * Remove user images from storage (existing method)
     *
     * @param user the user whose image is to be removed
     */
    public void removeUserImage(User user) {
        CollectionReference userRef = db.getUsersRef();
        CollectionReference all_userRef = db.getAll_UsersRef();
        userRef.document(user.getDeviceID()).delete();

        all_userRef.document(user.getDeviceID()).delete();

        removeUserImageFromStorage(user);
    }

    /**
     * Remove user image from Firebase Storage
     *
     * @param user the user whose image is to be removed
     */
    public void removeUserImageFromStorage(User user) {
        String imagePath = "user_images/" + user.getDeviceID() + ".jpg";
        StorageReference imageRef = storage.getReference().child(imagePath);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("Admin", "User image deleted from Storage: " + imagePath);
        }).addOnFailureListener(exception -> {
            Log.e("Admin", "Error deleting user image: " + exception.getMessage());
        });
    }

    /**
     * Remove user profile (existing method)
     *
     * @param user the user profile to remove
     */
    public void removeUserProfile(User user) {
        String userDeviceID = user.getDeviceID();

        // Delete user documents from Firestore
        CollectionReference userRef = db.getUsersRef();
        CollectionReference allUserRef = db.getAll_UsersRef();

        userRef.document(user.getDeviceID()).delete();

        allUserRef.document(user.getDeviceID()).delete();

        // Delete user profile image from Firebase Storage
        removeUserImageFromStorage(user);
    }
}
