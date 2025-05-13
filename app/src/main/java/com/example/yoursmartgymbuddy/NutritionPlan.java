package com.example.yoursmartgymbuddy;

public class NutritionPlan {
    private String title;
    private String description;
    private String imageUrl;

    // Add a no-argument constructor
    public NutritionPlan() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Existing constructor
    public NutritionPlan(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    // You might want to add setters if you plan to modify NutritionPlan objects after fetching
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}