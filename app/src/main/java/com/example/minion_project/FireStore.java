package com.example.minion_project;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireStore {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("Users");
    private final CollectionReference All_UsersRef = db.collection("All_Users");
    private final CollectionReference organizersRef = db.collection("Organizers");
    private final CollectionReference eventsRef = db.collection("Events");

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
     * method to get all)usersRef
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
     * method to get events ref
     * @return events ref
     */
    public CollectionReference getEventsRef() {
        return eventsRef;
    }
}
