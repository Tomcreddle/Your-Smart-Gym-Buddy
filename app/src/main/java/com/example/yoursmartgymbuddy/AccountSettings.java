package com.example.yoursmartgymbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettings extends AppCompatActivity {

    private TextView emailTextView;
    private Button updatePasswordBtn, logoutBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Get the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize the TextView for displaying the email
        emailTextView = findViewById(R.id.emailTextView);  // Corrected ID
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn);  // Corrected ID
        logoutBtn = findViewById(R.id.logoutBtn);  // Corrected ID

        // Check if user is logged in
        if (currentUser != null) {
            // Set the email of the logged-in user
            emailTextView.setText("Email: " + currentUser.getEmail());
        } else {
            // If the user is not logged in, show a message
            emailTextView.setText("Email: Guest");
        }

        // Set up logout button
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            Toast.makeText(AccountSettings.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AccountSettings.this, LoginActivity.class)); // Go to login screen
            finish(); // Close the current activity
        });

        // Set up update password button
        updatePasswordBtn.setOnClickListener(v -> {
            if (currentUser != null) {
                showUpdatePasswordDialog(currentUser); // Show the dialog to update password
            }
        });

        // BottomNavigationView setup (unchanged)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.bottom_account);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_account) {
                return true;
            } else if (itemId == R.id.bottom_Todo) {
                startActivity(new Intent(getApplicationContext(), ToDoList.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_ai) {
                startActivity(new Intent(getApplicationContext(), AI.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_maps) {
                startActivity(new Intent(getApplicationContext(), Maps.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });

    }

    // Show the update password dialog
    private void showUpdatePasswordDialog(FirebaseUser user) {
        // Create a LayoutInflater to inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_update_password, null);

        final EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPassword);  // Corrected ID
        final EditText newPasswordEditText = dialogView.findViewById(R.id.newPassword);  // Corrected ID

        // Create an AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Update Password")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String currentPassword = currentPasswordEditText.getText().toString().trim();
                    String newPassword = newPasswordEditText.getText().toString().trim();

                    if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
                        updatePassword(user, currentPassword, newPassword);
                    } else {
                        Toast.makeText(AccountSettings.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Update the password
    private void updatePassword(FirebaseUser user, String currentPassword, String newPassword) {
        mAuth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If re-authentication is successful, update the password
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(AccountSettings.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AccountSettings.this, "Password update failed: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(AccountSettings.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
