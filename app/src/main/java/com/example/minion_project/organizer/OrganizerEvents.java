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
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.OrganizerController;
import com.example.minion_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        organizerEventsRecyclerView.setAdapter(eventsAdapter);

        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        ArrayList<String> eventIds = organizerController.getOrganizer().getAllEvents();
        FirebaseFirestore db = ourFirestore.getFirestore();

        for (String eventId : eventIds) {
            db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        eventList.add(event);
                        eventsAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
