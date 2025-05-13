package com.example.yoursmartgymbuddy;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder> {

    private List<WorkoutSession> workoutSessions;

    public WorkoutHistoryAdapter(List<WorkoutSession> workoutSessions) {
        this.workoutSessions = workoutSessions;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_history, parent, false);
        return new WorkoutViewHolder(itemView);
    }

    @SuppressLint("SetTextI18WithPlaceholders")
    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutSession currentWorkout = workoutSessions.get(position);

        // FIX: Use getWorkoutName() instead of getWorkoutType()
        holder.workoutTypeTextView.setText(currentWorkout.getWorkoutName());
        holder.durationTextView.setText("Duration: " + currentWorkout.getDurationMinutes() + " minutes");
        holder.caloriesBurnedTextView.setText("Calories Burned: " + currentWorkout.getEstimatedCaloriesBurned() + " kcal");

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        holder.dateTextView.setText(dateFormat.format(new Date(currentWorkout.getDate())));

        // Display specific workout details if they exist
        Map<String, Object> detailsMap = currentWorkout.getDetails();
        if (detailsMap != null && !detailsMap.isEmpty()) {
            holder.workoutDetailsTextView.setVisibility(View.VISIBLE);
            // FIX: Pass getWorkoutName() to buildDetailsString
            String detailsText = buildDetailsString(detailsMap, currentWorkout.getWorkoutName());
            holder.workoutDetailsTextView.setText("Details: " + detailsText);
        } else {
            holder.workoutDetailsTextView.setVisibility(View.GONE);
        }
    }

    private String buildDetailsString(Map<String, Object> detailsMap, String workoutType) {
        switch (workoutType) {
            case "Weightlifting":
                String exercise = (String) detailsMap.get("exercise");
                Long setsLong = (Long) detailsMap.get("sets"); // Firestore reads numbers as Long
                int sets = setsLong != null ? setsLong.intValue() : 0;
                Long repsLong = (Long) detailsMap.get("reps");
                int reps = repsLong != null ? repsLong.intValue() : 0;
                Double weight = (Double) detailsMap.get("weight");
                return (exercise != null ? exercise : "N/A") + " - " + sets + " sets of " + reps + " reps at " + (weight != null ? weight : 0.0) + " kg";
            case "Cardio":
                Double distance = (Double) detailsMap.get("distance");
                String pace = (String) detailsMap.get("pace");
                return "Distance: " + (distance != null ? distance : 0.0) + " km, Pace: " +(pace != null ? pace : "N/A");
            // Add more cases for other workout types
            default:
                StringBuilder genericDetails = new StringBuilder();
                for (Map.Entry<String, Object> entry : detailsMap.entrySet()) {
                    if (genericDetails.length() > 0) {
                        genericDetails.append(", ");
                    }
                    genericDetails.append(entry.getKey()).append(": ").append(entry.getValue());
                }
                return genericDetails.toString();
        }
    }

    @Override
    public int getItemCount() {
        return workoutSessions.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutTypeTextView;
        TextView dateTextView;
        TextView durationTextView;
        TextView caloriesBurnedTextView;
        TextView workoutDetailsTextView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutTypeTextView = itemView.findViewById(R.id.workoutTypeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            caloriesBurnedTextView = itemView.findViewById(R.id.caloriesBurnedTextView);
            workoutDetailsTextView = itemView.findViewById(R.id.workoutDetailsTextView);
        }
    }
}