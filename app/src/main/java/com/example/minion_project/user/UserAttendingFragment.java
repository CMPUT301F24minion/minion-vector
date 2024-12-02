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

/**
 * UserAttendingFragment class for handling the RecyclerView for user attending events.
 */
public class UserAttendingFragment extends Fragment {

    private RecyclerView userEventStatusRecyclerView;
    private UserAttendingEventsAdapter eventsAdapter;
    private List<Event> enrolledEvents;
    private FirebaseFirestore firestore;
    private String currentUserId; // Replace this with your method of fetching the logged-in user's ID

    /**
     * onCreateView method for creating the view for the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
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

    /**
     * getCurrentUserId method for getting the unique device ID.
     * @return The unique device ID.
     */
    private String getCurrentUserId() {
        // Retrieve the unique device ID using ANDROID_ID
        return Settings.Secure.getString(
                getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }


    /**
     * fetchEnrolledEvents method for fetching the user's enrolled events.
     */
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

    /**
     * fetchEventDetails method for fetching event details.
     * @param eventId The ID of the event to fetch details for.
     */
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
