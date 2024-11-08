package com.example.minion_project.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * One of two functionalities for organizer.
 * Organizers are able to schedule events, and are able to view their scheduled events here.
 * Handles displaying a recyclyer view for an organizers events (held within and associated with a unique organizer
 * within the database)
 */
public class OrganizerEvents extends Fragment {
    private RecyclerView organizerEventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;
    private FireStoreClass ourFirestore;
    private OrganizerController organizerController;

    public OrganizerEvents(OrganizerController organizerController) {
        this.organizerController = organizerController;
        this.ourFirestore = new FireStoreClass();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_events, container, false);

        organizerEventsRecyclerView = view.findViewById(R.id.organizerEventsRecyclerView);
        organizerEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(getContext(), eventList); // Using two-parameter constructor
        organizerEventsRecyclerView.setAdapter(eventsAdapter);

        fetchEvents();

        return view;
    }

    /**
     * Fetch all organizer events
     */
    private void fetchEvents() {
        ArrayList<String> eventIds = organizerController.getOrganizer().getAllEvents();
        FirebaseFirestore db = ourFirestore.getFirestore();

        for (String eventId : eventIds) {
            db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setEventID(document.getId());
                            eventList.add(event);
                            eventsAdapter.notifyItemInserted(eventList.size() - 1);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
