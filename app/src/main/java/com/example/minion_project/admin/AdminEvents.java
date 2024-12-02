// AdminEvents.java

package com.example.minion_project.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminEvents extends Fragment implements EventsAdapter.OnEventDeleteListener, EventsAdapter.OnItemClickListener {

    private RecyclerView adminEventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventList;
    private FireStoreClass ourFirestore;
    private CollectionReference eventsRef;

    private static final String TAG = "AdminEvents";

    public AdminEvents() {
    }

    public static AdminEvents newInstance() {
        return new AdminEvents();
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
        eventsAdapter.setOnItemClickListener(this); // Set the item click listener
        adminEventsRecyclerView.setAdapter(eventsAdapter);

        fetchEvents();

        return view;
    }

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

    @Override
    public void onEventDelete(Event event) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with deleting the event
                    String adminDeviceID = getAdminDeviceID();
                    // Create an instance of Admin
                    Admin admin = new Admin(adminDeviceID, ourFirestore);
                    // Call the removeEvent method
                    admin.removeEvent(event, eventList, eventsAdapter);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemClick(Event event) {
        boolean isQrCodeEnabled = event.isQrCodeEnabled();
        String qrCodeOption = isQrCodeEnabled ? "Disable QR Code" : "Enable QR Code";

        String[] options = {qrCodeOption, "Delete Event"};

        new AlertDialog.Builder(getContext())
                .setTitle("Event Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Toggle QR Code enabled/disabled
                        toggleQrCode(event);
                    } else if (which == 1) {
                        // Delete Event
                        onEventDelete(event);
                    }
                })
                .show();
    }

    private void toggleQrCode(Event event) {
        boolean newQrCodeState = !event.isQrCodeEnabled();
        eventsRef.document(event.getEventID())
                .update("qrCodeEnabled", newQrCodeState)
                .addOnSuccessListener(aVoid -> {
                    event.setQrCodeEnabled(newQrCodeState);
                    eventsAdapter.notifyDataSetChanged();
                    String message = newQrCodeState ? "QR Code enabled" : "QR Code disabled";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating QR code state: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to update QR code state.", Toast.LENGTH_SHORT).show();
                });
    }

    private String getAdminDeviceID() {
        // retrieve the admin's deviceID
        return "admin_device_id";
    }
}
