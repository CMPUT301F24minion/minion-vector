package com.example.minion_project.user;

/**
 * Class representing a user's event.
 */
public class UserEvent {
    private String eventID;
    private String eventDate;
    private String eventName;
    private String eventDescription;
    private String status;

    /**
     * Constructor for UserEvent.
     * @param eventID The ID of the event.
     * @param status The status of the event.
     * @param eventDescription The description of the event.
     * @param eventName The name of the event.
     * @param eventDate The date of the event.
     */
    public UserEvent(String eventID, String status, String eventDescription, String eventName, String eventDate) {
        this.eventID = eventID;
        this.status = status;
        this.eventDescription = eventDescription;
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    /**
     * Getter for the event ID.
     * @return The event ID.
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Getter for the event date.
     * @return The event date.
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * Getter for the event name.
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Getter for the event description.
     * @return The event description.
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Getter for the event status.
     * @return The event status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter for the event ID.
     * @param status The new event ID.
     */
    public void setStatus(String status) {
        this.status=status;
    }
}
