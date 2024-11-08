package com.example.minion_project;

import static org.junit.jupiter.api.Assertions.*;

import com.example.minion_project.user.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class NotificationTest {

    @Test
    void notificationConstructorTest() {
        Notification notification = new Notification("Info", "Test message", new User("user1", "Alice"), new User("user2", "Bob"));

        assertEquals("Info", notification.getNotificationType(), "Notification type should be 'Info'");
        assertEquals("Test message", notification.getNotificationMessage(), "Notification message should be 'Test message'");
        assertNotNull(notification.getNotificationDate(), "Notification date should not be null");
        assertEquals("Alice", notification.getSender().getName(), "Sender should be 'Alice'");
        assertEquals("Bob", notification.getReceiver().getName(), "Receiver should be 'Bob'");
    }

    @Test
    void notificationGetterSetterTest() {
        Notification notification = new Notification();

        notification.setNotificationType("Alert");
        notification.setNotificationMessage("Updated message");
        Date date = new Date();
        notification.setNotificationDate(date);
        User sender = new User("user3", "Charlie");
        User receiver = new User("user4", "Diana");
        notification.setSender(sender);
        notification.setReceiver(receiver);

        assertEquals("Alert", notification.getNotificationType(), "Notification type should be 'Alert'");
        assertEquals("Updated message", notification.getNotificationMessage(), "Notification message should be 'Updated message'");
        assertEquals(date, notification.getNotificationDate(), "Notification date should match the set date");
        assertEquals(sender, notification.getSender(), "Sender should be 'Charlie'");
        assertEquals(receiver, notification.getReceiver(), "Receiver should be 'Diana'");
    }

    @Test
    void notificationDateUpdateTest() {
        Notification notification = new Notification("Reminder", "Initial message", new User("user5", "Eve"), new User("user6", "Frank"));

        Date initialDate = notification.getNotificationDate();
        assertNotNull(initialDate, "Initial notification date should not be null");

        Date newDate = new Date(initialDate.getTime() + 10000); // 10 seconds later
        notification.setNotificationDate(newDate);
        assertEquals(newDate, notification.getNotificationDate(), "Notification date should be updated to the new date");
    }

    @Test
    void notificationTypeEdgeCaseTest() {
        Notification notification = new Notification();
        notification.setNotificationType(null);
        assertNull(notification.getNotificationType(), "Notification type should be null when set to null");

        notification.setNotificationType("");
        assertEquals("", notification.getNotificationType(), "Notification type should be empty when set to an empty string");

        notification.setNotificationType("Long notification type that exceeds typical length");
        assertEquals("Long notification type that exceeds typical length", notification.getNotificationType(), "Notification type should match the long string set");
    }

    @Test
    void notificationSenderReceiverTest() {
        Notification notification = new Notification();

        User sender = new User("user7", "George");
        User receiver = new User("user8", "Hannah");
        notification.setSender(sender);
        notification.setReceiver(receiver);

        assertEquals(sender, notification.getSender(), "Sender should be 'George'");
        assertEquals(receiver, notification.getReceiver(), "Receiver should be 'Hannah'");
    }
}
