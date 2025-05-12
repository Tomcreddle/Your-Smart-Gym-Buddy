package com.example.yoursmartgymbuddy; // << REPLACE WITH YOUR PACKAGE NAME

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private RecyclerView nutritionRecyclerView;
    private NutritionAdapter nutritionAdapter;
    private List<NutritionPlan> nutritionPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
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

        // Set user email in nav header
        View headerView = navigationView.getHeaderView(0);
        TextView headerEmail = headerView.findViewById(R.id.headerEmail);
        if (user != null) {
            headerEmail.setText(user.getEmail());
        }

        // RecyclerView setup
        nutritionRecyclerView = findViewById(R.id.nutritionRecyclerView);
        nutritionPlans = new ArrayList<>();
        // You will need to create a NutritionAdapter class that extends RecyclerView.Adapter
        // and a NutritionPlan class to hold your data.
        nutritionAdapter = new NutritionAdapter(nutritionPlans, this);
        nutritionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nutritionRecyclerView.setAdapter(nutritionAdapter);

        db = FirebaseFirestore.getInstance();

        // Call this method ONCE to add initial data to Firestore
        // UNCOMMENT the line below, run your app once to populate the database,
        // then COMMENT it out again.
        // addInitialNutritionPlans();

        // Fetch data from Firestore
        fetchNutritionPlans();

        // ✅ BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        // Ensure R.id.bottom_home is a valid menu item ID in your bottom_menu.xml
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

    // Method to fetch nutrition plans from Firestore
    private void fetchNutritionPlans() {
        db.collection("nutritionPlans")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    nutritionPlans.clear(); // Clear existing data before adding new
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Ensure these fields exist in your Firestore documents
                        String title = doc.getString("title");
                        String description = doc.getString("description");
                        String imageUrl = doc.getString("imageUrl");
                        // Ensure the NutritionPlan constructor matches these parameters
                        nutritionPlans.add(new NutritionPlan(title, description, imageUrl));
                    }
                    nutritionAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error fetching data", e); // Log the error for debugging
                    Toast.makeText(NutritionActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to add initial nutrition plans to Firestore
    private void addInitialNutritionPlans() {
        List<NutritionPlan> initialPlans = new ArrayList<>();
        initialPlans.add(new NutritionPlan(
                "Balanced Diet",
                "A balanced diet includes a variety of foods from all food groups in the right proportions. This plan focuses on whole grains, lean proteins, fruits, vegetables, and healthy fats. It provides the necessary nutrients for overall health and well-being.",
                "https://via.placeholder.com/400x200?text=Balanced+Diet" // Replace with a real image URL
        ));
        initialPlans.add(new NutritionPlan(
                "High-Protein Meal Plan",
                "This plan is designed for muscle growth and repair, featuring meals rich in lean protein sources like chicken breast, fish, eggs, and legumes. It's often combined with strength training for best results.",
                "https://via.placeholder.com/400x200?text=High-Protein+Plan" // Replace with a real image URL
        ));
        initialPlans.add(new NutritionPlan(
                "Weight Loss Plan",
                "A calorie-controlled plan that emphasizes nutrient-dense foods to help create a calorie deficit. It typically involves portion control and limiting processed foods, sugary drinks, and unhealthy fats.",
                "https://via.placeholder.com/400x200?text=Weight+Loss+Plan" // Replace with a real image URL
        ));
        // Add more initial nutrition plans here

        for (NutritionPlan plan : initialPlans) {
            Map<String, Object> nutritionPlanMap = new HashMap<>();
            nutritionPlanMap.put("title", plan.getTitle());
            nutritionPlanMap.put("description", plan.getDescription());
            nutritionPlanMap.put("imageUrl", plan.getImageUrl());

            db.collection("nutritionPlans")
                    .add(nutritionPlanMap)
                    .addOnSuccessListener(documentReference -> {
                        // Optionally, log success or show a toast
                        Log.d("Firestore", "Document added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        // Optionally, log error or show a toast
                        Log.w("Firestore", "Error adding document", e);
                        Toast.makeText(NutritionActivity.this, "Error adding initial data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_nutrition) {
            // Already here
        } else if (id == R.id.nav_workout) {
            startActivity(new Intent(this, WorkoutPlansActivity.class));
        } else if (id == R.id.nav_progress) {
            startActivity(new Intent(this, ProgressTrackingActivity.class));
        } else if (id == R.id.nav_workouts) {
            startActivity(new Intent(this, WorkoutsActivity.class));
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