package com.example.yoursmartgymbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder> {

    private List<NutritionPlan> nutritionPlans;
    private Context context;

    // Constructor
    public NutritionAdapter(List<NutritionPlan> nutritionPlans, Context context) {
        this.nutritionPlans = nutritionPlans;
        this.context = context;  // Context is passed here
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
        holder.description.setText(nutritionPlan.getDescription());

        // Set click listener on the card view
        holder.itemView.setOnClickListener(v -> {
            showNutritionDialog(context, nutritionPlan);  // Show the dialog when item is clicked
        });
    }

    @Override
    public int getItemCount() {
        return nutritionPlans.size();
    }

    // ViewHolder to hold references to the UI components
    public static class NutritionViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        CardView cardView;

        public NutritionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.nutritionTitle);
            description = itemView.findViewById(R.id.nutritionDescription);
            cardView = itemView.findViewById(R.id.nutritionCard);
        }
    }

    // Method to show the nutrition plan dialog
    private void showNutritionDialog(Context context, NutritionPlan plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_nutrition_plan, null);

        ImageView imageView = dialogView.findViewById(R.id.dialogImage);
        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        TextView descriptionView = dialogView.findViewById(R.id.dialogDescription);

        titleView.setText(plan.getTitle());
        descriptionView.setText(plan.getDescription());

        // Use Glide to load the image (if there's an image URL)
        Glide.with(context)
                .load(plan.getImageUrl())
                .placeholder(R.drawable.placeholder)  // Use a placeholder if image URL is null or loading
                .into(imageView);

        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
