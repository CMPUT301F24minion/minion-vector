package com.example.minion_project.facility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.minion_project.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * FacilitiesAdapter: Adapter for displaying a list of facilities in a RecyclerView.
 */
public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.ViewHolder> {

    private Context context;
    private List<Facility> facilityList;
    private OnFacilityClickListener facilityClickListener;

    /**
     * Interface for handling facility click events
     */
    public interface OnFacilityClickListener {
        void onFacilityClick(Facility facility);
    }

    /**
     * Set the facility click listener
     * @param listener The listener
     */
    public void setOnFacilityClickListener(OnFacilityClickListener listener) {
        this.facilityClickListener = listener;
    }

    /**
     * Constructor for FacilitiesAdapter
     * @param context The context of the activity
     * @param facilityList The list of facilities to display
     */
    public FacilitiesAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    /**
     * ViewHolder class for holding the view for each facility item
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView facilityNameTextView;
        ImageView facilityImageView;

        /**
         * Constructor for ViewHolder
         * @param itemView The view for the facility item
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            facilityNameTextView = itemView.findViewById(R.id.facilityNameTextView);
            facilityImageView = itemView.findViewById(R.id.facilityImageView);

            // Set click listener on the entire item view
            itemView.setOnClickListener(this);
        }

        /**
         * Handle click events on the item view
         * @param view The view that was clicked
         */
        @Override
        public void onClick(View view) {
            if (facilityClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Facility clickedFacility = facilityList.get(position);
                    facilityClickListener.onFacilityClick(clickedFacility);
                }
            }
        }
    }

    /**
     * onCreateViewHolder
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public FacilitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_facility, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FacilitiesAdapter.ViewHolder holder, int position) {
        Facility facility = facilityList.get(position);

        // Log facility details for debugging
        Log.d("FacilitiesAdapter", "Binding Facility - Name: " + facility.getFacilityID());
        Log.d("FacilitiesAdapter", "Binding Facility - Image URL: " + facility.getFacilityImage());

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

    /**
     * getItemCount
     * @return facilityList size
     */
    @Override
    public int getItemCount() {
        return facilityList.size();
    }
}
