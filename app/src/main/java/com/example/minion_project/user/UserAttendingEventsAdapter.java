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

public class UserAttendingEventsAdapter extends RecyclerView.Adapter<UserAttendingEventsAdapter.EventViewHolder> {

    private final Context context;
    private final List<Event> events;

    public UserAttendingEventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_user_event_status_recycler, parent, false);
        return new EventViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDetails, eventStatus, eventDate;
        TextView selectionMessage, rejectedMessage, enrolledMessage;

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
