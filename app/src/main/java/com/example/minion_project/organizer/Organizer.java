package com.example.minion_project.organizer;

import android.app.Notification;

import com.example.minion_project.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class Organizer {
    private String deviceID;
    private String name;
    private String email;
    private String phoneNumber;
    private ArrayList<String> allEvents;

    public Organizer(ArrayList<String> allEvents, String phoneNumber, String email, String name, String deviceID) {
        this.allEvents = allEvents;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.name = name;
        this.deviceID = deviceID;
    }

    public ArrayList<String> getAllEvents() {
        return allEvents;
    }

    public void setAllEvents(ArrayList<String> allEvents) {
        this.allEvents = allEvents;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // adds 1 event to organizer events
    public void addEvent(String event) {
        this.allEvents.add(event);

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
