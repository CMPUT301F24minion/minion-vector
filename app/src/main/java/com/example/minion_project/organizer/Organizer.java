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
    private String token;
    private ArrayList<String> allEvents;

    /**
     * Constructor for Organizer class
     * @param allEvents list of events the organizer is associated with
     * @param phoneNumber organizer's phone number
     * @param email organizer's email
     * @param name organizer's name
     * @param deviceID organizer's device ID
     */
    public Organizer(ArrayList<String> allEvents, String phoneNumber, String email, String token, String name, String deviceID) {
        this.allEvents = allEvents;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.name = name;
        this.deviceID = deviceID;
        this.token = token;
    }

    public String getToken(){ return this.token;}
    public void setToken(String token) { this.token = token;}
    /**
     * Getter for allEvents
     * @return allEvents
     */
    public ArrayList<String> getAllEvents() {
        return allEvents;
    }

    /**
     * Setter for allEvents
     * @param allEvents
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
     * @param phoneNumber
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
     * getter for email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter for email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * setter for name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for deviceID
     * @return deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * setter for deviceID
     * @param deviceID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
