/**
 * Adapter class for displaying images in a RecyclerView for the admin view.
 */

package com.example.minion_project.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.minion_project.R;

import java.util.List;

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> imageUrls;

    /**
     * Constructor for AdminImagesAdapter
     * @param context  The context in which the adapter is used.
     * @param imageUrls A list of image URLs to be displayed in the RecyclerView.
     */
    public AdminImagesAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_image, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return imageUrls.size
     */
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * ViewHolder class for holding the view for each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Constructor for ViewHolder
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
