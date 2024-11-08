package com.example.minion_project.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        String imageUrl = event.getEventImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_add) // Optional placeholder image
                .into(holder.eventImage);

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

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_Date);
            eventImage = itemView.findViewById(R.id.event_image);
        }
    }
}
