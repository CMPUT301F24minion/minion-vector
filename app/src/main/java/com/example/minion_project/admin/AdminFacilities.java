package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.minion_project.FireStoreClass;
import com.example.minion_project.R;
import com.example.minion_project.facility.Facility;
import com.example.minion_project.facility.FacilitiesAdapter; // Update the package name
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminFacilities extends Fragment {

    private RecyclerView recyclerView;
    private com.example.minion_project.facility.FacilitiesAdapter adapter;
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
        facilitiesRef = ourFirestore.getFacilitiesRef(); // Ensure this method exists
        facilityList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_facilities, container, false);

        recyclerView = view.findViewById(R.id.adminFacilitiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new com.example.minion_project.facility.FacilitiesAdapter(getContext(), facilityList);
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
                            facility.setFacilityID(document.getId());
                            facilityList.add(facility);
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
}
