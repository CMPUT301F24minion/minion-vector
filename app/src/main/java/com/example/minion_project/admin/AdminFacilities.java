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
import com.example.minion_project.facility.FacilitiesAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminFacilities extends Fragment {

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
                            // Remove this line to prevent overwriting
                            // facility.setFacilityID(document.getId());
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
}
