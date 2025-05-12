package com.example.yoursmartgymbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder> {

    private List<NutritionPlan> nutritionPlans;
    private Context context;
    // Map to keep track of the expanded state of each item
    private Map<Integer, Boolean> expandedStates = new HashMap<>();

    // Constructor
    public NutritionAdapter(List<NutritionPlan> nutritionPlans, Context context) {
        this.nutritionPlans = nutritionPlans;
        this.context = context;
        // Initialize all items as collapsed
        for (int i = 0; i < nutritionPlans.size(); i++) {
            expandedStates.put(i, false);
        }
    }

    @Override
    public NutritionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrition, parent, false);
        return new NutritionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NutritionViewHolder holder, int position) {
        NutritionPlan nutritionPlan = nutritionPlans.get(position);

        holder.title.setText(nutritionPlan.getTitle());
        // Set both short and full descriptions to the same text from the plan
        holder.shortDescription.setText(nutritionPlan.getDescription());
        holder.fullDescription.setText(nutritionPlan.getDescription());

        // Use Glide to load the image
        Glide.with(context)
                .load(nutritionPlan.getImageUrl())
                .placeholder(R.drawable.placeholder) // Use a placeholder if image URL is null or loading
                .error(R.drawable.placeholder) // Optional error image
                .into(holder.imageViewNutrition);

        // Set initial visibility based on expanded state
        Boolean isExpanded = expandedStates.get(position);
        if (isExpanded != null && isExpanded) {
            holder.shortDescription.setVisibility(View.GONE);
            holder.fullDescription.setVisibility(View.VISIBLE);
        } else {
            holder.shortDescription.setVisibility(View.VISIBLE);
            holder.fullDescription.setVisibility(View.GONE);
        }

        // Set click listener to toggle description visibility
        holder.itemView.setOnClickListener(v -> {
            boolean currentlyExpanded = expandedStates.get(position) != null && expandedStates.get(position);
            expandedStates.put(position, !currentlyExpanded);
            notifyItemChanged(position); // Notify the adapter to rebind the item
        });
    }

    @Override
    public int getItemCount() {
        return nutritionPlans.size();
    }

    // ViewHolder to hold references to the UI components
    public static class NutritionViewHolder extends RecyclerView.ViewHolder {
        TextView title, shortDescription, fullDescription;
        ImageView imageViewNutrition; // Added ImageView
        CardView cardView; // Keep the CardView reference if needed for other purposes

        public NutritionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.nutritionTitle);
            shortDescription = itemView.findViewById(R.id.nutritionShortDescription); // Renamed
            fullDescription = itemView.findViewById(R.id.nutritionFullDescription); // Added
            imageViewNutrition = itemView.findViewById(R.id.imageViewNutrition); // Added
            cardView = itemView.findViewById(R.id.nutritionCard);
        }
    }

    // Removed the showNutritionDialog method as we are expanding within the item
    // private void showNutritionDialog(Context context, NutritionPlan plan) { ... }
}