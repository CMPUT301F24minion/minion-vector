// AdminImages.java

package com.example.minion_project.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minion_project.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminImages extends Fragment {

    private RecyclerView recyclerView;
    private AdminImagesAdapter adapter;
    private List<String> imageUrls;

    /**
     * Called to inflate the layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_images, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView} has returned, but before any saved state has been restored.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewAdminImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        imageUrls = new ArrayList<>();
        adapter = new AdminImagesAdapter(getContext(), imageUrls, this); // Pass 'this' as the third parameter
        recyclerView.setAdapter(adapter);

        loadImagesFromFirebaseStorage();
    }

    /**
     * Load event and profile images from Firebase Storage and update the RecyclerView.
     */
    private void loadImagesFromFirebaseStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Load event images
        StorageReference eventImagesRef = storage.getReference().child("event_images");
        eventImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(e -> Log.e("AdminImages", "Error getting event image URL", e));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminImages", "Failed to list event images", e);
                    Toast.makeText(getContext(), "Failed to load event images", Toast.LENGTH_SHORT).show();
                });

        // Load profile images
        StorageReference profileImagesRef = storage.getReference().child("profile_images");
        profileImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(e -> Log.e("AdminImages", "Error getting profile image URL", e));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminImages", "Failed to list profile images", e);
                    Toast.makeText(getContext(), "Failed to load profile images", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Removes the specified image from Firebase Storage and updates the UI.
     *
     * @param imageUrl The URL of the image to be removed.
     */
    public void removeImage(String imageUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            imageUrls.remove(imageUrl);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
            Log.d("AdminImages", "Image deleted successfully: " + imageUrl);
        }).addOnFailureListener(e -> {
            Log.e("AdminImages", "Error deleting image: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to delete image.", Toast.LENGTH_SHORT).show();
        });
    }
}
