package com.example.minion_project.user;

public class UserEvent {
    private String eventID;
    private String eventDate;
    private String eventName;
    private String eventDescription;
    private String status;

    // we dont want users to see everything about events
    // only what is relavant to them
    public UserEvent(String eventID, String status, String eventDescription, String eventName, String eventDate) {
        this.eventID = eventID;
        this.status = status;
        this.eventDescription = eventDescription;
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status=status;
    }
}
