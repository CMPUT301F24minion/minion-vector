package com.example.minion_project.facility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import necessary components
import android.widget.ImageView;
import android.widget.TextView;
import com.example.minion_project.R;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.ViewHolder> {

    private Context context;
    private List<com.example.minion_project.facility.Facility> facilityList;

    public FacilitiesAdapter(Context context, List<com.example.minion_project.facility.Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    // ViewHolder class to hold each item view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityNameTextView;
        TextView facilityLocationTextView;
        ImageView facilityImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            facilityNameTextView = itemView.findViewById(R.id.facilityNameTextView);
            facilityLocationTextView = itemView.findViewById(R.id.facilityLocationTextView);
            facilityImageView = itemView.findViewById(R.id.facilityImageView);
        }
    }

    @NonNull
    @Override
    public FacilitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_facility, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilitiesAdapter.ViewHolder holder, int position) {
        com.example.minion_project.facility.Facility facility = facilityList.get(position);
        // Set the facility's details
        holder.facilityNameTextView.setText(facility.getName());
        holder.facilityLocationTextView.setText(facility.getLocation());

        // Load facility image using Glide or any other image loading library
        Glide.with(context)
                .load(facility.getImageUrl())
                .placeholder(R.drawable.business_center)
                .into(holder.facilityImageView);
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }
}
