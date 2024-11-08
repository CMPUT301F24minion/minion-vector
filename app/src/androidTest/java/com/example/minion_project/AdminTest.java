package com.example.minion_project;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.minion_project.admin.Admin;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.example.minion_project.user.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AdminTest {

    private FireStoreClass db;
    private Admin admin;
    private ArrayList<Event> eventList;
    private EventsAdapter eventsAdapter;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("your-project-id")
                .setApplicationId("your-application-id")
                .setApiKey("your-api-key")
                .build();

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context, options);
        }

        db = new FireStoreClass();
        admin = new Admin("testDeviceID", db);

        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(context, eventList);
    }

    @Test
    public void testRemoveUserProfile() {
        try {
            // Simulate test scenario
            String testDeviceID = "testUserDeviceID_" + System.currentTimeMillis();
            HashMap<String, String> allEvents = new HashMap<>();
            allEvents.put("event1", "attending");

            User testUser = new User(testDeviceID, "Test User", "test@example.com", "1234567890", allEvents, "Test City", new HashMap<>());
            Tasks.await(db.getUsersRef().document(testDeviceID).set(testUser));

            admin.removeUserProfile(testUser);

            // Verify the user was deleted
            DocumentSnapshot deletedUserSnapshot = Tasks.await(db.getUsersRef().document(testDeviceID).get());
            assertFalse(deletedUserSnapshot.exists());

            // Verify that the image was removed
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("user_images/" + testDeviceID + ".jpg");
            try {
                Tasks.await(imageRef.getBytes(1024));
                fail("Image should have been deleted");
            } catch (Exception e) {
                assertTrue(e.getMessage() != null && e.getMessage().contains("Object does not exist"));
            }
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveEvent() {
        try {
            // Simulate test scenario
            String testEventID = "testEventID_" + System.currentTimeMillis();
            Event testEvent = new Event(testEventID, "Test Event");
            testEvent.setEventDescription("This is a test event.");
            testEvent.setEventOrganizer("Test Organizer");
            Tasks.await(db.getEventsRef().document(testEventID).set(testEvent));

            // Add the event to the event list and notify the adapter
            eventList.add(testEvent);
            eventsAdapter.notifyDataSetChanged();

            // Remove the event using the admin method
            admin.removeEvent(testEvent, eventList, eventsAdapter);

            // Verify the event was deleted from Firestore
            DocumentSnapshot deletedEventSnapshot = Tasks.await(db.getEventsRef().document(testEventID).get());
            assertFalse(deletedEventSnapshot.exists());

            // Verify that the event image was removed
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("event_images/" + testEventID + ".jpg");
            try {
                Tasks.await(imageRef.getBytes(1024));
                fail("Event image should have been deleted");
            } catch (Exception e) {
                assertTrue(e.getMessage() != null && e.getMessage().contains("Object does not exist"));
            }

            // Verify that the QR code was removed
            StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference().child("qr_codes/" + testEventID + ".png");
            try {
                Tasks.await(qrCodeRef.getBytes(1024));
                fail("QR code should have been deleted");
            } catch (Exception e) {
                assertTrue(e.getMessage() != null && e.getMessage().contains("Object does not exist"));
            }

            // Verify that the event was removed from the event list
            assertFalse(eventList.contains(testEvent));

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveUserImage() {
        try {
            // Simulate test scenario
            String testDeviceID = "testUserDeviceID_" + System.currentTimeMillis();
            User testUser = new User();
            testUser.setDeviceID(testDeviceID);
            testUser.setName("Test User");  // Corrected from setUserName
            testUser.setEmail("testuser@example.com");  // Corrected from setUserEmail
            Tasks.await(db.getUsersRef().document(testDeviceID).set(testUser));

            // Upload a dummy image for the user
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("user_images/" + testDeviceID + ".jpg");
            byte[] dummyData = "dummy image data".getBytes();
            Tasks.await(imageRef.putBytes(dummyData));

            // Remove user image using the admin method
            admin.removeUserImage(testUser);

            // Verify the user document was deleted
            DocumentSnapshot deletedUserSnapshot = Tasks.await(db.getUsersRef().document(testDeviceID).get());
            assertFalse(deletedUserSnapshot.exists());

            // Verify that the user image was removed
            try {
                Tasks.await(imageRef.getBytes(1024));
                fail("User image should have been deleted");
            } catch (Exception e) {
                assertTrue(e.getMessage() != null && e.getMessage().contains("Object does not exist"));
            }

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }



}