package com.example.minion_project.facility;

/**
 * Facility: Represents a facility in the application.
 */
public class Facility {
    private String documentID;     // Firestore document ID
    private String facilityID;     // Facility name
    private String facilityImage;

    /**
     * Constructor for Facility
     */
    public Facility() {
        // Empty constructor required for Firestore
    }

    // Getters and Setters

    /**
     * getDocumentID
     * @return documentID
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     * setDocumentID
     * @param documentID The document ID to set
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /**
     * getFacilityID
     * @return facilityID
     */
    public String getFacilityID() {
        return facilityID;
    }

    /**
     * setFacilityID
     * @param facilityID The facility ID to set
     */
    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    /**
     * getFacilityImage
     * @return facilityImage
     */
    public String getFacilityImage() {
        return facilityImage;
    }

    /**
     * setFacilityImage
     * @param facilityImage The facility image to set
     */
    public void setFacilityImage(String facilityImage) {
        this.facilityImage = facilityImage;
    }

    /**
     *
     * @return String representation of the Facility object
     */
    @Override
    public String toString() {
        return "Facility{" +
                "documentID='" + documentID + '\'' +
                ", facilityID='" + facilityID + '\'' +
                ", facilityImage='" + facilityImage + '\'' +
                '}';
    }
}
