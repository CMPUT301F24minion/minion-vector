package com.example.minion_project;

import android.app.Notification;

import java.util.ArrayList;

public class User {
    private String deviceID;
    private String name;
    private String email;
    private String phoneNumber;
    private ArrayList<Event> attendingEvents;
    private ArrayList<Event> waitlistedEvents;
    private ArrayList<Notification> notificationsArrayList;

    // Default Constructor
    public User() {
        this.deviceID = "";
        this.name = "";
        this.email = "";
        this.phoneNumber = "";
        this.attendingEvents = new ArrayList<>();
        this.waitlistedEvents = new ArrayList<>();
        this.notificationsArrayList = new ArrayList<>();
    }

    // Constructor with parameters
    public User(String deviceID, String name, String email, String phoneNumber) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.attendingEvents = new ArrayList<>();
        this.waitlistedEvents = new ArrayList<>();
        this.notificationsArrayList = new ArrayList<>();
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

    public void setNotificationsArrayList(ArrayList<Notification> notificationsArrayList) {
        this.notificationsArrayList = notificationsArrayList;
    }
}
