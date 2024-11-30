package com.example.minion_project.facility;

public class Facility {
    private String documentID;     // Firestore document ID
    private String facilityID;     // Facility name
    private String facilityImage;

    public Facility() {
        // Empty constructor required for Firestore
    }

    // Getters and Setters

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

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
