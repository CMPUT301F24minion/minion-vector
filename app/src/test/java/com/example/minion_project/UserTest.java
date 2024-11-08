package com.example.minion_project;

import static org.junit.jupiter.api.Assertions.*;

import com.example.minion_project.events.Event;
import com.example.minion_project.user.User;

import org.junit.jupiter.api.Test;


public class UserTest {
    @Test
    void userInstantiateTest() {
        User u1=new User("testid", "Albert");
        assertTrue(u1.getDeviceID()=="testid");
        assertTrue(2+2==4);
        assertEquals("Albert", u1.getName(), "Name should be 'Albert'");

        // Checking default values for other fields (like email, phone number, etc.)
        assertEquals("", u1.getEmail(), "Email should be empty");
        assertEquals("", u1.getPhoneNumber(), "Phone number should be empty");
        assertEquals("", u1.getLocation(), "City should be empty");
        assertTrue(u1.getAllowNotification(), "AllowNotification should be true by default");

        // Verifying that attendingEvents and waitlistedEvents are initialized as empty
        assertNotNull(u1.getAttendingEvents(), "Attending events should not be null");
        assertTrue(u1.getAttendingEvents().isEmpty(), "Attending events should be empty");
        assertNotNull(u1.getWaitlistedEvents(), "Waitlisted events should not be null");
        assertTrue(u1.getWaitlistedEvents().isEmpty(), "Waitlisted events should be empty");

    }
    @Test
    void userAddEventTest() {
        // create a new user
        User u1 = new User("testid", "Albert");


        Event event = new Event("eventID1", "Sample Event"); // Replace with actual Event constructor

        // add an event to the user
        u1.addEvent(event, "attending");

        // verify that the event was added to the allEvents map
        assertTrue(u1.getAllEvents().containsKey("eventID1"), "Event should be in the allEvents map");
        assertEquals("attending", u1.getAllEvents().get("eventID1"), "Event status should be 'attending'");
    }
    @Test
    void userAddEventTestRejected() {
        User u1 = new User("testid", "Albert");
        Event event = new Event("eventID1", "Sample Event"); // Replace with actual Event constructor
        u1.addEvent(event, "attending");

        // test random id not in keys
        assertFalse(u1.getAllEvents().containsKey("23423423"), "Event should not be in the allEvents map");
    }
}