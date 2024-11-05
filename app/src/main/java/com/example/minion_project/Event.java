package com.example.minion_project;

import com.example.minion_project.user.User;

import java.util.ArrayList;
import java.util.Collections;

public class Event {
    private String eventID;
    private String eventDate;
    private String eventName;
    private String eventDescription;
    private String eventCapacity;
    private String eventOrganizer;
    private ArrayList<User> eventAttendees;
    private ArrayList<User> eventWaitlist;
    private ArrayList<User> eventInvited;
    private ArrayList<User> eventCanceled;
    private String eventDetails;
    private String eventTime;
    private String eventLocation;
    private String eventImage;
    private String eventQrCode;

    public Event() {
        this.eventID = "";
        this.eventDate = "";
        this.eventName = "";
        this.eventDescription = "";
        this.eventCapacity = "";
        this.eventOrganizer = "";
        this.eventAttendees = new ArrayList<>(); // Users picked by lottery and accepted the invitation
        this.eventWaitlist = new ArrayList<>(); // All users go here before lottery system
        this.eventInvited = new ArrayList<>();  // Users who have been selected by lottery system waiting for them to accept
        this.eventCanceled = new ArrayList<>(); // Users who did not get accepted into the event
        this.eventLocation = "";
        this.eventImage = "";
        this.eventQrCode = "";
    }

    public String getEventID() {
        return eventID;
    }
    public String getEventQrCode() { return eventQrCode;}
    public void setEventQrCode(String eventQrCode) {
        this.eventQrCode = eventQrCode;
    }
    public String getEventImage() {return this.eventImage;}

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }
    public void setEventID(String eventID) {
        this.eventID = eventID;
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

    public String getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(String eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public ArrayList<User> getEventAttendees() {
        return eventAttendees;
    }

    public void setEventAttendees(ArrayList<User> eventAttendees) {
        this.eventAttendees = eventAttendees;
    }

    public ArrayList<User> getEventWaitlist() {
        return eventWaitlist;
    }

    public void setEventWaitlist(ArrayList<User> eventWaitlist) {
        this.eventWaitlist = eventWaitlist;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /**
     * runLotterySelection uses a lottery system to select users from the waitlist and
     * add them to the eventInvited list
     * @return ArrayList<User> selectedUsers
     */
    public ArrayList<User> runLotterySelection() {
        // Create a copy of the waitlist to avoid modifying the original list
        ArrayList<User> copyOfEventWaitlist = new ArrayList<>(this.eventWaitlist);

        // Shuffle the copy to randomize the selection
        Collections.shuffle(copyOfEventWaitlist);

        // Determine the number of users to select, ensuring it does not exceed the list size
        int numToSelect = Math.min(Integer.parseInt(this.eventCapacity), copyOfEventWaitlist.size());

        // Add each selected user to the eventInvited list
        ArrayList<User> selectedUsers = new ArrayList<>(copyOfEventWaitlist.subList(0, numToSelect));
        this.eventInvited.addAll(selectedUsers);

        return selectedUsers;
    }

    // Merge
    public ArrayList<User> getEventInvited() {
        return eventInvited;
    }
}
