package com.example.minion_project.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;

import java.util.List;

/**
 * UserAttendingEventsAdapter class for handling the RecyclerView for user attending events.
 */
public class UserAttendingEventsAdapter extends RecyclerView.Adapter<UserAttendingEventsAdapter.EventViewHolder> {

    private final Context context;
    private final List<Event> events;

    /**
     * Constructor for UserAttendingEventsAdapter.
     * @param context The context in which the adapter is used.
     * @param events The list of events to be displayed.
     */
    public UserAttendingEventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    /**
     * onCreateViewHolder method for creating a new ViewHolder.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_user_event_status_recycler, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * onBindViewHolder method for binding data to a ViewHolder.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.eventTitle.setText(event.getEventName());
        holder.eventDetails.setText(event.getEventDetails());
        holder.eventStatus.setText("Status: Enrolled");
        holder.eventDate.setText(event.getEventDate());

        // Set visibility of messages based on event status
        holder.selectionMessage.setVisibility(View.GONE);
        holder.rejectedMessage.setVisibility(View.GONE);
        holder.enrolledMessage.setVisibility(View.VISIBLE);
    }

    /**
     * getItemCount method for getting the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * EventViewHolder class for holding references to the views in each item of the RecyclerView.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDetails, eventStatus, eventDate;
        TextView selectionMessage, rejectedMessage, enrolledMessage;

        /**
         * Constructor for EventViewHolder.
         * @param itemView The View that represents the item in the RecyclerView.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDetails = itemView.findViewById(R.id.eventDetails);
            eventStatus = itemView.findViewById(R.id.eventStatus);
            eventDate = itemView.findViewById(R.id.eventDate);

            selectionMessage = itemView.findViewById(R.id.selectionMessage);
            rejectedMessage = itemView.findViewById(R.id.rejectedMessage);
            enrolledMessage = itemView.findViewById(R.id.enrolledMessage);
        }
    }
}
