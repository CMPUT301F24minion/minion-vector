package com.example.minion_project;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.UUID;

// TODO: Waiting on user class to be implemented
//       Somethings are commented out as we are waiting for the user class to be implemented
//       - Drae

public class Event {
    private UUID eventID;
    private String eventDate;
    private String eventName;
    private String eventDescription;
    private int eventCapacity;
    // private User eventOrganizer;
    // private ArrayList<User> eventAttendees;
    // private ArrayList<User> eventWaitlist;

    // Default constructor
    public Event() {
        this.eventID = UUID.randomUUID();
        this.eventDate = "";
        this.eventName = "";
        this.eventDescription = "";
        this.eventCapacity = 0;
        // this.eventOrganizer = null;
        // this.eventAttendees = new ArrayList<>();
        // this.eventWaitlist = new ArrayList<>();
    }

//    public Event(String eventDate, String eventName, String eventDescription, int eventCapacity,
//                 ArrayList<User> waitList, ArrayList<User> participants, User eventOrganizer) {
//        this.eventID = UUID.randomUUID(); // Always generate a unique event ID
//        this.eventDate = eventDate;
//        this.eventDescription = eventDescription;
//        this.waitList = waitList != null ? waitList : new ArrayList<>();
//        this.eventAttendees = participants != null ? participants : new ArrayList<>();
//        this.eventCapacity = eventCapacity;;
//        this.eventOrganizer = eventOrganizer;
//    }

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
