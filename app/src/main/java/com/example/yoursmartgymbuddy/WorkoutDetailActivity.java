package com.example.yoursmartgymbuddy;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    private ImageView workoutDetailImage;
    private TextView workoutDetailName;
    private TextView workoutDetailDescription;
    private TextView workoutDetailSetsRepsDuration;
    private TextView workoutDetailTarget;
    private TextView workoutDetailInstructions;
    private TextView timerTextView;
    private Button startTimerButton;
    private Button completeWorkoutButton; // Reference to the Complete Workout button

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private long initialTimeInMillis;
    private boolean timerRunning;

    private Workout currentWorkout; // To hold the details of the current workout

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        workoutDetailImage = findViewById(R.id.workoutDetailImage);
        workoutDetailName = findViewById(R.id.workoutDetailName);
        workoutDetailDescription = findViewById(R.id.workoutDetailDescription);
        workoutDetailSetsRepsDuration = findViewById(R.id.workoutDetailSetsRepsDuration);
        workoutDetailTarget = findViewById(R.id.workoutDetailTarget);
        workoutDetailInstructions = findViewById(R.id.workoutDetailInstructions);
        timerTextView = findViewById(R.id.timerTextView);
        startTimerButton = findViewById(R.id.startTimerButton);
        completeWorkoutButton = findViewById(R.id.completeWorkoutButton); // Initialize the button

        // Get data from Intent and populate UI
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("workoutName");
            int imageResource = extras.getInt("workoutImage");
            String description = extras.getString("workoutDescription");
            String sets = extras.getString("workoutSets");
            String reps = extras.getString("workoutReps");
            String duration = extras.getString("workoutDuration");
            String target = extras.getString("workoutTarget");
            List<String> instructions = extras.getStringArrayList("workoutInstructions");

            // Create a Workout object to store the current workout details
            currentWorkout = new Workout(name, imageResource, description, sets, reps, duration, target, instructions);

            workoutDetailName.setText(name);
            workoutDetailDescription.setText(description);
            workoutDetailSetsRepsDuration.setText("Sets: " + sets + ", Reps: " + reps + ", Duration: " + duration);
            workoutDetailTarget.setText("Target: " + target);

            StringBuilder instructionsText = new StringBuilder();
            if (instructions != null && !instructions.isEmpty()) {
                instructionsText.append("Instructions:\n");
                for (String instruction : instructions) {
                    instructionsText.append(instruction).append("\n");
                }
            } else {
                instructionsText.append("Instructions: No instructions available.");
            }
            workoutDetailInstructions.setText(instructionsText.toString());

            Glide.with(this)
                    .asGif()
                    .load(imageResource)
                    .into(workoutDetailImage);

            initialTimeInMillis = parseDurationToMillis(duration);
            timeLeftInMillis =initialTimeInMillis;
            updateCountDownText();
        }

        startTimerButton.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        // Set click listener for the Complete Workout button
        completeWorkoutButton.setOnClickListener(v -> {
            saveWorkoutSession();
        });
    }

    private long parseDurationToMillis(String duration) {
        // This is a simple parsing logic, you might need to adjust based on your duration format
        if (duration != null && duration.contains("seconds")) {
            try {
                String[] parts = duration.split(" ");
                return Long.parseLong(parts[0]) * 1000;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0; // Default to 0 if parsing fails
            }
        } else if (duration != null && duration.contains("minutes")) { // Added parsing for minutes
            try {
                String[] parts = duration.split(" ");
                return Long.parseLong(parts[0]) * 60 * 1000;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0; // Default to 0 if parsing fails
            }
        }
        return 0; // Default to 0 if format is unexpected
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                startTimerButton.setText("Start");
                timeLeftInMillis = initialTimeInMillis; // Reset for next start
                updateCountDownText();
                // Optionally, you could automatically save the workout here:
                // saveWorkoutSession();
            }
        }.start();

        timerRunning = true;
        startTimerButton.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startTimerButton.setText("Start");
    }private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void saveWorkoutSession() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentWorkout == null) {
            Toast.makeText(this, "No workout data available to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid(); // Get the user ID

        // --- Estimate Calories Burned (Placeholder - Implement your logic) ---
        // This is a crucial part. You need to replace this with a realistic calculation.
        int estimatedCaloriesBurned = estimateCalories(currentWorkout);
        // --- End of Calorie Estimation ---

        // Create a WorkoutSession object using the constructor with 6 arguments
        WorkoutSession newWorkoutSession = new WorkoutSession(
                userId, // User ID (String)
                System.currentTimeMillis(), // Timestamp of completion (long)
                currentWorkout.getName(), // Workout name (String)
                (int) (initialTimeInMillis / 60000), // Duration in minutes (int)
                estimatedCaloriesBurned, // Estimated calories burned (int)
                "Completed " + currentWorkout.getName() + " on " + new Date(System.currentTimeMillis()).toString() // Simple notes (String)
        );

        firestore.collection("users").document(userId)
                .collection("workouts")
                .add(newWorkoutSession)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Workout session added with ID: " + documentReference.getId());
                    Toast.makeText(this, "Workout saved to progress!", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate the user back or to the progress tracking screen
                    finish(); // Example: close this activity after saving
                })
                .addOnFailureListener(e -> {Log.w("Firestore", "Error adding workout session", e);
                    Toast.makeText(this, "Failed to save workout.", Toast.LENGTH_SHORT).show();
                });
    }

    // --- Placeholder for Calorie Estimation Logic ---
    private int estimateCalories(Workout workout) {
        // **IMPORTANT:** This is a highly simplified example.
        // Implement a more accurate calorie estimation based on workout type,
        // duration, intensity, and potentially user data (weight, age, etc.).
        // You might need to use metabolic equivalent (MET) values or other methods.

        int estimatedCalories = 0;
        long durationInMinutes = initialTimeInMillis / 60000; // Use initial duration for estimation

        // Basic estimation based on workout name or type
        switch (workout.getName()) {
            case "Plank":
                estimatedCalories = (int) (durationInMinutes * 8); // Example MET value for plank
                break;
            case "Crunches":
                estimatedCalories = (int) (durationInMinutes * 7); // Example MET value
                break;
            case "Tricep Dips(Bench Dips)":
                estimatedCalories = (int) (durationInMinutes * 6); // Example MET value
                break;
            case "Flutter Kicks":
                estimatedCalories = (int) (durationInMinutes * 9); // Example MET value
                break;
            case "Leg Raises":
                estimatedCalories = (int) (durationInMinutes * 8); // Example MET value
                break;
            default:
                // Default estimation for unknown workouts
                estimatedCalories = (int) (durationInMinutes * 5);
                break;
        }

        // Consider adding complexity based on sets/reps if applicable
        // For example, if reps are tracked, you could add a multiplier.

        return estimatedCalories;
    }
    // --- End of Calorie Estimation Placeholder ---


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}