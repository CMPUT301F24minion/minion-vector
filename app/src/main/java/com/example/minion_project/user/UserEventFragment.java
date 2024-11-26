/**
 * Fragment class to handle user interactions with events
 */

package com.example.minion_project.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minion_project.Notification;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.example.minion_project.user.UserController;


public class UserEventFragment extends Fragment {
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventStatus;
    private Button eventJoinButton;
    private Button eventUnJoinButton;
    private Notification notification;
    private UserController userController;


    private EventController eventController; // to talk to db and model

    /**
     * Constructor for UserEventFragment.
     * @param eventController The event controller for fetching event data.
     * @param userController The user controller for managing user interactions with events.
     */
    public UserEventFragment(EventController eventController,UserController userController) {
        this.eventController=eventController;
        this.userController=userController;
        this.notification=new Notification();
    }

    private Event event;

    private String userStatusforEvent;

    /**
     * Inflates the layout and initializes the views for the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_event, container, false);
        eventNameTextView = view.findViewById(R.id.event_name);
        eventDescriptionTextView = view.findViewById(R.id.event_description);
        eventLocationTextView = view.findViewById(R.id.event_location);
        eventJoinButton = view.findViewById(R.id.event_join);
        eventStatus = view.findViewById(R.id.event_status);
        eventUnJoinButton=view.findViewById(R.id.event_unjoin);


        eventJoinButton.setOnClickListener(v->joinEvent());
        eventUnJoinButton.setOnClickListener(v->unJoinEvent());
        if (getArguments() != null) {
            String scannedValue = getArguments().getString("scanned_value");
            fetchEventData(scannedValue);

        }

        return view;
    }

    /**
     * Sets the visibility of the join and unjoin buttons based on the user's event status.
     */
private void ButtonVisibility(){
    if ("joined".equals(userStatusforEvent)) {
        eventUnJoinButton.setVisibility(View.VISIBLE);
        eventJoinButton.setVisibility(View.INVISIBLE);
    }else if("invited".equals(userStatusforEvent) ||"enrolled".equals(userStatusforEvent) || "declined".equals(userStatusforEvent)|| "rejected".equals(userStatusforEvent)){
        // do not allow to join see if status is invited,enrolled,declined or rejected
        eventUnJoinButton.setVisibility(View.INVISIBLE);
        eventJoinButton.setVisibility(View.INVISIBLE);
    }
    else{
        eventUnJoinButton.setVisibility(View.INVISIBLE);
        eventJoinButton.setVisibility(View.VISIBLE);
    }
}

    /**
     * Unjoins the user from the event.
     */
    private  void unJoinEvent(){
        if (event!=null){
            userController.unjoin_event(this.event);
            userStatusforEvent =userController.getUserEventStatus(event.getEventID());
            // change vis
            ButtonVisibility();
        }
        else {
            // If event is not yet loaded, show a message
            Toast.makeText(getContext(), "Event data is still loading. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Joins the user to the event.
     */
    private void joinEvent(){
        if (event != null) {
            // Make sure event is loaded before trying to join
            userController.join_event(this.event);
            //get new status
            userStatusforEvent =userController.getUserEventStatus(event.getEventID());
            // change vis
            ButtonVisibility();
            notification.addUserToNotificationDocument("waitlistlist_entrants", userController.user.getDeviceID());


        } else {
            // If event is not yet loaded, show a message
            Toast.makeText(getContext(), "Event data is still loading. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches event data based on the provided event ID.
     * @param eventID
     */
    private  void fetchEventData(String eventID){
        eventController.getEvent(eventID, new EventController.EventCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null) {
                    //todo: add more fields and display all info
                    eventNameTextView.setText(event.getEventName());
                    eventDescriptionTextView.setText(event.getEventDescription());
                    eventLocationTextView.setText(event.getEventLocation());
                    UserEventFragment.this.event = event;

                    //set Event status for the user
                    userStatusforEvent =userController.getUserEventStatus(event.getEventID());
                    if(userStatusforEvent !=null){
                        eventStatus.setText("Status:"+ userStatusforEvent);
                    }
                    ButtonVisibility();

                }
            }

            /**
             * Handles errors during event fetching.
             * @param errorMessage
             */
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }
}