package com.example.minion_project.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final ArrayList<String> users;
    private final Set<String> selectedUsers = new HashSet<>();

    public UserAdapter(Context context, ArrayList<String> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_with_checkbox, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userName = users.get(position);
        holder.userName.setText(userName);
        holder.userCheckBox.setOnCheckedChangeListener(null); // Reset listener

        // Set checkbox state based on selection
        holder.userCheckBox.setChecked(selectedUsers.contains(userName));

        // Add or remove user from selected set when checkbox is toggled
        holder.userCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUsers.add(userName);
            } else {
                selectedUsers.remove(userName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public Set<String> getSelectedUsers() {
        return selectedUsers;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CheckBox userCheckBox;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userCheckBox = itemView.findViewById(R.id.userCheckBox);
        }
    }
}
