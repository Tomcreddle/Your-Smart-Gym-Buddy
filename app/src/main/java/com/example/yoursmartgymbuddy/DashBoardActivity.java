package com.example.yoursmartgymbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DashBoardActivity extends AppCompatActivity {

    private Button workoutPlansButton, nutritionButton, progressTrackingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Buttons
        workoutPlansButton = findViewById(R.id.workoutPlansButton);
        nutritionButton = findViewById(R.id.nutritionButton);
        progressTrackingButton = findViewById(R.id.progressTrackingButton);

        // Click Listeners
        workoutPlansButton.setOnClickListener(v -> {
            startActivity(new Intent(DashBoardActivity.this, WorkoutPlansActivity.class));
        });

        nutritionButton.setOnClickListener(v -> {
            startActivity(new Intent(DashBoardActivity.this, NutritionActivity.class));
        });

        progressTrackingButton.setOnClickListener(v -> {
            startActivity(new Intent(DashBoardActivity.this, ProgressTrackingActivity.class));
        });
    }
}
