package com.example.yoursmartgymbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Create a Handler to delay the splash screen
        new Handler(getMainLooper()).postDelayed(() -> {
            // Check if the user is logged in
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Navigate to the appropriate activity
            if (currentUser != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            // Finish SplashActivity so the user can't go back to it
            finish();
        }, SPLASH_DURATION);
    }
}
