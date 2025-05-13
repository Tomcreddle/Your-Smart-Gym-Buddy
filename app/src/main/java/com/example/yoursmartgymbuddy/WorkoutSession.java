package com.example.yoursmartgymbuddy;

import com.google.firebase.firestore.DocumentId;
import java.util.Date; // Import Date for potential future use or clarity
import java.util.Map; // Import Map if you plan to use the details field

public class WorkoutSession {

    @DocumentId
    private String id; // Firestore document ID
    private String userId;
    private long date; // Use a timestamp (milliseconds since epoch)
    private String workoutName; // Renamed from workoutType to match the data being saved
    private int durationMinutes;
    private int estimatedCaloriesBurned;
    private String notes; // Added field for notes
    private Map<String, Object> details; // Optional: Uncomment if you need to store type-specific details

    // No-argument constructor required for Firestore
    public WorkoutSession() {
    }

    // Updated constructor to match the data being saved from WorkoutDetailActivity
    // Added the notes field
    public WorkoutSession(String userId, long date, String workoutName, int durationMinutes, int estimatedCaloriesBurned, String notes) {
        this.userId = userId;
        this.date = date;
        this.workoutName = workoutName;
        this.durationMinutes = durationMinutes;
        this.estimatedCaloriesBurned = estimatedCaloriesBurned;
        this.notes = notes;
        // You might want to initialize the details map here if you're using it:
        // this.details = new HashMap<>();
    }

    // Constructor including details (if you are saving details)
    // Added the notes field
    public WorkoutSession(String userId, long date, String workoutName, int durationMinutes, int estimatedCaloriesBurned, String notes, Map<String, Object> details) {
        this.userId = userId;
        this.date = date;
        this.workoutName = workoutName;
        this.durationMinutes = durationMinutes;
        this.estimatedCaloriesBurned = estimatedCaloriesBurned;
        this.notes = notes;
        this.details = details;
    }


    // Getters (needed for Firestore to read the data)
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getDate() {
        return date;
    }

    // Renamed getter to match the field name
    public String getWorkoutName() {
        return workoutName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getEstimatedCaloriesBurned() {
        return estimatedCaloriesBurned;
    }

    // Added getter for notes
    public String getNotes() {
        return notes;
    }

    // Uncommented the getDetails method
    public Map<String, Object> getDetails() {
        return details;
    }


    // Setters (optional, often not needed if data is set in constructor)
    // You might not need setters if you only save the data once
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDate(long date) {
        this.date = date;
    }

    // Renamed setter to match the field name
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setEstimatedCaloriesBurned(int estimatedCaloriesBurned) {
        this.estimatedCaloriesBurned = estimatedCaloriesBurned;
    }

    // Added setter for notes
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Uncommented the setDetails method
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}