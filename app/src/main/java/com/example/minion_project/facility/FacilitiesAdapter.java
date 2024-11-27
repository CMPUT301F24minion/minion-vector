package com.example.minion_project.facility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minion_project.R;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.ViewHolder> {

    private Context context;
    private List<Facility> facilityList;

    public FacilitiesAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    // ViewHolder class to hold each item view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityNameTextView;
        ImageView facilityImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            facilityNameTextView = itemView.findViewById(R.id.facilityNameTextView);
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
        Facility facility = facilityList.get(position);

        // Set the facility's name using facilityID
        String facilityName = facility.getFacilityID();
        if (facilityName != null && !facilityName.isEmpty()) {
            holder.facilityNameTextView.setText(facilityName);
        } else {
            holder.facilityNameTextView.setText("No Name Provided");
        }

        // Load facility image using Glide
        Glide.with(context)
                .load(facility.getFacilityImage())
                .placeholder(R.drawable.business_center) // Ensure this drawable exists
                .into(holder.facilityImageView);
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }
}
