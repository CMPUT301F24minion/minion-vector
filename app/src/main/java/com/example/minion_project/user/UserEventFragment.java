package com.example.minion_project.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;


public class UserEventFragment extends Fragment {
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventStatus;
    private Button eventJoinButton;
    private Button eventUnJoinButton;

    private UserController userController;


    private EventController eventController; // to talk to db and model
    public UserEventFragment(EventController eventController,UserController userController) {
        this.eventController=eventController;
        this.userController=userController;
    }

    private Event event;

    private String userStatusforEvent;
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
private void ButtonVisibility(){
    if ("joined".equals(userStatusforEvent)) {
        eventUnJoinButton.setVisibility(View.VISIBLE);
        eventJoinButton.setVisibility(View.INVISIBLE);
    }else{
        eventUnJoinButton.setVisibility(View.INVISIBLE);
        eventJoinButton.setVisibility(View.VISIBLE);
    }
}
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
    private void joinEvent(){
        if (event != null) {
            // Make sure event is loaded before trying to join
            userController.join_event(this.event);
            //get new status
            userStatusforEvent =userController.getUserEventStatus(event.getEventID());
            // change vis
            ButtonVisibility();

        } else {
            // If event is not yet loaded, show a message
            Toast.makeText(getContext(), "Event data is still loading. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
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

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }
}