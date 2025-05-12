package com.example.yoursmartgymbuddy;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    private ImageView workoutDetailImage;
    private TextView workoutDetailName;
    private TextView workoutDetailDescription; // Added back the TextView for description
    private TextView workoutDetailSetsRepsDuration;
    private TextView workoutDetailTarget;
    private TextView workoutDetailInstructions;
    private TextView timerTextView;
    private Button startTimerButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private long initialTimeInMillis;
    private boolean timerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        workoutDetailImage = findViewById(R.id.workoutDetailImage);
        workoutDetailName = findViewById(R.id.workoutDetailName);
        workoutDetailDescription = findViewById(R.id.workoutDetailDescription); // Find the TextView in the layout
        workoutDetailSetsRepsDuration = findViewById(R.id.workoutDetailSetsRepsDuration);
        workoutDetailTarget = findViewById(R.id.workoutDetailTarget);
        workoutDetailInstructions = findViewById(R.id.workoutDetailInstructions);
        timerTextView = findViewById(R.id.timerTextView);
        startTimerButton = findViewById(R.id.startTimerButton);

        // Get data from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("workoutName");
            int imageResource = extras.getInt("workoutImage");
            String description = extras.getString("workoutDescription"); // Retrieve the description from Intent
            String sets = extras.getString("workoutSets");
            String reps = extras.getString("workoutReps");
            String duration = extras.getString("workoutDuration");
            String target = extras.getString("workoutTarget");
            List<String> instructions = extras.getStringArrayList("workoutInstructions"); // Retrieve as ArrayList

            workoutDetailName.setText(name);
            workoutDetailDescription.setText(description); // Set the description text
            workoutDetailSetsRepsDuration.setText("Sets: " + sets + ", Reps: " + reps + ", Duration: " + duration);
            workoutDetailTarget.setText("Target: " + target);

            // Format instructions as a list without the prepended number
            StringBuilder instructionsText = new StringBuilder();
            if (instructions != null && !instructions.isEmpty()) {
                instructionsText.append("Instructions:\n");
                for (int i = 0; i < instructions.size(); i++) {
                    // FIX: Removed (i + 1)).append(". ") to remove the prepended number
                    instructionsText.append(instructions.get(i)).append("\n");
                }
            } else {
                instructionsText.append("Instructions: No instructions available.");
            }
            workoutDetailInstructions.setText(instructionsText.toString());

            // Load GIF image using Glide
            Glide.with(this)
                    .asGif()
                    .load(imageResource)
                    .into(workoutDetailImage);

            // Parse duration to milliseconds for the timer and store initial
            initialTimeInMillis = parseDurationToMillis(duration);
            timeLeftInMillis = initialTimeInMillis; // Set initial time for the first start
            updateCountDownText();
        }

        startTimerButton.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
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
                timeLeftInMillis = initialTimeInMillis;
                updateCountDownText();
            }
        }.start();

        timerRunning = true;
        startTimerButton.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startTimerButton.setText("Start");
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}