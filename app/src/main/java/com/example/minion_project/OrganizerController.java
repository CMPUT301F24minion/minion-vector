package com.example.minion_project;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.minion_project.organizer.Organizer;
import com.example.minion_project.user.User;
import com.example.minion_project.user.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;



public class OrganizerController {
    public Organizer organizer;
    public FireStore Our_Firestore=new FireStore();
    private CollectionReference organizersRef;

    public OrganizerController(Organizer organizer) {
        this.organizer = organizer;
        this.organizersRef=Our_Firestore.getOrganizersRef();
    }


    public void addEvent(String eventId) {
        organizersRef.document(organizer.getDeviceID()).update("Events",
                        FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    // Only add to the organizer if the Firestore update was successful
                    this.organizer.addEvent(eventId);
                    Log.e("OrganizerController", "SUCCESS to add event: " + eventId );

                })
                .addOnFailureListener(e -> {
                    // Log failure
                    Log.e("OrganizerController", "Failed to add event: " + eventId + ", Error: " + e.getMessage());
                });;

    }



}
