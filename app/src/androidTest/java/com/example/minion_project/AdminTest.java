package com.example.minion_project;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.*;

import com.example.minion_project.admin.Admin;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.example.minion_project.facility.Facility;
import com.example.minion_project.user.User;
import com.example.minion_project.FireStoreClass;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * AdminTest class to test admin functionalities using the actual database.
 */
@RunWith(AndroidJUnit4.class)
public class AdminTest {

    private static FireStoreClass fireStoreClass;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static Admin admin;

    @BeforeClass
    public static void setup() {
        // Initialize Firestore and FirebaseStorage
        fireStoreClass = new FireStoreClass();
        db = fireStoreClass.getFirestore();
        storage = FirebaseStorage.getInstance();
        admin = new Admin("admin_device_id", fireStoreClass);
    }

    @Test
    public void testDeleteEvent() throws InterruptedException {
        // ... Existing test code ...
    }

    @Test
    public void testToggleQrCode() throws InterruptedException {
        // ... Existing test code ...
    }

    @Test
    public void testDeleteImage() throws InterruptedException {
        // ... Existing test code ...
    }

    @Test
    public void testDeleteUser() throws InterruptedException {
        // ... Existing test code ...
    }

    @Test
    public void testRemoveUserImage() throws InterruptedException {
        // Create a test user with a unique device ID
        User testUser = new User("test_user_image_id", "Test User Image");

        // Upload the test user image to Firebase Storage at the expected path
        String testImageUrl = uploadTestUserImage(testUser);

        assertNotNull("Test user image URL should not be null", testImageUrl);

        // Save the user to Firestore (optional)
        CountDownLatch writeLatch = new CountDownLatch(1);
        db.collection("Users").document(testUser.getDeviceID()).set(testUser)
                .addOnSuccessListener(aVoid -> writeLatch.countDown())
                .addOnFailureListener(e -> {
                    fail("Failed to create test user with image: " + e.getMessage());
                    writeLatch.countDown();
                });

        // Wait for the user to be saved
        assertTrue("Timeout while saving test user with image", writeLatch.await(10, TimeUnit.SECONDS));

        // Delete the user's image using admin
        admin.removeUserImage(testUser);

        // Wait for the deletion to complete
        Thread.sleep(5000);

        // Verify that the user's image is deleted from Storage
        CountDownLatch verifyLatch = new CountDownLatch(1);
        storage.getReference().child("user_images/" + testUser.getDeviceID() + ".jpg").getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    fail("User image was not deleted");
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    // Image deleted successfully
                    assertTrue(true);
                    verifyLatch.countDown();
                });

        assertTrue("Timeout while verifying user image deletion", verifyLatch.await(10, TimeUnit.SECONDS));

        // Clean up by deleting the test user from Firestore
        CountDownLatch cleanupLatch = new CountDownLatch(1);
        admin.removeUserProfile(testUser);
        cleanupLatch.await(10, TimeUnit.SECONDS);

        // Optionally, verify that the user is deleted from Firestore
        CountDownLatch verifyUserDeletionLatch = new CountDownLatch(1);
        db.collection("Users").document(testUser.getDeviceID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertFalse("User should be deleted from 'Users' collection", documentSnapshot.exists());
                    verifyUserDeletionLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify user deletion after cleanup: " + e.getMessage());
                    verifyUserDeletionLatch.countDown();
                });

        assertTrue("Timeout while verifying user deletion after cleanup", verifyUserDeletionLatch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testDeleteFacility() throws InterruptedException {
        // Create a test facility
        Facility testFacility = new Facility();
        testFacility.setFacilityID("Test Facility");
        testFacility.setDocumentID(db.collection("Facilities").document().getId());

        // Save the facility to Firestore
        CountDownLatch facilityLatch = new CountDownLatch(1);
        db.collection("Facilities").document(testFacility.getDocumentID()).set(testFacility)
                .addOnSuccessListener(aVoid -> facilityLatch.countDown())
                .addOnFailureListener(e -> {
                    fail("Failed to create test facility: " + e.getMessage());
                    facilityLatch.countDown();
                });

        // Wait for the facility to be saved
        assertTrue("Timeout while saving test facility", facilityLatch.await(10, TimeUnit.SECONDS));

        // Create a test event associated with the facility
        Event testEvent = new Event();
        testEvent.setEventName("Test Event for Facility");
        testEvent.setEventOrganizer(testFacility.getDocumentID()); // Associate with facility
        testEvent.setEventID(db.collection("Events").document().getId());

        // Save the event to Firestore
        CountDownLatch eventLatch = new CountDownLatch(1);
        db.collection("Events").document(testEvent.getEventID()).set(testEvent)
                .addOnSuccessListener(aVoid -> eventLatch.countDown())
                .addOnFailureListener(e -> {
                    fail("Failed to create test event: " + e.getMessage());
                    eventLatch.countDown();
                });

        // Wait for the event to be saved
        assertTrue("Timeout while saving test event", eventLatch.await(10, TimeUnit.SECONDS));

        // Delete the facility and its associated events
        deleteFacilityAndEvents(testFacility);

        // Wait for deletions to complete
        Thread.sleep(5000);

        // Verify that the facility is deleted
        CountDownLatch verifyFacilityLatch = new CountDownLatch(1);
        db.collection("Facilities").document(testFacility.getDocumentID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertFalse("Facility should be deleted", documentSnapshot.exists());
                    verifyFacilityLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify facility deletion: " + e.getMessage());
                    verifyFacilityLatch.countDown();
                });

        assertTrue("Timeout while verifying facility deletion", verifyFacilityLatch.await(10, TimeUnit.SECONDS));

        // Verify that the associated event is deleted
        CountDownLatch verifyEventLatch = new CountDownLatch(1);
        db.collection("Events").document(testEvent.getEventID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertFalse("Associated event should be deleted", documentSnapshot.exists());
                    verifyEventLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify event deletion: " + e.getMessage());
                    verifyEventLatch.countDown();
                });

        assertTrue("Timeout while verifying event deletion", verifyEventLatch.await(10, TimeUnit.SECONDS));
    }

    private void deleteFacilityAndEvents(Facility facility) throws InterruptedException {
        CountDownLatch deletionLatch = new CountDownLatch(1);

        // Delete associated events
        db.collection("Events").whereEqualTo("eventOrganizer", facility.getDocumentID()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        batch.delete(document.getReference());
                    }
                    // Delete the facility
                    batch.delete(db.collection("Facilities").document(facility.getDocumentID()));
                    // Commit the batch
                    batch.commit()
                            .addOnSuccessListener(aVoid -> deletionLatch.countDown())
                            .addOnFailureListener(e -> {
                                fail("Failed to delete facility and events: " + e.getMessage());
                                deletionLatch.countDown();
                            });
                })
                .addOnFailureListener(e -> {
                    fail("Failed to fetch associated events: " + e.getMessage());
                    deletionLatch.countDown();
                });

        assertTrue("Timeout during facility and events deletion", deletionLatch.await(20, TimeUnit.SECONDS));
    }




    private String uploadTestUserImage(User user) throws InterruptedException {
        // Upload a sample user image to Firebase Storage at the path used by removeUserImage
        String imagePath = "user_images/" + user.getDeviceID() + ".jpg";
        StorageReference storageRef = storage.getReference().child(imagePath);
        byte[] data = "This is a test user image".getBytes();

        CountDownLatch uploadLatch = new CountDownLatch(1);
        final String[] imageUrl = {null};
        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl[0] = uri.toString();
                        uploadLatch.countDown();
                    }).addOnFailureListener(e -> {
                        fail("Failed to get download URL for user image: " + e.getMessage());
                        uploadLatch.countDown();
                    });
                })
                .addOnFailureListener(e -> {
                    fail("Failed to upload test user image: " + e.getMessage());
                    uploadLatch.countDown();
                });

        assertTrue("Timeout while uploading test user image", uploadLatch.await(20, TimeUnit.SECONDS));
        return imageUrl[0];
    }

    @AfterClass
    public static void tearDown() {
        // Clean up any remaining test data if necessary
    }
}
