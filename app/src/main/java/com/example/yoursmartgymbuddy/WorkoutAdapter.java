package com.example.yoursmartgymbuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Workout workout);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_workout, parent, false);
        return new WorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.workoutName.setText(workout.getName());
        holder.workoutDetails.setText("Sets: " + workout.getSets() + ", Reps: " + workout.getReps() + ", Duration: " + workout.getDuration());

        // Load GIF image using Glide
        Glide.with(holder.itemView.getContext())
                .asGif() // Crucial for loading GIFs
                .load(workout.getImageResource()) // Load the drawable resource ID
                .into(holder.workoutImage); // The ImageView to display the GIF
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public ImageView workoutImage;
        public TextView workoutName;
        public TextView workoutDetails;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutImage = itemView.findViewById(R.id.workoutImage);
            workoutName = itemView.findViewById(R.id.workoutName);
            workoutDetails = itemView.findViewById(R.id.workoutDetails);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(workoutList.get(position));
                }
            });
        }
    }
}