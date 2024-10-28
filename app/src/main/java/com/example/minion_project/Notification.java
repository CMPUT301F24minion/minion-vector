package com.example.minion_project;

import com.example.minion_project.user.User;

import java.util.Date;

public class Notification {
    private String notificationType;
    private String notificationMessage;
    private Date notificationDate; // Intended to use date to sort notifications in chronological order
    private User sender;
    private User receiver;

    public Notification(){
        this.notificationType = "";
        this.notificationMessage = "";
        this.notificationDate = new Date(); // Set to current date when created
        this.sender = null;
        this.receiver = null;
    }

    public Notification(String notificationType, String notificationMessage, User sender, User receiver){
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        this.notificationDate = new Date(); // Set to current date when created
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
