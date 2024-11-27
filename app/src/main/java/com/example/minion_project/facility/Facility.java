package com.example.minion_project.facility;

public class Facility {
    private String facilityID; // This holds the name
    private String facilityImage;

    public Facility() {
        // Empty constructor required for Firestore
    }

    // Getters and Setters
    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public String getFacilityImage() {
        return facilityImage;
    }

    public void setFacilityImage(String facilityImage) {
        this.facilityImage = facilityImage;
    }
}
