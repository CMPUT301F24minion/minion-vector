package com.example.minion_project.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserEventStatusAdapter extends RecyclerView.Adapter<UserEventStatusAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserEvent> eventList;


    public UserEventStatusAdapter(Context context, ArrayList<UserEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user_event_status_recycler, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserEvent event = eventList.get(position);

        holder.eventTitle.setText("Title: " + event.getEventName());
        holder.eventDetails.setText("Description: "+event.getEventDescription());
        holder.eventsDate.setText("Date: "+event.getEventDate());
        holder.eventStatus.setText("Status: "+event.getStatus());


        //        holder.eventDetails.setText(event.getEventDescription());

        // Set event image (you could load images via Glide if needed)
//        holder.eventImage.setImageResource(R.drawable.baseline_location);  // Replace with actual image resource
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView eventImage;
        public TextView eventTitle;
        public TextView eventDetails;
        public TextView eventStatus;
        public TextView eventsDate;
        public ViewHolder(View view) {
            super(view);
            eventsDate = view.findViewById(R.id.eventDate);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDetails = view.findViewById(R.id.eventDetails);
            eventStatus = view.findViewById(R.id.eventStatus);
        }
    }
}