package com.example.minion_project.Lottery;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.Random;

public class Lottery {
    private Event event;
    private EventController eventController;
    private FireStoreClass fireStore;
    private CollectionReference userRef;

    // the lottery has access to the event
    public Lottery(Event event){
        this.event=event;
        this.eventController=new EventController();
        this.fireStore=new FireStoreClass();
        this.userRef=fireStore.getUsersRef();

    }

    public void poolApplicants(Integer numberToPool){
        // first we get the list of all people in waitlist
        ArrayList<String> eventsWaitlist=event.getEventWaitlist();
        // Log.d("poolApplicants", String.valueOf(eventsWaitlist));

        // next we sample from this pool eventCapacity random users
        // and save into an array called processPooled
        int numberOfApplicantsToPool = Math.min(eventsWaitlist.size(), event.getEventCapacity());
        numberOfApplicantsToPool=Math.min(numberToPool,numberOfApplicantsToPool);
        ArrayList<String> processPooled = new ArrayList<>();
        Random random = new Random();

        for (int i=0;i<numberOfApplicantsToPool;i++){
            int randomIdx=random.nextInt(eventsWaitlist.size()); //get random index

            String selectedApplicant =eventsWaitlist.get(randomIdx);
            processPooled.add(selectedApplicant);
            eventsWaitlist.remove(randomIdx); //remember this is a local in class func so we havent committed anything to db

        }

        // now that we have the pooled applicants id we call for each of them the PoolUser func
        for (int i=0;i<numberOfApplicantsToPool;i++) {
            poolUser(processPooled.get(i));
        }

    }
    public void poolUser(String userId) {
        //remove from waitlist
        eventController.removeUserFromWaitlist(event, userId);

        // set user eventId to invited status
        setUserEventID(userId,"invited");

        // call notification
        // TODO

        //add to the eventsInvited arr
        eventController.addToEventInvited(event,userId);
    }

    private void setUserEventID(String userId, String status){
        userRef.document(userId)
                .update("Events."+event.getEventID(), status);
    }

}
