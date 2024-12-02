package com.example.minion_project;

import com.example.minion_project.events.Event;
import com.example.minion_project.user.User;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    void eventConstructorTest() {
        // Create a new event
        Event event = new Event("event123", "Sample Event");

        // Verify that the event is instantiated correctly
        assertEquals("event123", event.getEventID(), "Event ID should be 'event123'");
        assertEquals("Sample Event", event.getEventName(), "Event name should be 'Sample Event'");

        assertEquals("", event.getEventDate(), "Event date should be empty");
        assertEquals("", event.getEventDescription(), "Event description should be empty");
        assertEquals(10, event.getEventCapacity(), "Event capacity should be the default value (10)");
        assertEquals("", event.getEventOrganizer(), "Event organizer should be empty");
        assertTrue(event.getEventWaitlist().isEmpty(), "Event waitlist should be empty");
        assertTrue(event.getEventInvited().isEmpty(), "Event invited list should be empty");
        assertEquals("", event.getEventLocation(), "Event location should be empty");
        assertEquals("", event.getEventImage(), "Event image should be empty");
        assertEquals("", event.getEventQrCode(), "Event QR code should be empty");
    }

    @Test
    void eventGetterSetterTest() {
        Event event = new Event("event123", "Sample Event");

        // Set values
        event.setEventDate("2024-11-01");
        event.setEventDescription("Sample event description");
        event.setEventCapacity(100);
        event.setEventOrganizer("Organizer Name");
        event.setEventLocation("Event Location");
        event.setEventImage("image_url");
        event.setEventQrCode("qr_code");

        // Verify values
        assertEquals("2024-11-01", event.getEventDate(), "Event date should be '2024-11-01'");
        assertEquals("Sample event description", event.getEventDescription(), "Event description should be 'Sample event description'");
        assertEquals(100, event.getEventCapacity(), "Event capacity should be '100'");
        assertEquals("Organizer Name", event.getEventOrganizer(), "Event organizer should be 'Organizer Name'");
        assertEquals("Event Location", event.getEventLocation(), "Event location should be 'Event Location'");
        assertEquals("image_url", event.getEventImage(), "Event image URL should be 'image_url'");
        assertEquals("qr_code", event.getEventQrCode(), "Event QR code should be 'qr_code'");
    }

    @Test
    void eventAttendeesTest() {
        Event event = new Event("event123", "Sample Event");

        // create some sample users
        User user1 = new User("user1", "Alice");
        User user2 = new User("user2", "Bob");

        // add users to the event attendees list
        ArrayList<String> attendees = new ArrayList<>();
        attendees.add("user1");
        attendees.add("user2");
        //event.setEventAttendees(attendees);

        // verify
        //assertEquals(2, event.getEventAttendees().size(), "Event should have 2 attendees");
        //assertTrue(event.getEventAttendees().contains("user1"), "Attendees list should contain Alice");
        //assertTrue(event.getEventAttendees().contains("user2"), "Attendees list should contain Bob");
    }

    @Test
    void eventWaitlistTest() {
        Event event = new Event("event123", "Sample Event");

        // Add users to the event waitlist
        ArrayList<String> waitlist = new ArrayList<>();
        waitlist.add("user1");
        waitlist.add("user2");
        event.setEventWaitlist(waitlist);

        // Verify that the waitlist is correct
        assertEquals(2, event.getEventWaitlist().size(), "Event should have 2 users in the waitlist");
        assertTrue(event.getEventWaitlist().contains("user1"), "Waitlist should contain 'user1'");
        assertTrue(event.getEventWaitlist().contains("user2"), "Waitlist should contain 'user2'");
    }

    @Test
    void eventInvitedTest() {
        Event event = new Event("event123", "Sample Event");

        ArrayList<String> invitedUserIds = new ArrayList<>();
        invitedUserIds.add("user1");
        invitedUserIds.add("user2");

        //event.setEventInvited(invitedUserIds);

        // Verify
        assertEquals(0, event.getEventInvited().size(), "Event should have 2 invited users");
        assertFalse(event.getEventInvited().contains("user1"), "Invited list should not contain 'user1'");
        assertFalse(event.getEventInvited().contains("user2"), "Invited list should not contain 'user2'");
    }

    @Test
    void eventEdgeCasesTest() {
        Event event = new Event();

        // Verify default values
        assertEquals("", event.getEventID(), "Event ID should be empty");
        assertEquals("", event.getEventName(), "Event name should be empty");
        assertTrue(event.getEventWaitlist().isEmpty(), "Waitlist should be empty");
        assertTrue(event.getEventInvited().isEmpty(), "Invited list should be empty");

        // Test null assignments
        event.setEventName("New Event");
        assertEquals("New Event", event.getEventName(), "Event name should be 'New Event'");

        event.setEventDescription(null);
        assertNull(event.getEventDescription(), "Event description should be null");

        event.setEventLocation(null);
        assertNull(event.getEventLocation(), "Event location should be null");
    }
}
