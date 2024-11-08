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


/**
 * Fragment representing the User's attending events.
 */
public class UserAttendingFragment extends Fragment {
    private RecyclerView attendeeRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;

    /**
     * Called to have the fragment instantiate its user interface view.
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

    /**
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Additional setup can go here if needed
    }
}
