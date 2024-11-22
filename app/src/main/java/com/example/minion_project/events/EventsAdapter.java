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
    private OnImageSelectListener imageSelectListener;
    private OnItemClickListener listener;

    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    // for organizer view event
    public interface OnItemClickListener {
        void onItemClick(Event event);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface OnImageSelectListener {
        void onImageSelect(Event event);
    }

    public EventsAdapter(Context context, ArrayList<Event> eventList, OnEventDeleteListener deleteListener) {
        this.context = context;
        this.eventList = eventList;
        this.deleteListener = deleteListener;
    }

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
        fetchFacilityName(event.getEventOrganizer(), holder.facilityName);

        String imageUrl = event.getEventImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_add)
                .into(holder.eventImage);

        holder.eventImage.setOnClickListener(v -> {
            if (imageSelectListener != null) {
                imageSelectListener.onImageSelect(event); // Notify listener
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(event);  // Trigger listener when an item is clicked
            }
        });
    }

    private void fetchFacilityName(String organizerID, TextView facilityTextView) {
        if (organizerID == null || organizerID.isEmpty()) {
            facilityTextView.setText("Facility: Unknown");
            return;
        }
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
                .addOnFailureListener(e -> facilityTextView.setText("Facility: Error"));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setOnImageSelectListener(OnImageSelectListener listener) {
        this.imageSelectListener = listener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, facilityName;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_Date);
            eventImage = itemView.findViewById(R.id.event_image);
            facilityName = itemView.findViewById(R.id.facility);
        }
    }
}
