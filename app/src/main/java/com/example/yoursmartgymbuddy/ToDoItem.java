package com.example.yoursmartgymbuddy;

public class ToDoItem {

    private String itemText;  // Task description

    // Required empty constructor for Firestore
    public ToDoItem() {
    }

    // Constructor to create a new ToDoItem with the task description
    public ToDoItem(String itemText) {
        this.itemText = itemText;
    }

    // Getter for itemText (task)
    public String getItemText() {
        return itemText;
    }

    // Setter for itemText (task)
    public void setItemText(String itemText) {
        this.itemText = itemText;
    }
}
