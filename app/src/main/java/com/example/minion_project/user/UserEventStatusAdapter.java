package com.example.minion_project.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.example.minion_project.events.Event;
import com.example.minion_project.events.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying user event status.
 */
public class UserEventStatusAdapter extends RecyclerView.Adapter<UserEventStatusAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserEvent> eventList;
    private UserController userController;

    /**
     * Constructor for UserEventStatusAdapter.
     * @param context The context in which the adapter is used.
     * @param eventList The list of events to display.
     * @param usercontroller The user controller for managing user interactions with events.
     */
    public UserEventStatusAdapter(Context context, ArrayList<UserEvent> eventList,UserController usercontroller) {
        this.context = context;
        this.eventList = eventList;
        this.userController=usercontroller;
    }


    /**
     * Creates a new ViewHolder for the RecyclerView.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user_event_status_recycler, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Updates the contents of the ViewHolder to reflect the item at the given position.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserEvent event = eventList.get(position);
        if ("invited".equals(event.getStatus())) {
            holder.eventSelectedTextView.setVisibility(View.VISIBLE);
            // Set OnClickListener if status is "invited"
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                /**
                 * Handles click events on the item view.
                 * @param v The View that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Accept Invite")
                            .setMessage("Do you want to accept or decline the invitation?")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                /**
                                 * Handles click events on the Accept button.
                                 * @param dialog The AlertDialog that was clicked.
                                 * @param id The ID of the clicked button.
                                 */
                                public void onClick(DialogInterface dialog, int id) {
                                    // Handle Accept action
                                    Toast.makeText(context, "You have accepted the invitation!", Toast.LENGTH_SHORT).show();
                                    //set the user to enrolled
                                    userController.AcceptInvite(event.getEventID());
                                    event.setStatus("enrolled");  // Assuming the status will be "enrolled" after accept
                                    notifyItemChanged(position);

                                }
                            })
                            .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                /**
                                 * Handles click events on the Decline button.
                                 * @param dialog The AlertDialog that was clicked.
                                 * @param id The ID of the clicked button.
                                 */
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(context, "You have declined the invitation!", Toast.LENGTH_SHORT).show();

                                    //set the event to declined
                                    userController.DeclineInvite(event.getEventID());
                                    event.setStatus("declined");  // Assuming the status will be "enrolled" after accept
                                    notifyItemChanged(position);
                                }
                            })
                            .create()
                            .show();
                }
            });
        }else if ("rejected".equals(event.getStatus() )|| "declined".equals(event.getStatus())){
            holder.eventRejectedView.setVisibility(View.VISIBLE);
        }else if ("enrolled".equals(event.getStatus())) {
            holder.eventAcceptedView.setVisibility(View.VISIBLE);
        }

        holder.eventTitle.setText("Title: " + event.getEventName());
        holder.eventDetails.setText("Description: "+event.getEventDescription());
        holder.eventsDate.setText("Date: "+event.getEventDate());
        holder.eventStatus.setText("Status: "+event.getStatus());


    }

    /**
     * get eventList size
     * @return The number of items in the eventList.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for holding the views for each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView eventImage;
        public TextView eventTitle;
        public TextView eventDetails;
        public TextView eventStatus;
        public TextView eventsDate;
        public  TextView eventSelectedTextView;
        public TextView eventRejectedView;
        public TextView eventAcceptedView;

        /**
         * Constructor for ViewHolder.
         * @param view The View that holds the layout for each item in the RecyclerView.
         */
        public ViewHolder(View view) {
            super(view);
            eventsDate = view.findViewById(R.id.eventDate);
            eventTitle = view.findViewById(R.id.eventTitle);
            eventDetails = view.findViewById(R.id.eventDetails);
            eventStatus = view.findViewById(R.id.eventStatus);
            eventSelectedTextView=view.findViewById(R.id.selectionMessage);
            eventRejectedView=view.findViewById(R.id.rejectedMessage);
            eventAcceptedView=view.findViewById(R.id.enrolledMessage);

        }
    }
}