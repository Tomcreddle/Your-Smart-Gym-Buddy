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
import java.util.Locale; // Import Locale for formatting

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
    private boolean timerFinished = false; // New flag to track if the timer has finished

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

        // Initially disable the complete workout button
        completeWorkoutButton.setEnabled(false);
        completeWorkoutButton.setAlpha(0.5f); // Optionally reduce alpha to show it's disabled

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
            // Note: We are adding isStarted and isCompleted fields to the Workout data model
            // although for this activity, we are primarily using the timerFinished flag.
            // If you plan to extend this to track state across multiple exercises in a plan,
            // you would modify the Workout model and potentially manage state in a ViewModel.
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
            timeLeftInMillis = initialTimeInMillis;
            updateCountDownText();
        }

        startTimerButton.setOnClickListener(v -> {
            if (timerRunning) {
                // If timer is running, pause it
                pauseTimer();
            } else {
                // If timer is not running, start it
                startTimer();
            }
        });

        // Set click listener for the Complete Workout button
        completeWorkoutButton.setOnClickListener(v -> {
            if (timerFinished) {
                saveWorkoutSession();
            } else {
                Toast.makeText(this, "Please complete the exercise duration first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long parseDurationToMillis(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 0; // Return 0 if duration is null or empty
        }

        String lowerCaseDuration = duration.toLowerCase(Locale.getDefault());

        if (lowerCaseDuration.contains("seconds")) {
            try {
                String[] parts = lowerCaseDuration.split(" ");
                return Long.parseLong(parts[0]) * 1000;
            } catch (NumberFormatException e) {
                Log.e("WorkoutDetail", "Error parsing duration (seconds): " + duration, e);
                return 0;
            }
        } else if (lowerCaseDuration.contains("minutes")) {
            try {
                String[] parts = lowerCaseDuration.split(" ");
                return Long.parseLong(parts[0]) * 60 * 1000;
            } catch (NumberFormatException e) {
                Log.e("WorkoutDetail", "Error parsing duration (minutes): " + duration, e);
                return 0;
            }
        } else {
            // Handle cases where duration might be just a number (assuming seconds)
            try {
                return Long.parseLong(duration) * 1000;
            } catch (NumberFormatException e) {
                Log.e("WorkoutDetail", "Error parsing duration (assuming seconds): " + duration, e);
                return 0;
            }
        }
    }


    private void startTimer() {
        // Disable the start button while timer is running
        startTimerButton.setEnabled(false);
        startTimerButton.setAlpha(0.5f); // Optional: reduce alpha

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                timerFinished = true; // Set timer finished flag
                startTimerButton.setText("Start Again"); // Change text after finish
                startTimerButton.setEnabled(true); // Enable start button again
                startTimerButton.setAlpha(1.0f); // Restore alpha

                // Enable the complete workout button
                completeWorkoutButton.setEnabled(true);
                completeWorkoutButton.setAlpha(1.0f); // Restore alpha

                timeLeftInMillis = initialTimeInMillis; // Reset for next start
                updateCountDownText();

                Toast.makeText(WorkoutDetailActivity.this, "Exercise time finished!", Toast.LENGTH_SHORT).show();
                // Optionally, you could automatically save the workout here:
                // saveWorkoutSession();
            }
        }.start();

        timerRunning = true;
        startTimerButton.setText("Pause");
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
            startTimerButton.setText("Resume");
            // Re-enable the start button so they can resume
            startTimerButton.setEnabled(true);
            startTimerButton.setAlpha(1.0f); // Restore alpha
        }
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds); // Use Locale
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

        // --- Estimate Calories Burned (Improved Placeholder) ---
        int estimatedCaloriesBurned = estimateCalories(currentWorkout);
        // --- End of Calorie Estimation ---

        // Calculate the completed duration in seconds
        // Since the timer counts down from initialTimeInMillis to 0,
        // the completed duration is the initial duration.
        long completedDurationSeconds = initialTimeInMillis / 1000; // Convert initial duration from millis to seconds

        // Create a WorkoutSession object using the constructor with 6 arguments
        WorkoutSession newWorkoutSession = new WorkoutSession(
                userId, // User ID (String)
                System.currentTimeMillis(), // Timestamp of completion (long)
                currentWorkout.getName(), // Workout name (String)
                completedDurationSeconds, // Pass the completed duration in seconds (long)
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
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding workout session", e);
                    Toast.makeText(this, "Failed to save workout.", Toast.LENGTH_SHORT).show();
                });
    }

    // --- Improved Calorie Estimation Logic ---
    private int estimateCalories(Workout workout) {
        // IMPORTANT: This is still an estimation. For true accuracy,
        // you would need user weight and more precise MET values.
        // This implementation provides a basic, non-zero estimation.

        double estimatedCalories = 0;
        // Use initialTimeInMillis for calorie calculation, as this is the intended duration
        double durationInMinutes = initialTimeInMillis / 60000.0; // Use double for accurate division

        // Approximate MET (Metabolic Equivalent of Task) values
        // These are general estimations and can vary.
        double metValue;

        switch (workout.getName()) {
            case "Plank":
                metValue = 8.0; // Moderate effort
                break;
            case "Crunches":
                metValue = 7.0; // Moderate effort
                break;
            case "Tricep Dips(Bench Dips)":
                metValue = 6.0; // Moderate effort
                break;
            case "Flutter Kicks":
                metValue = 9.0; // Vigorous effort
                break;
            case "Leg Raises":
                metValue = 8.0; // Moderate effort
                break;
            default:
                // Default MET value for other exercises
                metValue = 5.0; // Light effort
                break;
        }

        // Basic calorie calculation formula (without considering user weight):
        // Calories Burned = Duration (in minutes) * METs * 3.5 / 5 (This is a simplified version for METs)
        // A more accurate formula includes weight:
        // Calories Burned = (Duration in minutes) * (MET * 3.5 * weight in kg) / 200

        // For now, we'll use a simplified approach based on duration and MET
        // To avoid getting 0, we ensure duration is treated as a double and the result is rounded
        estimatedCalories = durationInMinutes * metValue * 5; // A simple scaling factor

        // Ensure a minimum calorie burn for any completed workout
        if (estimatedCalories < 1) {
            estimatedCalories = 5; // Assign a small value if calculation is very low
        }


        return (int) Math.round(estimatedCalories); // Round to nearest integer
    }
    // --- End of Improved Calorie Estimation Logic ---


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}