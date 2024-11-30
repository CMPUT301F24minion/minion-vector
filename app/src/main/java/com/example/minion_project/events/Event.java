/**
 * Event Class: Represents an event
 * Includes attributes related to the event, such as its ID, date, name, description,
 * capacity, organizer, attendees, and more. Provides methods to get and set these attributes
 *
 * Outstanding Issues:
 *  - None
 */
package com.example.minion_project.events;

import static java.lang.Boolean.FALSE;

import java.util.ArrayList;

public class Event {
    private String eventID;
    private String eventDate;
    private String eventName;
    private String eventDescription;
    private Integer eventCapacity;

    private String eventOrganizer;

    private ArrayList<String> eventWaitlist; // users on the waitlist
    private ArrayList<String> eventEnrolled; // users who accepted invite
    private ArrayList<String> eventInvited;  // users chosen to attend
    private ArrayList<String> eventDeclined; // users who were offered but declined
    private ArrayList<String> eventRejected; // final users who weren't selected

    private String eventDetails;
    private String eventTime;
    private String eventLocation;
    private String eventImage;
    private String eventQrCode;
    private Integer DEFAULT_CAPACITY=10;
    private Boolean eventStart;

    public Boolean getEventStart() {
        return eventStart;
    }

    public void setEventStart(Boolean eventStart) {
        this.eventStart = eventStart;
    }

    /**
     * Default constructor for Event class: empty event
     */
    public Event() {
        this.eventID = "";
        this.eventDate = "";
        this.eventName = "";
        this.eventDescription = "";
        this.eventCapacity = DEFAULT_CAPACITY; //default capacity
        this.eventOrganizer = "";
        this.eventWaitlist = new ArrayList<>();
        this.eventEnrolled = new ArrayList<>();
        this.eventInvited = new ArrayList<>();
        this.eventDeclined = new ArrayList<>();
        this.eventRejected = new ArrayList<>();
        this.eventStart=FALSE;
        this.eventLocation = "";
        this.eventImage = "";
        this.eventQrCode = "";
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
        this.eventCapacity = DEFAULT_CAPACITY;
        this.eventOrganizer = "";
        this.eventWaitlist = new ArrayList<>();
        this.eventEnrolled = new ArrayList<>();
        this.eventInvited = new ArrayList<>();
        this.eventDeclined = new ArrayList<>();
        this.eventRejected = new ArrayList<>();
        this.eventLocation = "";
        this.eventImage = "";
        this.eventQrCode = "";
        this.eventStart= FALSE;


    }
    public ArrayList<String> getEventEnrolled() {
        return eventEnrolled;
    }

    public void setEventEnrolled(ArrayList<String> eventEnrolled) {
        this.eventEnrolled = eventEnrolled;
    }

    public ArrayList<String> getEventDeclined() {
        return eventDeclined;
    }

    public void setEventDeclined(ArrayList<String> eventDeclined) {
        this.eventDeclined = eventDeclined;
    }

    public ArrayList<String> getEventRejected() {
        return eventRejected;
    }

    public void setEventRejected(ArrayList<String> eventRejected) {
        this.eventRejected = eventRejected;
    }



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
    public Integer getEventCapacity() {
        return eventCapacity;
    }

    /**
     * setEventCapacity
     * @param eventCapacity
     */
    public void setEventCapacity(Integer eventCapacity) {
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


    public void setEventInvited(ArrayList<String> invitedUserIds) {
        this.eventInvited = invitedUserIds;
    }
}