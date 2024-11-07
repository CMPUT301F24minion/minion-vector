package com.example.minion_project;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.user.User;
import com.example.minion_project.user.UserController;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;

public class UserControllerTest {

    private UserController userController;
    private User mockUser;
    private Event mockEvent;
    private FireStoreClass mockFireStoreClass;
    private CollectionReference mockCollectionReference;
    private DocumentReference mockDocumentReference;

    @BeforeEach
    void setUp() {
        // Create mock objects for dependencies

        mockUser = mock(User.class);
        mockEvent = mock(Event.class);
        mockFireStoreClass = mock(FireStoreClass.class);
        mockCollectionReference = mock(CollectionReference.class);
        mockDocumentReference = mock(DocumentReference.class);

        // Setup mock FireStoreClass to return a mocked CollectionReference
        when(mockFireStoreClass.getUsersRef()).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        // Create an instance of UserController with the mocked User
        userController = new UserController(mockUser);
    }

    /**
     * Tests that the user can successfully join an event when they have not joined it yet.
     */
    @Test
    void testJoinEvent_success() {
        // Setup the behavior for mockEvent and mockUser
        String eventId = "testEventId";
        String eventName = "Test Event";

        when(mockEvent.getEventID()).thenReturn(eventId);
        when(mockEvent.getEventName()).thenReturn(eventName);
        when(mockUser.getAllEvents()).thenReturn(new HashMap<>());
        when(mockUser.getDeviceID()).thenReturn("userDeviceID");

        // Call the join_event method
        Boolean result = userController.join_event(mockEvent);

        // Assert that the result is true (event was joined successfully)
        assertTrue(result);

        // Verify that the Firestore update method was called
        verify(mockDocumentReference, times(1)).update(eq("Events." + eventId), eq("joined"));
        verify(mockUser, times(1)).addEvent(mockEvent, "joined");
    }

    /**
     * Tests that the user cannot join an event they have already joined.
     */
    @Test
    void testJoinEvent_alreadyJoined() {
        // Setup the behavior for mockEvent and mockUser
        String eventId = "testEventId";
        String eventName = "Test Event";

        when(mockEvent.getEventID()).thenReturn(eventId);
        when(mockEvent.getEventName()).thenReturn(eventName);

        // Simulate that the user has already joined the event
        Map<String, String> userEvents = new HashMap<>();
        userEvents.put(eventId, "joined");
        when(mockUser.getAllEvents()).thenReturn((HashMap<String, String>) userEvents);
        when(mockUser.getDeviceID()).thenReturn("userDeviceID");

        // Call the join_event method
        Boolean result = userController.join_event(mockEvent);

        // Assert that the result is false (user has already joined the event)
        assertFalse(result);

        // Verify that Firestore was not updated
        verify(mockDocumentReference, never()).update(anyString(), any());
        verify(mockUser, never()).addEvent(any(), any());
    }

    /**
     * Tests that the user cannot join an event with invalid data.
     */
    @Test
    void testJoinEvent_invalidEvent() {
        // Setup the behavior for mockEvent and mockUser
        when(mockEvent.getEventID()).thenReturn(null); // Invalid Event ID
        when(mockEvent.getEventName()).thenReturn("Test Event");
        when(mockUser.getAllEvents()).thenReturn(new HashMap<>());
        when(mockUser.getDeviceID()).thenReturn("userDeviceID");

        // Call the join_event method with invalid data
        Boolean result = userController.join_event(mockEvent);

        // Assert that the result is false (event data is invalid)
        assertFalse(result);

        // Verify that Firestore was not updated
        verify(mockDocumentReference, never()).update(anyString(), any());
        verify(mockUser, never()).addEvent(any(), any());
    }

    /**
     * Tests that the join_event method returns false if the event name is empty.
     */
    @Test
    void testJoinEvent_emptyEventName() {
        // Setup the behavior for mockEvent and mockUser
        when(mockEvent.getEventID()).thenReturn("testEventId");
        when(mockEvent.getEventName()).thenReturn(""); // Invalid empty Event Name
        when(mockUser.getAllEvents()).thenReturn(new HashMap<>());
        when(mockUser.getDeviceID()).thenReturn("userDeviceID");

        // Call the join_event method with invalid event name
        Boolean result = userController.join_event(mockEvent);

        // Assert that the result is false (invalid event name)
        assertFalse(result);

        // Verify that Firestore was not updated
        verify(mockDocumentReference, never()).update(anyString(), any());
        verify(mockUser, never()).addEvent(any(), any());
    }
}
