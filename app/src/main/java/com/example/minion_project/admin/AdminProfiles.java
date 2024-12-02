package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Fragment class for managing and displaying user profiles in the admin view.
 * Includes functionality to delete user profiles upon tapping on them.
 */
public class AdminProfiles extends Fragment implements AdminProfilesAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private AdminProfilesAdapter adapter;
    private ArrayList<User> userList;
    private FireStoreClass ourFirestore;
    private CollectionReference usersRef;

    private static final String TAG = "AdminProfiles";

    /**
     * Default constructor required for Fragment.
     */
    public AdminProfiles() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     * @return
     */
    public static AdminProfiles newInstance() {
        return new AdminProfiles();
    }

    /**
     * Called to do initial creation of a fragment.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourFirestore = new FireStoreClass();
        usersRef = ourFirestore.getUsersRef();
        userList = new ArrayList<>();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_profiles, container, false);
    }

    /**
     * Called immediately after the fragment's view has been created.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.adminProfilesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminProfilesAdapter(getContext(), userList);
        adapter.setOnUserClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);

        fetchUsers();
    }

    /**
     * Fetch all users from Firestore and populate the RecyclerView.
     * Manually maps Firestore fields to User object fields.
     */
    private void fetchUsers(){
        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                        // Manually extract fields from Firestore document
                        String deviceID = document.getId();
                        String name = document.getString("Name"); // Note the capital 'N'
                        String email = document.getString("Email"); // Capital 'E'
                        String phoneNumber = document.getString("Phone_number"); // Match the exact field name
                        String city = document.getString("City"); // Capital 'C'

                        // Create a new User object
                        User user = new User();
                        user.setDeviceID(deviceID);
                        user.setName(name != null ? name : "No Name");
                        user.setEmail(email != null ? email : "No Email");
                        user.setPhoneNumber(phoneNumber != null ? phoneNumber : "No Phone");
                        user.setLocation(city != null ? city : "No City");

                        // Add the user to the list
                        userList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching users: " + e.getMessage());
                });
    }

    /**
     * Called when a user profile item is clicked.
     * Shows a confirmation dialog before deleting the user.
     *
     * @param user The user to be deleted.
     */
    @Override
    public void onUserClick(User user) {
        // Show a confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Delete User")
                .setMessage("Do you want to delete this user profile?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Show second confirmation dialog
                    new AlertDialog.Builder(getContext())
                            .setTitle("Are you sure?")
                            .setMessage("This action cannot be undone.")
                            .setPositiveButton("Delete", (innerDialog, innerWhich) -> {
                                deleteUser(user);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the user from Firestore and updates the local list.
     *
     * @param user The user to be deleted.
     */
    private void deleteUser(User user) {
        // References to Firestore user documents in both collections
        DocumentReference userDocRef = usersRef.document(user.getDeviceID());
        CollectionReference allUsersRef = ourFirestore.getAll_UsersRef();
        DocumentReference allUserDocRef = allUsersRef.document(user.getDeviceID());

        // Delete user from 'Users' collection
        userDocRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // After deleting from 'Users', delete from 'All_Users'
                    allUserDocRef.delete()
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(getContext(), "User deleted successfully from both collections.", Toast.LENGTH_SHORT).show();

                                // Remove user from local list and update adapter
                                userList.remove(user);
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error deleting user from 'All_Users': " + e.getMessage());
                                Toast.makeText(getContext(), "Failed to delete user from 'All_Users'.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user from 'Users': " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to delete user.", Toast.LENGTH_SHORT).show();
                });
    }


}
