package com.example.minion_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.example.minion_project.events.Event;
import com.example.minion_project.user.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Unit test for Event.runLotterySelection()
 */
public class EventRunLotterySelectionTest {
    private Event event;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    @Before
    public void setUp() {
        // Initialize an Event instance before each test
        event = new Event();
        event.setEventCapacity("3");  // Set the capacity to 3 for testing purposes

        // Create users to add to the event waitlist
        user1 = new User("device1", "User1", "user1@example.com", "1234567890", "City1");
        user2 = new User("device2", "User2", "user2@example.com", "1234567891", "City2");
        user3 = new User("device3", "User3", "user3@example.com", "1234567892", "City3");
        user4 = new User("device4", "User4", "user4@example.com", "1234567893", "City4");
        user5 = new User("device5", "User5", "user5@example.com", "1234567894", "City5");

        // Add users to the event waitlist
        event.getEventWaitlist().add(user1);
        event.getEventWaitlist().add(user2);
        event.getEventWaitlist().add(user3);
        event.getEventWaitlist().add(user4);
        event.getEventWaitlist().add(user5);
    }

    @Test
    public void testRunLotterySelectionReturnsCorrectNumber() {
        // Test that runLotterySelection returns the correct number of users
        ArrayList<User> selectedUsers = event.runLotterySelection();
        assertEquals(3, selectedUsers.size());  // We expect 3 users, as per eventCapacity
    }

    @Test
    public void testRunLotterySelectionDoesNotExceedWaitlistSize() {
        // Set capacity higher than the waitlist size
        event.setEventCapacity("10");
        ArrayList<User> selectedUsers = event.runLotterySelection();
        assertEquals(event.getEventWaitlist().size(), selectedUsers.size());  // Should select all waitlist users
    }

    @Test
    public void testRunLotterySelectionUpdatesInvitedList() {
        ArrayList<User> selectedUsers = event.runLotterySelection();
        assertEquals(selectedUsers, event.getEventInvited());
    }

    @Test
    public void testRunLotterySelectionRandomSelection() {
        // Run runLotterySelection multiple times to check if we get varied results
        ArrayList<User> firstRun = event.runLotterySelection();
        ArrayList<User> secondRun = event.runLotterySelection();
        ArrayList<User> thirdRun = event.runLotterySelection();

        // Verify that selections may differ across runs due to randomness
        boolean differentResults = !firstRun.equals(secondRun) || !secondRun.equals(thirdRun);
        assertTrue("Selections should differ across runs due to random shuffling", differentResults);
    }
}
