/**
 * EventsAdapter: Adapter class for displaying a list of events in a RecyclerView
 */

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> eventList;
    private OnEventDeleteListener deleteListener;

    /**
     * Interface for handling event deletion
     */
    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    /**
     * Constructor for EventsAdapter
     * @param context
     * @param eventList list of events to be displayed
     * @param deleteListener listener for handling event deletion
     */
    public EventsAdapter(Context context, ArrayList<Event> eventList, OnEventDeleteListener deleteListener) {
        this.context = context;
        this.eventList = eventList;
        this.deleteListener = deleteListener;
    }

    /**
     * Constructor for EventsAdapter without a delete listener
     * @param context
     * @param eventList list of events to be displayed
     */
    public EventsAdapter(Context context, ArrayList<Event> eventList) {
        this(context, eventList, null);
    }

    /**
     * Called when the ViewHolder is created
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds event data to the ViewHolder
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText("Date: " + event.getEventDate());
        fetchFacilityName(event.getEventOrganizer(), holder.facilityName);
        String imageUrl = event.getEventImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_add) // Optional placeholder image
                .into(holder.eventImage);

        // Optionally, load event image using a library like Glide or Picasso
        // For example, using Glide:
        // Glide.with(context).load(event.getEventImage()).into(holder.eventImage);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for holding the views for each event item
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, facilityName;
        ImageView eventImage;

        /**
         * Constructor for EventViewHolder
         * @param itemView The View that represents the item in the RecyclerView
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_Date);
            eventImage = itemView.findViewById(R.id.event_image);
            facilityName = itemView.findViewById(R.id.facility); // Make sure you have the correct ID
        }
    }

    private void fetchFacilityName(String organizerID, TextView facilityTextView) {
        FirebaseFirestore.getInstance().collection("Organizers")
                .document(organizerID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facilityName = documentSnapshot.getString("facilityName");
                        facilityTextView.setText("Facility: " + facilityName);
                    } else {
                        facilityTextView.setText("Facility: Unknown");
                    }
                })
                .addOnFailureListener(e -> {
                    facilityTextView.setText("Facility: Error");
                });
    }
}
