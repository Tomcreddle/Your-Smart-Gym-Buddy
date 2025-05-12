package com.example.yoursmartgymbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView workoutsRecyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Toolbar
        toolbar = findViewById(R.id.workoutToolbar);
        setSupportActionBar(toolbar);

        // Drawer & NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Set user email in header
        View headerView = navigationView.getHeaderView(0);
        TextView headerEmail = headerView.findViewById(R.id.headerEmail);
        if (user != null) {
            headerEmail.setText(user.getEmail());
        }

        // Setup RecyclerView
        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        workoutList = new ArrayList<>();
        workoutList.add(new Workout(
                "Plank",
                R.drawable.exersice_1, // Assuming exersice_1 is the correct drawable for Plank
                "A core strengthening exercise that engages the abdominal muscles, obliques, and lower back. Improves stability and posture.",
                "3",
                "N/A", // Plank is typically held for time, not reps
                "60 seconds",
                "Core",
                Arrays.asList(
                        "Step 1: Start in a push-up position on your forearms and toes.",
                        "Step 2: Ensure your body forms a straight line from head to heels.",
                        "Step 3: Engage your core and glutes, avoiding sagging hips or arching your back.",
                        "Step 4: Hold the position for the specified duration."
                )
        ));
        workoutList.add(new Workout(
                "Crunches",
                R.drawable.exersice_2, // Assuming exersice_2 is the correct drawable for Crunches
                "A classic abdominal exercise primarily targeting the rectus abdominis (abs).",
                "4",
                "20",
                "45 seconds",
                "Abdominals",
                Arrays.asList(
                        "Step 1: Lie on your back with knees bent and feet flat on the floor.",
                        "Step 2: Place your hands behind your head or across your chest.",
                        "Step 3: Engage your abdominal muscles and lift your upper body towards your knees, keeping your lower back on the floor.",
                        "Step 4: Lower back down slowly and with control."
                )
        ));
        workoutList.add(new Workout(
                "Tricep Dips(Bench Dips)",
                R.drawable.exersice_3, // Assuming exersice_3 is the correct drawable for Tricep Dips
                "An effective exercise for targeting the triceps and chest, using your body weight and a bench or elevated surface.",
                "3",
                "15",
                "60 seconds",
                "Triceps, Chest, Shoulders",
                Arrays.asList(
                        "Step 1: Sit on the edge of a bench or sturdy chair with your hands gripping the edge, fingers pointing forward.",
                        "Step 2: Slide your hips off the bench, supporting your weight with your hands.",
                        "Step 3: Lower your body by bending your elbows until they are close to 90 degrees.",
                        "Step 4: Push back up to the starting position, extending your arms."
                )
        ));
        workoutList.add(new Workout(
                "Flutter Kicks",
                R.drawable.exersice_4, // Assuming exersice_4 is the correct drawable for Flutter Kicks
                "A great exercise for the lower abdominal muscles and hip flexors.",
                "3",
                "30 seconds", // Flutter kicks are typically done for time
                "60 seconds", // Rest duration
                "Lower Abdominals, Hip Flexors",
                Arrays.asList(
                        "Step 1: Lie on your back with your legs extended.",
                        "Step 2: Place your hands under your lower back for support (optional).",
                        "Step 3: Lift your legs slightly off the ground.",
                        "Step 4: Alternate rapidly kicking your legs up and down in a fluttering motion."
                )
        ));
        workoutList.add(new Workout(
                "Leg Raises",
                R.drawable.exersice_5, // Assuming exersice_5 is the correct drawable for Leg Raises
                "An exercise that targets the lower abdominal muscles.",
                "4",
                "20",
                "45 seconds",
                "Lower Abdominals",
                Arrays.asList(
                        "Step 1: Lie on your back with your legs extended and together.",
                        "Step 2: Keep your hands by your sides or under your lower back for support.",
                        "Step 3: Slowly raise your legs towards the ceiling, keeping them straight.",
                        "Step 4: Lower your legs back down slowly without letting them touch the floor."
                )
        ));

        workoutAdapter = new WorkoutAdapter(workoutList);
        workoutsRecyclerView.setAdapter(workoutAdapter);

        workoutAdapter.setOnItemClickListener(workout -> {
            // Start the WorkoutDetailActivity and pass workout data
            Intent intent = new Intent(WorkoutsActivity.this, WorkoutDetailActivity.class);
            intent.putExtra("workoutName", workout.getName());
            intent.putExtra("workoutImage", workout.getImageResource());
            intent.putExtra("workoutDescription", workout.getDescription());
            intent.putExtra("workoutSets", workout.getSets());
            intent.putExtra("workoutReps", workout.getReps());
            intent.putExtra("workoutDuration", workout.getDuration());
            intent.putExtra("workoutTarget", workout.getTargetMuscleGroup()); // Pass target

            // FIX: Convert the List from Arrays.asList to a proper ArrayList
            List<String> instructionsList = workout.getInstructions();
            if (instructionsList != null) {
                intent.putStringArrayListExtra("workoutInstructions", new ArrayList<>(instructionsList));
            } else {
                // Handle the case where instructions might be null
                intent.putStringArrayListExtra("workoutInstructions", new ArrayList<>()); // Pass an empty ArrayList
            }

            startActivity(intent);
        });

        // ✅ BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.bottom_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_Todo) {
                startActivity(new Intent(getApplicationContext(), ToDoList.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_maps) {
                startActivity(new Intent(getApplicationContext(), Maps.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_ai) {
                startActivity(new Intent(getApplicationContext(), AI.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_account) {
                startActivity(new Intent(getApplicationContext(), AccountSettings.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_workouts) {
            // Already here
        } else if (id == R.id.nav_nutrition) {
            startActivity(new Intent(this, NutritionActivity.class));
        } else if (id == R.id.nav_workout) {
            startActivity(new Intent(this, WorkoutPlansActivity.class));
        } else if (id == R.id.nav_progress) {
            startActivity(new Intent(this, ProgressTrackingActivity.class));
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}