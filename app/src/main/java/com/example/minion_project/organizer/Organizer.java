/**
 * Class that represents an organizer
 * The Organizer class holds information about an organizer, including their ID, name, email, phone number,
 * and the events they are associated with.
 */

package com.example.minion_project.organizer;

import java.util.ArrayList;

public class Organizer {
    private String deviceID;
    private String name;
    private String email;
    private String phoneNumber;
    private ArrayList<String> allEvents;
    private boolean facility;

    public Organizer() {
        this.allEvents = null;
        this.phoneNumber = "";
        this.email = "";
        this.name = "";
        this.deviceID = "";
        this.facility = false;
    }

    /**
     * Constructor for Organizer class
     * @param allEvents list of events the organizer is associated with
     * @param phoneNumber organizer's phone number
     * @param email organizer's email
     * @param name organizer's name
     * @param deviceID organizer's device ID
     * @param facility indicates if the organizer has a facility
     */
    public Organizer(ArrayList<String> allEvents, String phoneNumber, String email, String name, String deviceID, boolean facility) {
        this.allEvents = allEvents;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.name = name;
        this.deviceID = deviceID;
        this.facility = facility;
    }

    /**
     * Getter for facility
     * @return facility
     */
    public boolean getFacility() {
        return facility;
    }

    /**
     * Setter for facility
     * @param facility boolean indicating if the organizer has a facility
     */
    public void setFacility(boolean facility) {
        this.facility = facility;
    }

    /**
     * Getter for allEvents
     * @return allEvents
     */
    public ArrayList<String> getAllEvents() {
        return allEvents;
    }

    /**
     * Setter for allEvents
     * @param allEvents list of events the organizer is associated with
     */
    public void setAllEvents(ArrayList<String> allEvents) {
        this.allEvents = allEvents;
    }

    /**
     * Getter for phoneNumber
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter for phoneNumber
     * @param phoneNumber organizer's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Adds an event to the list of events the organizer is associated with
     * @param event event to be added
     */
    public void addEvent(String event) {
        this.allEvents.add(event);
    }

    /**
     * Getter for email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email
     * @param email organizer's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name organizer's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for deviceID
     * @return deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Setter for deviceID
     * @param deviceID organizer's device ID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
