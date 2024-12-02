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

/**
 * UserAdapter class for handling user selection in a RecyclerView.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final ArrayList<String> users;
    private final Set<String> selectedUsers = new HashSet<>();

    /**
     * Constructor for UserAdapter.
     * @param context The context in which the adapter is used.
     * @param users The list of users to be displayed.
     */
    public UserAdapter(Context context, ArrayList<String> users) {
        this.context = context;
        this.users = users;
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
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_with_checkbox, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * onBindViewHolder method for binding data to a ViewHolder.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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

    /**
     * getItemCount method for getting the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * getSelectedUsers method to get the set of selected users.
     * @return The set of selected users.
     */
    public Set<String> getSelectedUsers() {
        return selectedUsers;
    }

    /**
     * UserViewHolder class for holding references to the views in each item of the RecyclerView.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CheckBox userCheckBox;

        /**
         * Constructor for UserViewHolder.
         * @param itemView The View that represents the item in the RecyclerView.
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userCheckBox = itemView.findViewById(R.id.userCheckBox);
        }
    }
}
