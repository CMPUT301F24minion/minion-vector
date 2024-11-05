package com.example.minion_project.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.example.minion_project.R;

import java.util.ArrayList;

public class UserAttendingFragment extends Fragment {
    private RecyclerView attendeeRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_user_attending, container, false);

        /*
        // Initialize the RecyclerView
        attendeeRecyclerView = view.findViewById(R.id.attendeeEventsRecyclerView);
        attendeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with the sample event list
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        attendeeRecyclerView.setAdapter(eventsAdapter);
        */
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Additional setup can go here if needed
    }
}
