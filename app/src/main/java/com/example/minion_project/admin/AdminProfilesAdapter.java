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

public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener clickListener;

    // Listener interface for item clicks
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    // Setter for the click listener
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.clickListener = listener;
    }

    public AdminProfilesAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    // ViewHolder class to hold each item view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userEmailTextView;
        TextView userPhoneNumberTextView;
        TextView userCityTextView;
        ImageView userIconImageView;

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

    @NonNull
    @Override
    public AdminProfilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_profile, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
