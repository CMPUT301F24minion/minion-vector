package com.example.minion_project;

import java.util.ArrayList;
import java.util.UUID;

public class Event {
    private UUID eventID;
    private String eventDate;
    private String eventName;
    private String eventDescription;
    private int eventCapacity;
    private User eventOrganizer;
    private ArrayList<User> eventAttendees;
    private ArrayList<User> eventWaitlist;

    // Default constructor
    public Event() {
        this.eventID = UUID.randomUUID();
        this.eventDate = "";
        this.eventName = "";
        this.eventDescription = "";
        this.eventCapacity = 0;
        this.eventOrganizer = null;
        this.eventAttendees = new ArrayList<>();
        this.eventWaitlist = new ArrayList<>();
    }

    // Parameterized constructor
    public Event(String eventDate, String eventName, String eventDescription, int eventCapacity,
                 ArrayList<User> waitlist, ArrayList<User> participants, User eventOrganizer) {
        this.eventID = UUID.randomUUID();
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventCapacity = eventCapacity;
        this.eventOrganizer = eventOrganizer;

        // If participants and waitlist are null, initialize them to empty lists
        this.eventAttendees = (participants != null) ? participants : new ArrayList<>();
        this.eventWaitlist = (waitlist != null) ? waitlist : new ArrayList<>();
    }

    // Getters and setters for each field
    public UUID getEventID() {
        return eventID;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public int getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }
}
