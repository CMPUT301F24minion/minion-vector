package com.example.minion_project;

import com.example.minion_project.user.User;

import java.util.Date;


/**
 * Notification class: represents a notification object with details such as type, message, date, sender, and receiver.
 */
public class Notification {
    private String notificationType;
    private String notificationMessage;
    private Date notificationDate; // Intended to use date to sort notifications in chronological order
    private User sender;
    private User receiver;

    /**
     * Default constructor for Notification class.
     */
    public Notification(){
        this.notificationType = "";
        this.notificationMessage = "";
        this.notificationDate = new Date(); // Set to current date when created
        this.sender = null;
        this.receiver = null;
    }

    /**
     * Constructor for Notification class.
     * @param notificationType type of notification
     * @param notificationMessage message of notification
     * @param sender sender of notification
     * @param receiver receiver of notification
     */
    public Notification(String notificationType, String notificationMessage, User sender, User receiver){
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        this.notificationDate = new Date(); // Set to current date when created
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Getter for notification type.
     * @return notification type
     */
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * Setter for notification type.
     * @param notificationType type of notification
     */
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * Getter for notification message.
     * @return notification message
     */
    public String getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * Setter for notification message.
     * @param notificationMessage message of notification
     */
    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    /**
     * Getter for notification date.
     * @return notification date
     */
    public Date getNotificationDate() {
        return notificationDate;
    }

    /**
     * Setting for notification date
     * @param notificationDate date of notification
     */
    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    /**
     * Getter for sender
     * @return sender
     */
    public User getSender() {
        return sender;
    }

    /**
     * Setter for sender
     * @param sender sender of notification
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Getter for receiver
     * @return receiver
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * Setter for receiver
     * @param receiver receiver of notification
     */
    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
