// Admin.java
package com.example.minion_project.admin;

import com.example.minion_project.user.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Admin extends User {

    // Constructors
    public Admin() {
        super();
    }

    public Admin(String deviceID, String name, String email, String phoneNumber) {
        super(deviceID, name, email, phoneNumber);
    }

    // Remove Event
    public Task<Void> deleteEvent(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("Events");
        return eventsRef.document(eventId).delete();
    }

    // Remove User Profile
    public Task<Void> deleteUserProfile(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        return usersRef.document(userId).delete();
    }

    // Remove Image
    public Task<Void> deleteImage(String imageId, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imagesRef = db.collection("Images");

        // Delete the image document from Firestore
        Task<Void> firestoreTask = imagesRef.document(imageId).delete();

        // Delete the actual image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        Task<Void> storageTask = imageRef.delete();

        // Return a combined task
        return Tasks.whenAll(firestoreTask, storageTask);
    }

    // Remove QR Code Data
    public Task<Void> deleteQrCodeData(String qrCodeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCodesRef = db.collection("QR_Codes");
        return qrCodesRef.document(qrCodeId).delete();
    }

    // Remove Facility
    public Task<Void> deleteFacility(String facilityId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facilitiesRef = db.collection("Facilities");
        return facilitiesRef.document(facilityId).delete();
    }

    // Browse Events
    public Task<QuerySnapshot> fetchAllEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("Events");
        return eventsRef.get();
    }

    // Browse User Profiles
    public Task<QuerySnapshot> fetchAllUserProfiles() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        return usersRef.get();
    }

    // Browse Images
    public Task<QuerySnapshot> fetchAllImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imagesRef = db.collection("Images");
        return imagesRef.get();
    }

    // Browse QR Code Data
    public Task<QuerySnapshot> fetchAllQrCodeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCodesRef = db.collection("QR_Codes");
        return qrCodesRef.get();
    }

    // Browse Facilities
    public Task<QuerySnapshot> fetchAllFacilities() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facilitiesRef = db.collection("Facilities");
        return facilitiesRef.get();
    }
}
