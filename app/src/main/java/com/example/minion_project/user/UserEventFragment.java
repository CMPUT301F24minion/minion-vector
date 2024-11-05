package com.example.minion_project.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventController;
import com.example.minion_project.organizer.OrganizerController;


public class UserEventFragment extends Fragment {
    private String eventID;
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

        if (getArguments() != null) {
            String scannedValue = getArguments().getString("scanned_value");
            eventID=scannedValue;
            event=eventController.getEvent(eventID);
            Log.d("UserScanFragment", "Scanned Value: " + event);
        }
        return view;
    }
}