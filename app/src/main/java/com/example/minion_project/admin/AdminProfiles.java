package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import necessary components
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class AdminProfiles extends Fragment {

    private RecyclerView recyclerView;
    private AdminProfilesAdapter adapter;
    private ArrayList<User> userList;
    private FireStoreClass ourFirestore;
    private CollectionReference usersRef;

    private static final String TAG = "AdminProfiles";

    public AdminProfiles() {
        // Required empty public constructor
    }

    public static AdminProfiles newInstance() {
        return new AdminProfiles();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourFirestore = new FireStoreClass();
        usersRef = ourFirestore.getUsersRef(); // Ensure this method returns the users collection
        userList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.adminProfilesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminProfilesAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);

        fetchUsers();
    }

    private void fetchUsers() {
        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Manually extract fields from Firestore document
                        String deviceID = document.getId();
                        String name = document.getString("Name"); // Note the capital 'N'
                        String email = document.getString("Email"); // Capital 'E'
                        String phoneNumber = document.getString("Phone_number"); // Match the exact field name
                        String city = document.getString("City"); // Capital 'C'
                        // Other fields as needed

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

}
