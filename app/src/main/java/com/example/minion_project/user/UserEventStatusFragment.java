package com.example.minion_project.user;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;

import java.util.ArrayList;
import java.util.List;

public class UserEventStatusFragment extends Fragment {

    private RecyclerView userEventStatusRecyclerView;
    private UserEventStatusAdapter adapter;
    private List<Event> eventList;

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
        eventList.add(new Event("ahasdhasdfahfuahf", "Event1"));
        eventList.add(new Event("ksfjgkdfgjskjgd", "Event2"));
        eventList.add(new Event("sfkjgsfjkgskfjgsf", "Event3"));

        // Set the adapter
        adapter = new UserEventStatusAdapter(eventList);
        userEventStatusRecyclerView.setAdapter(adapter);

        return rootView;
    }
}
