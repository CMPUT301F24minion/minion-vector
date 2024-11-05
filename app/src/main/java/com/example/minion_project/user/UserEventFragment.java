package com.example.minion_project.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.example.minion_project.organizer.OrganizerController;


public class UserEventFragment extends Fragment {
    private String eventID;
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private Button eventJoinButton;


    private EventController eventController; // to talk to db and model
    public UserEventFragment(EventController eventController) {
        this.eventController=eventController;
    }

    private Event event;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_event, container, false);
        eventNameTextView = view.findViewById(R.id.event_name);
        eventDescriptionTextView = view.findViewById(R.id.event_description);
        eventLocationTextView = view.findViewById(R.id.event_location);
        eventJoinButton = view.findViewById(R.id.event_join);


        if (getArguments() != null) {
            String scannedValue = getArguments().getString("scanned_value");
            eventID=scannedValue;
            fetchEventData(eventID);


            Log.d("UserScanFragment", "Scanned Value: " + event);
        }
        return view;
    }
    private  void fetchEventData(String eventID){
        eventController.getEvent(eventID, new EventController.EventCallback() {
            @Override
            public void onEventFetched(Event event) {
                if (event != null) {
                    eventNameTextView.setText(event.getEventName());
                    eventDescriptionTextView.setText(event.getEventDescription());
                    eventLocationTextView.setText(event.getEventLocation());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
}