package com.example.yoursmartgymbuddy;

import static com.example.yoursmartgymbuddy.R.id.nutritionCard;

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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer & NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Update nav header email
        View headerView = navigationView.getHeaderView(0);
        TextView headerEmail = headerView.findViewById(R.id.headerEmail);
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            headerEmail.setText(user.getEmail());
        }

        // ✅ Add clickable functionality to the TextViews inside the cards
        findViewById(nutritionCard).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, NutritionActivity.class)));

        findViewById(R.id.workoutCard).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutsActivity.class)));

        findViewById(R.id.progressCard).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProgressTrackingActivity.class)));

        findViewById(R.id.workoutPlanCard).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutPlansActivity.class))); // Reuse or create different activity if needed
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_nutrition) {
            startActivity(new Intent(this, NutritionActivity.class));
        } else if (id == R.id.nav_workout) {
            startActivity(new Intent(this, WorkoutPlansActivity.class));
        } else if (id == R.id.nav_progress) {
            startActivity(new Intent(this, ProgressTrackingActivity.class));
        } else if (id == R.id.nav_workouts) {  // New workouts section
            startActivity(new Intent(this, WorkoutsActivity.class)); // Adjust class as needed
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
