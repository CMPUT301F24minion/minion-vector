// EventsAdapter.java

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
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> eventList;
    private OnEventDeleteListener deleteListener;
    private OnImageSelectListener imageSelectListener;
    private OnItemClickListener listener;

    /**
     * Interface for handling delete actions
     */
    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    /**
     * Interface for handling item clicks
     */
    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    /**
     * Interface for handling image selection
     */
    public interface OnImageSelectListener {
        void onImageSelect(Event event);
    }

    /**
     * Constructor for EventsAdapter with delete listener
     *
     * @param context        The context in which the adapter is used
     * @param eventList      List of events to display
     * @param deleteListener Listener for delete actions
     */
    public EventsAdapter(Context context, ArrayList<Event> eventList, OnEventDeleteListener deleteListener) {
        this.context = context;
        this.eventList = eventList;
        this.deleteListener = deleteListener;
    }

    /**
     * Alternative constructor without delete listener
     *
     * @param context   The context in which the adapter is used
     * @param eventList List of events to display
     */
    public EventsAdapter(Context context, ArrayList<Event> eventList) {
        this(context, eventList, null);
    }

    /**
     * Set the item click listener
     *
     * @param listener The listener to set
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Set the image select listener
     *
     * @param listener The listener to set
     */
    public void setOnImageSelectListener(OnImageSelectListener listener) {
        this.imageSelectListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item_admin, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Bind event data to the view holder
     *
     * @param holder   The view holder
     * @param position Position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText("Date: " + event.getEventDate());
        holder.facilityName.setText("Facility: " + event.getEventOrganizer());

        String imageUrl = event.getEventImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_add)
                .into(holder.eventImage);

        // Set click listener for the event image if needed
        holder.eventImage.setOnClickListener(v -> {
            if (imageSelectListener != null) {
                imageSelectListener.onImageSelect(event);
            }
        });

        // Set click listener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onEventDelete(event);
            }
        });

        // Set click listener for the entire item if needed
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for holding the view for each event item
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, facilityName;
        ImageView eventImage;
        ImageView deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_Date);
            facilityName = itemView.findViewById(R.id.facility);
            eventImage = itemView.findViewById(R.id.event_image);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
        }
    }
}
