package com.example.minion_project.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.minion_project.R;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> eventList;
    private OnEventDeleteListener deleteListener;

    // Listener Interface
    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    // Constructor with OnEventDeleteListener
    public EventsAdapter(Context context, ArrayList<Event> eventList, OnEventDeleteListener deleteListener) {
        this.context = context;
        this.eventList = eventList;
        this.deleteListener = deleteListener;
    }

    // Overloaded Constructor without OnEventDeleteListener
    public EventsAdapter(Context context, ArrayList<Event> eventList) {
        this(context, eventList, null);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText("Date: " + event.getEventDate());
        holder.eventTime.setText("Time: " + event.getEventTime());
        holder.eventLocation.setText("Location: " + event.getEventLocation());
        holder.eventDescription.setText("Description: " + event.getEventDescription());
        holder.eventCapacity.setText("Capacity: " + event.getEventCapacity());
        holder.eventOrganizer.setText("Organizer: " + event.getEventOrganizer());

        // Set up delete button click if listener is provided
        if (deleteListener != null) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> deleteListener.onEventDelete(event));
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Optionally, load event image using a library like Glide or Picasso
        // For example, using Glide:
        // Glide.with(context).load(event.getEventImage()).into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder Class
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventTime, eventLocation, eventDescription, eventCapacity, eventOrganizer;
        ImageView eventImage;
        TextView deleteButton; // Changed from ImageButton to TextView

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
            deleteButton = itemView.findViewById(R.id.deleteEventButton);
        }
    }
}
