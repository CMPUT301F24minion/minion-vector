package com.example.minion_project.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import necessary components
import android.widget.ImageView;
import android.widget.TextView;
import com.example.minion_project.R;
import com.example.minion_project.user.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for the RecyclerView in AdminProfiles.
 */
public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener clickListener;

    /**
     * Interface for handling click events on user profiles.
     */
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    /**
     * Set the click listener for user profiles.
     * @param listener The click listener to be set.
     */
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * Constructor for the adapter.
     * @param context The context in which the adapter is used.
     * @param userList The list of users to be displayed.
     */
    public AdminProfilesAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    /**
     * ViewHolder class for the RecyclerView items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userEmailTextView;
        TextView userPhoneNumberTextView;
        TextView userCityTextView;
        ImageView userIconImageView;

        /**
         * Constructor for the ViewHolder.
         * @param itemView The view representing an individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
            userPhoneNumberTextView = itemView.findViewById(R.id.userPhoneNumberTextView);
            userCityTextView = itemView.findViewById(R.id.userCityTextView);
            userIconImageView = itemView.findViewById(R.id.userIconImageView);
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public AdminProfilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_profile, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AdminProfilesAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        // Set the user's details
        holder.userNameTextView.setText(user.getName());
        holder.userEmailTextView.setText(user.getEmail());
        holder.userPhoneNumberTextView.setText(user.getPhoneNumber());
        holder.userCityTextView.setText(user.getLocation());

        // Set click listener on the entire itemView
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserClick(user);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return userList.size()
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }
}
