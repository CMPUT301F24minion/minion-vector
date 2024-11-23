package com.example.minion_project;

import static org.junit.jupiter.api.Assertions.*;

import com.example.minion_project.organizer.Organizer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class OrganizerTest {
    @Test
    void organizerConstructorTest() {
        ArrayList<String> events = new ArrayList<>();
        events.add("Event1");
        events.add("Event2");

        Organizer organizer = new Organizer(events, "1234567890", "organizer@example.com", "John Doe", "device123", false);

        // verifying that the organizer fields are correctly
        assertEquals("device123", organizer.getDeviceID(), "Device ID should be 'device123'");
        assertEquals("John Doe", organizer.getName(), "Name should be 'John Doe'");
        assertEquals("organizer@example.com", organizer.getEmail(), "Email should be 'organizer@example.com'");
        assertEquals("1234567890", organizer.getPhoneNumber(), "Phone number should be '1234567890'");

        // verifying that the events are correct
        assertNotNull(organizer.getAllEvents(), "All events list should not be null");
        assertEquals(2, organizer.getAllEvents().size(), "The number of events should be 2");
        assertTrue(organizer.getAllEvents().contains("Event1"), "Event list should contain 'Event1'");
        assertTrue(organizer.getAllEvents().contains("Event2"), "Event list should contain 'Event2'");
    }

    @Test
    void organizerGetterSetterTest() {
        // creating an organizer with an empty event list
        Organizer organizer = new Organizer(new ArrayList<>(), "1234567890", "organizer@example.com", "John Doe", "device123", false);

        // verifying initial val
        assertEquals("device123", organizer.getDeviceID(), "Device ID should be 'device123'");
        assertEquals("John Doe", organizer.getName(), "Name should be 'John Doe'");
        assertEquals("organizer@example.com", organizer.getEmail(), "Email should be 'organizer@example.com'");
        assertEquals("1234567890", organizer.getPhoneNumber(), "Phone number should be '1234567890'");

        // changing values
        organizer.setDeviceID("newDeviceID");
        organizer.setName("Jane Smith");
        organizer.setEmail("newemail@example.com");
        organizer.setPhoneNumber("9876543210");

        // verifying updated values
        assertEquals("newDeviceID", organizer.getDeviceID(), "Device ID should be 'newDeviceID'");
        assertEquals("Jane Smith", organizer.getName(), "Name should be 'Jane Smith'");
        assertEquals("newemail@example.com", organizer.getEmail(), "Email should be 'newemail@example.com'");
        assertEquals("9876543210", organizer.getPhoneNumber(), "Phone number should be '9876543210'");
    }

    @Test
    void addEventTest() {
        // creating an organizer with an empty events list
        Organizer organizer = new Organizer(new ArrayList<>(), "1234567890", "organizer@example.com", "John Doe", "device123", false);

        organizer.addEvent("New Event");

        // verifying that the event was added to the list
        assertEquals(1, organizer.getAllEvents().size(), "Event list should have one event");
        assertTrue(organizer.getAllEvents().contains("New Event"), "Event list should contain 'New Event'");

        organizer.addEvent("Another Event");

        // verifying that the second event was added
        assertEquals(2, organizer.getAllEvents().size(), "Event list should have two events");
        assertTrue(organizer.getAllEvents().contains("Another Event"), "Event list should contain 'Another Event'");
    }

    @Test
    void organizerEdgeCasesTest() {
        // creating an organizer with empty events list and no name or email
        Organizer organizer = new Organizer(new ArrayList<>(), "", "", "", "", false);

        // verifying that empty strings are handled correctly
        assertEquals("", organizer.getDeviceID(), "Device ID should be an empty string");
        assertEquals("", organizer.getName(), "Name should be an empty string");
        assertEquals("", organizer.getEmail(), "Email should be an empty string");
        assertEquals("", organizer.getPhoneNumber(), "Phone number should be an empty string");

        organizer.addEvent("Empty Event Test");
        assertTrue(organizer.getAllEvents().contains("Empty Event Test"), "Event list should contain 'Empty Event Test'");
    }
}
