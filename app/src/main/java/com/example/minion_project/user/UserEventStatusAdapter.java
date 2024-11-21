package com.example.minion_project.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;

import java.util.List;

public class UserEventStatusAdapter extends RecyclerView.Adapter<UserEventStatusAdapter.ViewHolder> {

    private List<Event> eventList;

    public UserEventStatusAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_event_status, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventTitle.setText(event.getEventName());
        holder.eventDetails.setText(event.getEventDetails());

        // Set event image (you could load images via Glide if needed)
        holder.eventImage.setImageResource(R.drawable.baseline_location);  // Replace with actual image resource
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventImage;
        public TextView eventTitle;
        public TextView eventDetails;
        public TextView eventStatus;

        public ViewHolder(View view) {
            super(view);
            eventImage = view.findViewById(R.id.eventImage);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDetails = view.findViewById(R.id.eventDetails);
            eventStatus = view.findViewById(R.id.eventStatus);
        }
    }
}