package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class AdminEvents extends Fragment implements EventsAdapter.OnEventDeleteListener {

    private RecyclerView adminEventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;
    private FireStoreClass ourFirestore;
    private CollectionReference eventsRef;

    private static final String TAG = "AdminEvents";

    public AdminEvents() {
    }
    public static AdminEvents newInstance(String param1, String param2) {
        AdminEvents fragment = new AdminEvents();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourFirestore = new FireStoreClass();
        eventsRef = ourFirestore.getEventsRef();
        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_events, container, false);

        adminEventsRecyclerView = view.findViewById(R.id.adminEventsRecyclerView);
        adminEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsAdapter = new EventsAdapter(getContext(), eventList, this);
        adminEventsRecyclerView.setAdapter(eventsAdapter);

        fetchEvents();

        return view;
    }

    /**
     * Fetch all events from Firestore and populate the RecyclerView
     */
    private void fetchEvents() {
        eventsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setEventID(document.getId());
                            eventList.add(event);
                        }
                    }

                    if (eventList.isEmpty()) {
                        adminEventsRecyclerView.setVisibility(View.GONE);
                        getView().findViewById(R.id.noEventsTextView).setVisibility(View.VISIBLE);
                    } else {
                        adminEventsRecyclerView.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.noEventsTextView).setVisibility(View.GONE);
                    }

                    eventsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching events: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch events.", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Handle event deletion
     *
     * @param event The event to be deleted
     */
    @Override
    public void onEventDelete(Event event) {

        eventsRef.document(event.getEventID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event deleted successfully.", Toast.LENGTH_SHORT).show();

                    eventList.remove(event);
                    eventsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to delete event.", Toast.LENGTH_SHORT).show();
                });
    }
}
