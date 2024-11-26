// AdminEvents.java

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

    /**
     * Default constructor
     */
    public AdminEvents() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment AdminEvents.
     */
    public static AdminEvents newInstance() {
        return new AdminEvents();
    }

    /**
     * Initialize Firestore and event list
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourFirestore = new FireStoreClass();
        eventsRef = ourFirestore.getEventsRef();
        eventList = new ArrayList<>();
    }

    /**
     * Inflate the layout and setup RecyclerView
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
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
     * Handle event deletion from the adapter
     *
     * @param event The event to be deleted
     */
    @Override
    public void onEventDelete(Event event) {
        // TODO: Retrieve adminDeviceID securely (e.g., from authentication)
        String adminDeviceID = getAdminDeviceID();

        // Create an instance of Admin
        Admin admin = new Admin(adminDeviceID, ourFirestore);

        // Call the removeEvent method
        admin.removeEvent(event, eventList, eventsAdapter);
    }

    /**
     * Placeholder method to retrieve adminDeviceID
     *
     * @return adminDeviceID as a String
     */
    private String getAdminDeviceID() {
        // Implement logic to retrieve the admin's deviceID
        // This could be from Firebase Authentication or another secure source
        // For example:
        // return FirebaseAuth.getInstance().getCurrentUser().getUid();
        return "admin_device_id"; // Replace with actual retrieval logic
    }
}
