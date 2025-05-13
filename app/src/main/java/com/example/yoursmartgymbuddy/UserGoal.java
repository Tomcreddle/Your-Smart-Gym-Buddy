package com.example.yoursmartgymbuddy;

import com.google.firebase.firestore.DocumentId;

public class UserGoal {

    @DocumentId
    private String id;
    private String userId;
    private String type; // e.g., "Weight Loss", "Running Distance", "Weightlifting Max"
    private double targetValue;
    private double currentValue; // Update this as the user progresses
    private long startDate;
    private Long endDate; // Use Long for nullable

    // No-argument constructor required for Firestore
    public UserGoal() {
    }

    public UserGoal(String id, String userId, String type, double targetValue, double currentValue, long startDate, Long endDate) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    // Setters (optional)
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}