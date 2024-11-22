package com.example.minion_project;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for database communications.
 * Is able to return a distinct database reference, and establishes cleaner and more concise means to query and store
 * data within the collections.
 */
public class FireStoreClass {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("Users");
    private final CollectionReference All_UsersRef = db.collection("All_Users");
    private final CollectionReference organizersRef = db.collection("Organizers");
    private final CollectionReference eventsRef = db.collection("Events");
    private final CollectionReference notRef = db.collection("Notifications");

    /**
     * method to get firestore
     * @return db
     */
    public FirebaseFirestore getFirestore(){
        return db;
    }
    /**
     * method to get usersRef
     * @return useresref
     */
    public CollectionReference getUsersRef(){
        return usersRef;
    }
    /**
     * method to get allUsersRef
     * @return all_useresref
     */
    public CollectionReference getAll_UsersRef() {
        return All_UsersRef;
    }
    /**
     * method to get organizer ref
     * @return organizer ref
     */
    public CollectionReference getOrganizersRef() {
        return organizersRef;
    }
    /**
     * Retrieves the facility name and image URL from the organizer document.
     * @param ID The document ID of the organizer.
     * @return A Task<Map<String, String>> containing the "Name" and "ImageURL" fields.
     */
    public Task<Map<String, String>> getFacilityInfo(String ID) {
        return organizersRef.document(ID)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        String name = document.getString("Name");
                        String imageURL = document.getString("ImageURL");

                        // Create a map to hold the Name and ImageURL
                        Map<String, String> facilityData = new HashMap<>();
                        facilityData.put("Name", name);
                        facilityData.put("ImageURL", imageURL);

                        return facilityData; // Return the map
                    }
                    return null; // Return null if the document does not exist or is invalid
                });
    }
    /**
     * method to get events ref
     * @return events ref
     */
    public CollectionReference getEventsRef() {
        return eventsRef;
    }
    /**
     * Checks if a document with the given androidID exists in the notifications collection.
     * @param androidID The Android ID to search for.
     * @return A Task<Boolean> indicating whether the ID exists.
     */
    public Task<List<Map<String, Object>>> getNotificationsAsList(String androidID) {
        return notRef.whereEqualTo("androidID", androidID)
                .get()
                .continueWith(task -> {
                    List<Map<String, Object>> notifications = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            notifications.add(document.getData());
                        }
                    }
                    return notifications;
                });
    }
}
