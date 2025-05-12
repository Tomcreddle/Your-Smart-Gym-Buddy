package com.example.yoursmartgymbuddy;

import java.util.List;

public class Workout {
    private String name;
    private int imageResource; // Or String for URL if loading from network
    private String description;
    private String sets;
    private String reps;
    private String duration;
    private String targetMuscleGroup;
    private List<String> instructions; // Changed to List<String>

    public Workout(String name, int imageResource, String description, String sets, String reps, String duration, String targetMuscleGroup, List<String> instructions) {
        this.name = name;
        this.imageResource = imageResource;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.targetMuscleGroup = targetMuscleGroup;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getDescription() {
        return description;
    }

    public String getSets() {
        return sets;
    }

    public String getReps() {
        return reps;
    }

    public String getDuration() {
        return duration;
    }

    public String getTargetMuscleGroup() {
        return targetMuscleGroup;
    }

    public List<String> getInstructions() { // Getter returns List<String>
        return instructions;
    }
}