/** Class that represents the Admin class
 * this class handles the functionalities of removing all the events, images and profiles that breaks the app policy
 *
 */
package com.example.minion_project.admin;

import com.example.minion_project.events.EventsAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.user.User;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class Admin {
    public FireStoreClass db;
    public String deviceID;
    public FirebaseStorage storage;

    /**
     * constructor for Admin class
     * @param deviceID
     * @param db
     */

    public Admin(String deviceID, FireStoreClass db) {
        this.deviceID = deviceID;
        this.db = db;
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     *
     * @return returns the deviceID uniquely associated for each Admin
     */
    public String getDeviceID() {
        return this.deviceID;
    }

    /**
     *
     * @param deviceID sets the deviceID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     *
     * @param event gets the event to remove
     * @param eventList the place to search for the event
     * @param eventsAdapter update the adapter
     */
    public void removeEvent(Event event, ArrayList<Event> eventList, EventsAdapter eventsAdapter) {
        CollectionReference eventsRef = db.getEventsRef();

        // Step 1: Delete the event document from Firestore
        eventsRef.document(event.getEventID())
                .delete();

                    // Step 2: Remove the event image from Firebase Storage
                    removeEventImageFromStorage(event);
                    removeQRCodeFromStorage(event);

                    // Step 3: Update eventList and notify the adapter
                    eventList.remove(event);
                    eventsAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param user every image is linked to a user
     */
    public void removeUserImage(User user) {
        CollectionReference userRef = db.getUsersRef();
        CollectionReference all_userRef = db.getAll_UsersRef();
        userRef.document(user.getDeviceID()).delete();

        all_userRef.document(user.getDeviceID()).delete();

        removeUserImageFromStorage(user);
    }

    /**
     *
     * @param user get the deviceID from the user to remove the user image form storage
     */
    public void removeUserImageFromStorage(User user) {
        // Assuming the image path is based on user device ID
        String imagePath = "user_images/" + user.getDeviceID() + ".jpg";
        StorageReference imageRef = storage.getReference().child(imagePath);

        imageRef.delete();
    }

    /**
     *
     * @param event every event has an image linked
     */

    public void removeEventImage(Event event) {
        CollectionReference eventsRef = db.getEventsRef();
        eventsRef.document(event.getEventID())
                .delete();
    }

    /**
     *
     * @param event reference to the event
     */
    public void removeEventImageFromStorage(Event event) {
        String imagePath = "event_images/" + event.getEventID() + ".jpg";
        StorageReference imageRef = storage.getReference().child(imagePath);
        imageRef.delete();
    }

    /**
     *
     * @param event reference to the event
     */
    public void removeQRCodeFromStorage(Event event) {
        String qrCodePath = "qr_codes/" + event.getEventID() + ".png";
        StorageReference qrCodeRef = storage.getReference().child(qrCodePath);

        qrCodeRef.delete();
    }

    /**
     *
     * @param user reference to the user to ge t the deviceID
     */
    public void removeUserProfile(User user) {
        // Step 1: Get Firestore references for the user
        CollectionReference userRef = db.getUsersRef();
        CollectionReference allUserRef = db.getAll_UsersRef();

        // Step 2: Delete the user document from Firestore
        userRef.document(user.getDeviceID()).delete();

        allUserRef.document(user.getDeviceID()).delete();

        // Step 3: Delete user profile image from Firebase Storage
        removeUserImageFromStorage(user);
    }
}