package com.example.yoursmartgymbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProgressTrackingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private LinearProgressIndicator goalProgressIndicator;
    private TextView goalProgressText;
    private TextView caloriesBurnedText;
    private RecyclerView workoutHistoryRecyclerView;
    private WorkoutHistoryAdapter workoutAdapter;
    private LineChart workoutChart;

    private List<WorkoutSession> workoutSessions = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracking);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance(); // Get Firestore instance

        // Toolbar
        toolbar = findViewById(R.id.workoutToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Progress Tracking");
        }

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

        // Initialize UI elements
        goalProgressIndicator = findViewById(R.id.goalProgressIndicator);
        goalProgressText = findViewById(R.id.goalProgressText);
        caloriesBurnedText = findViewById(R.id.caloriesBurnedText);
        workoutHistoryRecyclerView = findViewById(R.id.workoutHistoryRecyclerView);
        workoutChart = findViewById(R.id.workoutChart);

        // Setup RecyclerView
        workoutHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutAdapter = new WorkoutHistoryAdapter(workoutSessions);
        workoutHistoryRecyclerView.setAdapter(workoutAdapter);

        // Configure the chart
        configureChart();

        // Load workout data
        loadWorkoutData();
        // You might also load goals here if you implement goal tracking

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        // IMPORTANT: Make sure R.id.bottom_ai is the correct ID for the Progress Tracking menu item
        bottomNav.setSelectedItemId(R.id.bottom_ai);

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
                // Already in this activity
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

    private void configureChart() {
        workoutChart.getDescription().setEnabled(false); // Hide description
        workoutChart.setTouchEnabled(true); // Enable touch gestures
        workoutChart.setDragEnabled(true); // Enable dragging
        workoutChart.setScaleEnabled(true); // Enable scaling
        workoutChart.setPinchZoom(true); // Enable pinch zoom

        XAxis xAxis = workoutChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Minimum axis interval

        com.github.mikephil.charting.components.YAxis leftAxis = workoutChart.getAxisLeft();
        leftAxis.setDrawGridLines(true); // Show grid lines for calorie values
        leftAxis.setAxisMinimum(0f); // Start y-axis from 0

        workoutChart.getAxisRight().setEnabled(false); // Disable right axis

        workoutChart.getLegend().setEnabled(true); // Show legend
    }

    private void loadWorkoutData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid();

        firestore.collection("users").document(userId)
                .collection("workouts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        workoutSessions.clear();
                        int totalCaloriesThisWeek = 0;
                        List<Entry> chartEntries = new ArrayList<>();
                        List<String> chartLabels = new ArrayList<>();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        long startOfWeekTimestamp = calendar.getTimeInMillis();

                        // Aggregate data for charting (e.g., daily calorie burn)
                        Map<String, Integer> dailyCalories = new HashMap<>();
                        SimpleDateFormat dateFormatForChart = new SimpleDateFormat("MMM dd", Locale.getDefault());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WorkoutSession workoutSession = document.toObject(WorkoutSession.class);
                            workoutSessions.add(workoutSession);

                            if (workoutSession.getDate() >= startOfWeekTimestamp) {
                                totalCaloriesThisWeek += workoutSession.getEstimatedCaloriesBurned();
                            }

                            // Aggregate daily calories for the chart
                            String dateString = dateFormatForChart.format(new Date(workoutSession.getDate()));
                            dailyCalories.put(dateString, dailyCalories.getOrDefault(dateString, 0) + workoutSession.getEstimatedCaloriesBurned());
                        }

                        // Prepare data for the chart, sorted by date
                        List<Map.Entry<String, Integer>> sortedDailyCalories = new ArrayList<>(dailyCalories.entrySet());
                        Collections.sort(sortedDailyCalories, (entry1, entry2) -> {
                            try {
                                Date date1 = dateFormatForChart.parse(entry1.getKey());
                                Date date2 = dateFormatForChart.parse(entry2.getKey());
                                return date1.compareTo(date2);
                            } catch (ParseException e) {
                                return 0;
                            }
                        });

                        for (int i = 0; i < sortedDailyCalories.size(); i++) {
                            Map.Entry<String, Integer> entry = sortedDailyCalories.get(i);
                            chartEntries.add(new Entry(i, entry.getValue()));
                            chartLabels.add(entry.getKey());
                        }


                        workoutAdapter.notifyDataSetChanged();
                        updateProgressUI(totalCaloriesThisWeek);
                        updateChart(chartEntries, chartLabels);

                    } else {
                        Log.w("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateProgressUI(int totalCaloriesThisWeek) {
        caloriesBurnedText.setText("Total: " + totalCaloriesThisWeek + " kcal");

        // Example: Update a goal progress (replace with your actual goal logic)
        int calorieGoal = 2000; // Example weekly calorie goal
        int progressPercentage = 0;
        if (calorieGoal > 0) {
            progressPercentage = (int) Math.min(100, (double) totalCaloriesThisWeek / calorieGoal * 100);
        }
        goalProgressIndicator.setProgress(progressPercentage);
        goalProgressText.setText("Progress: " + progressPercentage + "%");

        // You would also update other goal-related UI elements here
    }

    private void updateChart(List<Entry> entries, List<String> labels) {
        if (entries.isEmpty()) {
            workoutChart.clear();
            workoutChart.setNoDataText("No workout data available for chart.");
            workoutChart.invalidate();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Calories Burned");
        dataSet.setColor(ContextCompat.getColor(this, R.color.purple_500)); // Use a color from your resources
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.purple_700));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        List<com.github.mikephil.charting.interfaces.datasets.ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);

        workoutChart.setData(lineData);
        workoutChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        workoutChart.getXAxis().setLabelCount(labels.size(), false); // Ensure all labels are shown
        workoutChart.invalidate(); // Refresh the chart
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_workouts) {
            startActivity(new Intent(this, WorkoutsActivity.class));
        } else if (itemId == R.id.nav_nutrition) {
            startActivity(new Intent(this, NutritionActivity.class));
        } else if (itemId == R.id.nav_workout) {
            startActivity(new Intent(this, WorkoutPlansActivity.class));
        } else if (itemId == R.id.nav_progress) {
            // Already in this activity
        } else if (itemId == R.id.nav_logout) {
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

    // Example functions for adding, updating, and deleting data (add these as needed)
    // You would typically call these from other activities or fragments where
    // the user performs actions like logging a workout or setting a goal.

    public void addWorkoutSession(WorkoutSession workoutSession) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid();

        // Use a HashMap to store the details for Firestore
        Map<String, Object> detailsMap = null;
        if (workoutSession.getDetails() != null) {
            detailsMap = new HashMap<>(workoutSession.getDetails());
        }


        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("userId", workoutSession.getUserId());
        workoutData.put("date", workoutSession.getDate());
        workoutData.put("workoutName", workoutSession.getWorkoutName());
        workoutData.put("durationMinutes", workoutSession.getDurationMinutes());
        workoutData.put("estimatedCaloriesBurned", workoutSession.getEstimatedCaloriesBurned());
        workoutData.put("details", detailsMap); // Store the details map


        firestore.collection("users").document(userId)
                .collection("workouts")
                .add(workoutData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Workout session added with ID: " + documentReference.getId());
                    // Optionally, refresh your data after adding
                    // loadWorkoutData(); // Uncomment if you want to refresh the view immediately
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding workout session", e);
                });
    }

    public void updateGoalProgress(String goalId, double newCurrentValue) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid();

        firestore.collection("users").document(userId)
                .collection("goals").document(goalId)
                .update("currentValue", newCurrentValue)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Goal progress updated successfully!");
                    // Optionally, refresh goals data if needed
                    // loadGoalsData(); // Assuming you have a method to load goals
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating goal progress", e);
                });
    }

    public void deleteWorkoutSession(String workoutId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid();

        firestore.collection("users").document(userId)
                .collection("workouts").document(workoutId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Workout session deleted successfully!");
                    // Optionally, refresh your data after deleting
                    loadWorkoutData();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting workout session", e);
                });
    }

    // Add other data manipulation methods as needed (e.g., updateWorkoutSession)
}