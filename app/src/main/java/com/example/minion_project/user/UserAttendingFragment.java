package com.example.minion_project.user;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.user.UserAttendingEventsAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAttendingFragment extends Fragment {

    private RecyclerView userEventStatusRecyclerView;
    private UserAttendingEventsAdapter eventsAdapter;
    private List<Event> enrolledEvents;
    private FirebaseFirestore firestore;
    private String currentUserId; // Replace this with your method of fetching the logged-in user's ID

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_event_status, container, false);

        userEventStatusRecyclerView = view.findViewById(R.id.userEventStatusRecyclerView);
        userEventStatusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        enrolledEvents = new ArrayList<>();
        eventsAdapter = new UserAttendingEventsAdapter(getContext(), enrolledEvents);
        userEventStatusRecyclerView.setAdapter(eventsAdapter);

        firestore = FirebaseFirestore.getInstance();
        currentUserId = getCurrentUserId(); // Replace this with the actual implementation to fetch user ID

        fetchEnrolledEvents();

        return view;
    }

    private String getCurrentUserId() {
        // Retrieve the unique device ID using ANDROID_ID
        return Settings.Secure.getString(
                getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }


    private void fetchEnrolledEvents() {
        firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc = task.getResult();
                if (userDoc != null && userDoc.exists()) {
                    // Fetch the "Events" field, which is expected to be a HashMap
                    Map<String, String> userEvents = (Map<String, String>) userDoc.get("Events");

                    if (userEvents != null) {
                        for (Map.Entry<String, String> entry : userEvents.entrySet()) {
                            String eventId = entry.getKey();
                            String eventStatus = entry.getValue();

                            // Check if the event is "enrolled"
                            if ("enrolled".equalsIgnoreCase(eventStatus)) {
                                fetchEventDetails(eventId);
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch user events: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventDetails(String eventId) {
        firestore.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot eventDoc = task.getResult();
                if (eventDoc != null && eventDoc.exists()) {
                    Event event = eventDoc.toObject(Event.class);
                    if (event != null) {
                        event.setEventID(eventDoc.getId());
                        enrolledEvents.add(event);
                        eventsAdapter.notifyItemInserted(enrolledEvents.size() - 1);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch event details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
