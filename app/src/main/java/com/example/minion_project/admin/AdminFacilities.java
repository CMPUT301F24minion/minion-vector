// AdminFacilities.java
package com.example.minion_project.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.facility.Facility;
import com.example.minion_project.facility.FacilitiesAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminFacilities extends Fragment implements FacilitiesAdapter.OnFacilityClickListener {

    private RecyclerView recyclerView;
    private FacilitiesAdapter adapter;
    private ArrayList<Facility> facilityList;
    private FireStoreClass ourFirestore;
    private CollectionReference facilitiesRef;

    public AdminFacilities() {
        // Required empty public constructor
    }

    public static AdminFacilities newInstance() {
        return new AdminFacilities();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourFirestore = new FireStoreClass();
        facilitiesRef = ourFirestore.getFacilitiesRef();
        facilityList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_facilities, container, false);

        recyclerView = view.findViewById(R.id.adminFacilitiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FacilitiesAdapter(getContext(), facilityList);
        adapter.setOnFacilityClickListener(this); // Set the click listener
        recyclerView.setAdapter(adapter);

        fetchFacilities();

        return view;
    }

    private void fetchFacilities() {
        facilitiesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilityList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Facility facility = document.toObject(Facility.class);
                        if (facility != null) {
                            facility.setDocumentID(document.getId()); // Set document ID
                            facilityList.add(facility);
                            Log.d("AdminFacilities", "Added Facility: " + facility.toString());
                        }
                    }

                    if (facilityList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        if (getView() != null) {
                            getView().findViewById(R.id.noFacilitiesTextView).setVisibility(View.VISIBLE);
                        }
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        if (getView() != null) {
                            getView().findViewById(R.id.noFacilitiesTextView).setVisibility(View.GONE);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminFacilities", "Error fetching facilities: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch facilities.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onFacilityClick(Facility facility) {
        showDeleteConfirmationDialog(facility);
    }

    private void showDeleteConfirmationDialog(Facility facility) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Facility")
                .setMessage("Are you sure you want to delete this facility and all associated events?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteFacilityAndEvents(facility);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteFacilityAndEvents(Facility facility) {
        // Delete associated events first
        deleteAssociatedEvents(facility.getDocumentID(), () -> {
            // After deleting events, delete the facility
            facilitiesRef.document(facility.getDocumentID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility and associated events deleted successfully.", Toast.LENGTH_SHORT).show();
                        // Remove facility from local list and notify adapter
                        facilityList.remove(facility);
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminFacilities", "Error deleting facility: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to delete facility.", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void deleteAssociatedEvents(String facilityDocumentID, Runnable onComplete) {
        // Reference to the Events collection
        CollectionReference eventsRef = ourFirestore.getEventsRef();

        // Query events where eventOrganizer matches the facility's document ID
        eventsRef.whereEqualTo("eventOrganizer", facilityDocumentID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Batch delete events
                        WriteBatch batch = ourFirestore.getFirestore().batch();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            batch.delete(document.getReference());
                        }
                        // Commit the batch
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("AdminFacilities", "Associated events deleted successfully.");
                                    onComplete.run();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AdminFacilities", "Error deleting associated events: " + e.getMessage());
                                    Toast.makeText(getContext(), "Failed to delete associated events.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No associated events found
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminFacilities", "Error fetching associated events: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch associated events.", Toast.LENGTH_SHORT).show();
                });
    }
}
