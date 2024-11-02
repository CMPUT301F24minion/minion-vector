package com.example.minion_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.Event;
import com.example.minion_project.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> eventList;

    // Constructor to pass the context and event list
    public EventsAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the event_item layout
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Bind data to the view holder
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate());
        holder.eventTime.setText(event.getEventTime());
        holder.eventLocation.setText(event.getEventLocation());
        holder.eventDescription.setText(event.getEventDescription());
        holder.eventCapacity.setText(String.valueOf(event.getEventCapacity()));

        // Set the event image if applicable (assuming event has an image resource or URL)
        // holder.eventImage.setImageResource(event.getImageResource()); // Replace with actual image loading if needed
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder class for RecyclerView items
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventName, eventDate, eventTime, eventLocation, eventDescription, eventCapacity, eventOrganizer;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            eventCapacity = itemView.findViewById(R.id.eventCapacity);
            eventOrganizer = itemView.findViewById(R.id.eventOrganizer);
        }
    }
}
