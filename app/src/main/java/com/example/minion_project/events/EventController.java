package com.example.minion_project.events;

import com.example.minion_project.FireStoreClass;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventController {
    public FireStoreClass Our_Firestore = new FireStoreClass();
    private CollectionReference eventsRef;
    private Event event;
    public EventController() {
        this.eventsRef=Our_Firestore.getEventsRef();
    }

    // get an event
    public Event getEvent(String eventId){
       eventsRef.document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    this.event = document.toObject(Event.class);

                }
            }
        });
       return this.event;
    }
}
