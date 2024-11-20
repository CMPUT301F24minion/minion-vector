/**
 * Event Class: Represents an event
 * Includes attributes related to the event, such as its ID, date, name, description,
 * capacity, organizer, attendees, and more. Provides methods to get and set these attributes
 *
 * Outstanding Issues:
 *  - None
 */
package com.example.minion_project.events;

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
    private ArrayList<String> eventAttendees;
    private ArrayList<String> eventWaitlist;
    private ArrayList<String> eventInvited;
    private ArrayList<String> eventCanceled;
    private String eventDetails;
    private String eventTime;
    private String eventLocation;
    private String eventImage;
    private String eventQrCode;
    private String facilityName;

    /**
     * Default constructor for Event class: empty event
     */
    public Event() {
        this.eventID = "";
        this.eventDate = "";
        this.eventName = "";
        this.eventDescription = "";
        this.eventCapacity = "";
        this.eventOrganizer = "";
        this.eventAttendees = new ArrayList<>();
        this.eventWaitlist = new ArrayList<>();
        this.eventInvited = new ArrayList<>();
        this.eventCanceled = new ArrayList<>();
        this.eventLocation = "";
        this.eventImage = "";
        this.eventQrCode = "";
        this.facilityName = "";
    }

    /**
     * Constructor that initializes a event with ID and name
     * @param Id
     * @param name
     */
    public Event(String Id,String name){
        this.eventName=name;
        this.eventID=Id;
        this.eventDate = "";
        this.eventDescription = "";
        this.eventCapacity = "";
        this.eventOrganizer = "";
        this.eventAttendees = new ArrayList<>();
        this.eventWaitlist = new ArrayList<>();
        this.eventInvited = new ArrayList<>();
        this.eventCanceled = new ArrayList<>();
        this.eventLocation = "";
        this.eventImage = "";
        this.eventQrCode = "";
        this.facilityName = "";

    }
    public String getFacilityName() {return this.facilityName;}

    public void setFacilityName(String facilityName) {this.facilityName = facilityName;}

    /**
     * getEventID
     * @return eventID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * getEventQrCode
     * @return eventQrCode
     */
    public String getEventQrCode() { return eventQrCode;}

    /**
     * setEventQrCode
     * @param eventQrCode
     */
    public void setEventQrCode(String eventQrCode) {
        this.eventQrCode = eventQrCode;
    }

    /**
     * getEventImage
     * @return eventImage
     */
    public String getEventImage() {return this.eventImage;}

    /**
     * setEventImage
     * @param eventImage
     */
    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    /**
     * setEventID
     * @param eventID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * getEventDate
     * @return eventDate
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * setEventDate
     * @param eventDate
     */
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * getEventName
     * @return eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * setEventName
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * getEventDescription
     * @return eventDescription
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * setEventDescription
     * @param eventDescription
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * getEventCapacity
     * @return eventCapacity
     */
    public String getEventCapacity() {
        return eventCapacity;
    }

    /**
     * setEventCapacity
     * @param eventCapacity
     */
    public void setEventCapacity(String eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    /**
     * getEventOrganizer
     * @return eventOrganizer
     */
    public String getEventOrganizer() {
        return eventOrganizer;
    }

    /**
     * setEventOrganizer
     * @param eventOrganizer
     */
    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    /**
     * getEventAttendees
     * @return eventAttendees
     */
    public ArrayList<String> getEventAttendees() {
        return eventAttendees;
    }

    /**
     * setEventAttendees
     * @param eventAttendees
     */
    public void setEventAttendees(ArrayList<String> eventAttendees) {
        this.eventAttendees = eventAttendees;
    }

    /**
     * getEventCanceled
     * @return eventCanceled
     */
    public ArrayList<String> getEventWaitlist() {
        return eventWaitlist;
    }

    /**
     * setEventCanceled
     * @param eventWaitlist
     */
    public void setEventWaitlist(ArrayList<String> eventWaitlist) {
        this.eventWaitlist = eventWaitlist;
    }

    /**
     * getEventCanceled
     * @return eventCanceled
     */
    public String getEventDetails() {
        return eventDetails;
    }

    /**
     * setEventCanceled
     * @param eventDetails
     */
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    /**
     * getEventTime
     * @return eventTime
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * setEventTime
     * @param eventTime
     */
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * getEventLocation
     * @return eventLocation
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * setEventLocation
     * @param eventLocation
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /**
     * getEventInvited
     * @return eventInvited
     */
    public ArrayList<String> getEventInvited() {
        return eventInvited;
    }

    /**
     * setEventInvited:
     * @param invitedDeviceIds
     */
    public void setEventInvited(ArrayList<String> invitedDeviceIds) {
        this.eventInvited.addAll(invitedDeviceIds);
    }

}


