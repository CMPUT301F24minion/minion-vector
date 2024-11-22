package com.example.minion_project.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserEventStatusFragment extends Fragment {

    private RecyclerView userEventStatusRecyclerView;
    private UserEventStatusAdapter adapter;
    private ArrayList<UserEvent> eventList;
    private FireStoreClass ourFirestore=new FireStoreClass();
    private UserController userController;
    public UserEventStatusFragment(UserController userController) {
        this.userController=userController;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_event_status, container, false);

        // Initialize RecyclerView
        userEventStatusRecyclerView = rootView.findViewById(R.id.userEventStatusRecyclerView);
        userEventStatusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Prepare event data (this can be dynamic or fetched from a database)
        eventList = new ArrayList<>();
        adapter = new UserEventStatusAdapter(getContext(), eventList);
        fetchEvents();
        // Set the adapter
        userEventStatusRecyclerView.setAdapter(adapter);

        return rootView;
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, facilityName;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_Date);
        }
    }
    private void fetchEvents() {
        HashMap<String,String> eventIds = userController.user.getAllEvents();

        FirebaseFirestore db = ourFirestore.getFirestore();

        for (String eventId : eventIds.keySet()) {
            db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String eventID = document.getId();
                        String status = userController.getUserEventStatus(eventID);
                        String eventDescription = document.getString("eventDescription");
                        String eventName = document.getString("eventName");
                        String eventDate = document.getString("eventDate");
                        UserEvent uEvent = new UserEvent(eventID, status, eventDescription, eventName, eventDate);
                        Log.e("userfrag", "SUCCESS to add event: " + uEvent);

                        if (uEvent != null) {
//                            uEvent.setEventID(document.getId());
                            eventList.add(uEvent);
                            adapter.notifyItemInserted(eventList.size() - 1);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
