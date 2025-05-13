package com.example.yoursmartgymbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    // Define the name of the shared collection for nutrition plans
    private static final String SHARED_NUTRITION_COLLECTION = "sharedNutritionPlans";

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
        nutritionAdapter = new NutritionAdapter(nutritionPlans, this);
        nutritionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nutritionRecyclerView.setAdapter(nutritionAdapter);

        db = FirebaseFirestore.getInstance();

        // *** IMPORTANT ***
        // Call this method ONCE during development to add initial data to the shared collection.
        // After the data is added, COMMENT OUT this line to avoid adding duplicate data
        // every time the activity starts.
        // addInitialNutritionPlansToSharedCollection();

        // Fetch data from the shared Firestore collection
        fetchSharedNutritionPlans();

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

    // Method to fetch nutrition plans from the shared Firestore collection
    private void fetchSharedNutritionPlans() {
        Log.d("Firestore", "Attempting to fetch nutrition plans from shared collection: " + SHARED_NUTRITION_COLLECTION);

        // Query the shared nutrition plans collection
        db.collection(SHARED_NUTRITION_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    nutritionPlans.clear(); // Clear existing data before adding new
                    Log.d("FirestoreData", "Fetched " + queryDocumentSnapshots.size() + " documents from shared collection.");
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Use toObject() to convert the document directly to your NutritionPlan object
                        try {
                            NutritionPlan nutritionPlan = doc.toObject(NutritionPlan.class);
                            nutritionPlans.add(nutritionPlan);
                            Log.d("FirestoreData", "Added Shared Nutrition Plan: " + nutritionPlan.getTitle());
                        } catch (Exception e) {
                            Log.e("FirestoreData", "Error converting document to NutritionPlan", e);
                            // Handle potential errors during conversion
                        }
                    }
                    Log.d("FirestoreData", "Before notifyDataSetChanged, total items: " + nutritionPlans.size());
                    nutritionAdapter.notifyDataSetChanged();
                    Log.d("FirestoreData", "Adapter notified.");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error fetching data from shared collection", e); // Log the error for debugging
                    Toast.makeText(NutritionActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to add initial nutrition plans to the shared Firestore collection
    // *** IMPORTANT: Only call this method ONCE during development ***
    private void addInitialNutritionPlansToSharedCollection() {
        List<Map<String, Object>> initialPlans = new ArrayList<>();

        // Balanced Diet
        Map<String, Object> balancedDiet = new HashMap<>();
        balancedDiet.put("title", "Balanced Diet");
        balancedDiet.put("description", "A balanced diet includes a variety of foods from all food groups in the right proportions. This plan focuses on whole grains, lean proteins, fruits, vegetables, and healthy fats. It provides the necessary nutrients for overall health and well-being.");
        balancedDiet.put("imageUrl", "https://via.placeholder.com/400x200?text=Balanced+Diet"); // Replace with a real image URL
        initialPlans.add(balancedDiet);

        // High-Protein Meal Plan
        Map<String, Object> highProteinPlan = new HashMap<>();
        highProteinPlan.put("title", "High-Protein Meal Plan");
        highProteinPlan.put("description", "This plan is designed for muscle growth and repair, featuring meals rich in lean protein sources like chicken breast, fish, eggs, and legumes. It's often combined with strength training for best results.");
        highProteinPlan.put("imageUrl", "https://via.placeholder.com/400x200?text=High-Protein+Plan"); // Replace with a real image URL
        initialPlans.add(highProteinPlan);

        // Weight Loss Plan
        Map<String, Object> weightLossPlan = new HashMap<>();
        weightLossPlan.put("title", "Weight Loss Plan");
        weightLossPlan.put("description", "A calorie-controlled plan that emphasizes nutrient-dense foods to help create a calorie deficit. It typically involves portion control and limiting processed foods, sugary drinks, and unhealthy fats.");
        weightLossPlan.put("imageUrl", "https://via.placeholder.com/400x200?text=Weight+Loss+Plan"); // Replace with a real image URL
        initialPlans.add(weightLossPlan);

        // Add more initial nutrition plans here as needed

        for (Map<String, Object> planMap : initialPlans) {
            // Add data to the shared collection
            db.collection(SHARED_NUTRITION_COLLECTION)
                    .add(planMap)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Initial shared nutrition plan document added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error adding initial shared nutrition plan document", e);
                        // You might want to show a Toast or log the error more prominently during development
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