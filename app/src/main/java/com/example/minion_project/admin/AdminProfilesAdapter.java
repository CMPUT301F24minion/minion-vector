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

    public AdminProfilesAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userEmailTextView;
        TextView userPhoneNumberTextView;
        TextView userCityTextView;
        ImageView userIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
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
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getName());
        holder.userEmailTextView.setText(user.getEmail());
        holder.userPhoneNumberTextView.setText(user.getPhoneNumber());
        holder.userCityTextView.setText(user.getLocation());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
