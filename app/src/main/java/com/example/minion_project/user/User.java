package com.example.minion_project.user;

import static java.lang.Boolean.TRUE;

import android.app.Notification;
import android.location.Location;

import com.example.minion_project.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String deviceID;
    private String name;
    private String email;
    private String location;
    private  Boolean AllowNotication;
    private String phoneNumber;
    private ArrayList<Event> attendingEvents;
    private ArrayList<Event> waitlistedEvents;
    private ArrayList<Notification> notificationsArrayList;
    private HashMap<String,String> allEvents;
    private HashMap<String,ArrayList> notifcations;

    // Default Constructor
    public User() {
        this.deviceID = "";
        this.name = "";
        this.email = "";
        this.location="";
        this.AllowNotication=TRUE;
        this.notifcations=new HashMap<>();
        this.phoneNumber = "";
        this.attendingEvents = new ArrayList<>();
        this.allEvents = new HashMap<>();
        this.waitlistedEvents = new ArrayList<>();
        this.notificationsArrayList = new ArrayList<>();
    }

    // Constructor with parameters
    // all events is a a hashmap where we have {event_id: status} then we need to populate the waitlist and attending dep on status
    public User(String deviceID, String name, String email, String phoneNumber, HashMap<String,String> allEvents , String location, HashMap<String,ArrayList> notification) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.attendingEvents = new ArrayList<>();
        this.waitlistedEvents = new ArrayList<>();
        this.notificationsArrayList = new ArrayList<>();
        this.allEvents=allEvents;
        this.location = location;
        this.notifcations = notification;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        notifcations = notifcations;
    }

    public void setNotificationsArrayList(ArrayList<Notification> notificationsArrayList) {
        this.notificationsArrayList = notificationsArrayList;
    }
}
