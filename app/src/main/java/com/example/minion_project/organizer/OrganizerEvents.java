package com.example.minion_project.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.Event;
import com.example.minion_project.EventsAdapter;
import com.example.minion_project.R;

import java.util.ArrayList;

public class OrganizerEvents extends Fragment {
    private RecyclerView organizerEventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_organizer_events, container, false);

        // Initialize the RecyclerView
        organizerEventsRecyclerView = view.findViewById(R.id.organizerEventsRecyclerView);
        organizerEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create sample data for testing (replace with actual data source as needed)
        eventList = new ArrayList<>();
        eventList.add(new Event("1", "2024-11-01", "Tech Conference", "An engaging tech conference", "100",
                new ArrayList<>(), new ArrayList<>(), null));
        eventList.add(new Event("2", "2024-11-15", "Music Festival", "Enjoy live performances by top artists", "500",
                new ArrayList<>(), new ArrayList<>(), null));
        eventList.add(new Event("3", "2024-12-05", "Art Exhibition", "Showcasing local talent", "50",
                new ArrayList<>(), new ArrayList<>(), null));

        // Initialize the adapter with the sample event list
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        organizerEventsRecyclerView.setAdapter(eventsAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Additional setup can go here if needed
    }
}
