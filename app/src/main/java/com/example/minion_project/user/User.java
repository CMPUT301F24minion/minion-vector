package com.example.minion_project.user;

import static java.lang.Boolean.TRUE;

import android.app.Notification;

import com.example.minion_project.events.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Represents user objects, who are associated with their personal attributes and have permissions to attend events.
 */
public class User {
    private String deviceID;
    private String name;
    private String email;
    private String city;
    private  Boolean AllowNotication;
    private String phoneNumber;
    private ArrayList<Event> attendingEvents;
    private ArrayList<Event> waitlistedEvents;
    private ArrayList<Notification> notificationsArrayList;
    private HashMap<String,String> allEvents;
    private HashMap<String,ArrayList> notifcations;

    /**
     * Default constructor for User()'s with null fields for communication with the Firebase.
     */
    public User() {
        this.deviceID = "";
        this.name = "";
        this.email = "";
        this.city="";
        this.AllowNotication=TRUE;
        this.notifcations=new HashMap<>();
        this.phoneNumber = "";
        this.attendingEvents = new ArrayList<>();
        this.allEvents = new HashMap<>();
        this.waitlistedEvents = new ArrayList<>();
        this.notificationsArrayList = new ArrayList<>();
    }
    /**
     * Constructor that accepts only name and deviceID.
     * Other fields will be initialized to their default values (e.g., empty strings, null, or empty collections).
     * This constructor can be used when only basic user information is available.
     *
     * @param deviceID Unique identifier for the user.
     * @param name     Name of the user.
     */
    public User(String deviceID, String name) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = "";
        this.phoneNumber = "";
        this.city = "";
        this.AllowNotication = TRUE;
        this.notifcations = new HashMap<>();
        this.attendingEvents = new ArrayList<>();
        this.waitlistedEvents = new ArrayList<>();
        this.allEvents = new HashMap<>();
        this.notificationsArrayList = new ArrayList<>();
    }

    /**
     * Sets all fields for a new User().
     * all events is a a hashmap where we have {event_id: status} then we need to populate the waitlist and attending dep on status
     * @param deviceID indexes User
     * @param name
     * @param email
     * @param phoneNumber
     * @param allEvents
     * @param city
     * @param notification
     */
    public User(String deviceID, String name, String email, String phoneNumber, HashMap<String,String> allEvents , String city, HashMap<String,ArrayList> notification) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.attendingEvents = new ArrayList<>();
        this.waitlistedEvents = new ArrayList<>();
        this.notificationsArrayList = new ArrayList<>();
        this.allEvents=allEvents;
        this.city = city;
        this.notifcations = notification;
    }

    /**
     * @return allEvents
     */
    public HashMap<String, String> getAllEvents() {
        return allEvents;
    }

    /**
     * Another constructor for some case specific calls where only certain user information is accessible.
     * @param deviceID indexes user
     * @param name
     * @param email
     * @param phoneNumber
     * @param city
     */
    public User(String deviceID, String name, String email, String phoneNumber, String city) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
    }

    /**
     * @return this users unique device id
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     *
     * @param deviceID set User() deviceID to this requested deviceID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * @return this User()'s name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name set User()'s name to this requested name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return this User()'s email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email set User()'s email to this requested email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return User()'s phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<Event> getAttendingEvents() {
        return attendingEvents;
    }

    public void setAttendingEvents(ArrayList<Event> attendingEvents) {
        this.attendingEvents = attendingEvents;
    }

    public ArrayList<Event> getWaitlistedEvents() {
        return waitlistedEvents;
    }

    public void setWaitlistedEvents(ArrayList<Event> waitlistedEvents) {
        this.waitlistedEvents = waitlistedEvents;
    }

    public ArrayList<Notification> getNotificationsArrayList() {
        return notificationsArrayList;
    }

    public String getLocation() {
        return city;
    }

    public void setLocation(String city) {
        this.city = city;
    }

    public Boolean getAllowNotication() {
        return AllowNotication;
    }

    public void setAllowNotication(Boolean allowNotication) {
        AllowNotication = allowNotication;
    }

    public HashMap<String, ArrayList> getNotifcations() {
        return notifcations;
    }

    public void setNotifcations(HashMap<String, ArrayList> notifcations) {
        this.notifcations = notifcations;
    }

    /*
    add an event with status
     */
    public void addEvent(Event event,String status){
        this.allEvents.put(event.getEventID(),status);
    }

    public void setNotificationsArrayList(ArrayList<Notification> notificationsArrayList) {
        this.notificationsArrayList = notificationsArrayList;
    }

    public Boolean getAllowNotification() {
        return AllowNotication;
    }

}
