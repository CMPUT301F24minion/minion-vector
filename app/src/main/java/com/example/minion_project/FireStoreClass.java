package com.example.minion_project;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handler for database communications.
 * Provides methods to access collections and search for documents by ID.
 */
public class FireStoreClass {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("Users");
    private final CollectionReference All_UsersRef = db.collection("All_Users");
    private final CollectionReference organizersRef = db.collection("Organizers");
    private final CollectionReference eventsRef = db.collection("Events");

    /**
     * Method to get FirebaseFirestore instance.
     * @return FirebaseFirestore instance.
     */
    public FirebaseFirestore getFirestore() {
        return db;
    }

    /**
     * Method to get Users collection reference.
     * @return Users CollectionReference.
     */
    public CollectionReference getUsersRef() {
        return usersRef;
    }

    /**
     * Method to get All_Users collection reference.
     * @return All_Users CollectionReference.
     */
    public CollectionReference getAll_UsersRef() {
        return All_UsersRef;
    }

    /**
     * Method to get Organizers collection reference.
     * @return Organizers CollectionReference.
     */
    public CollectionReference getOrganizersRef() {
        return organizersRef;
    }

    /**
     * Method to get Events collection reference.
     * @return Events CollectionReference.
     */
    public CollectionReference getEventsRef() {
        return eventsRef;
    }

    /**
     * Searches for a user document by ID in the Users collection.
     * @param documentId The document ID to search for.
     * @return Task containing DocumentSnapshot of the document, if found.
     */
    public Task<DocumentSnapshot> searchUserById(String documentId) {
        return usersRef.document(documentId).get();
    }

    /**
     * Searches for a user document by ID in the All_Users collection.
     * @param documentId The document ID to search for.
     * @return Task containing DocumentSnapshot of the document, if found.
     */
    public Task<DocumentSnapshot> searchAllUsersById(String documentId) {
        return All_UsersRef.document(documentId).get();
    }

    /**
     * Searches for an organizer document by ID in the Organizers collection.
     * @param documentId The document ID to search for.
     * @return Task containing DocumentSnapshot of the document, if found.
     */
    public Task<DocumentSnapshot> searchOrganizerById(String documentId) {
        return organizersRef.document(documentId).get();
    }

    /**
     * Searches for an event document by ID in the Events collection.
     * @param documentId The document ID to search for.
     * @return Task containing DocumentSnapshot of the document, if found.
     */
    public Task<DocumentSnapshot> searchEventById(String documentId) {
        return eventsRef.document(documentId).get();
    }
}
