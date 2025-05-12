package com.example.yoursmartgymbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ToDoList extends AppCompatActivity {

    private List<String> toDoList;
    private ToDoAdapter toDoAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Initialize to-do list and RecyclerView
        toDoList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        // Set adapter with edit/delete support
        toDoAdapter = new ToDoAdapter(toDoList, new ToDoAdapter.OnItemInteractionListener() {
            @Override
            public void onItemEdit(int position) {
                showEditDeleteDialog(position);
            }

            @Override
            public void onItemDelete(int position) {
                toDoList.remove(position);
                toDoAdapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(toDoAdapter);

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.bottom_Todo);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_Todo) {
                return true;
            } else if (itemId == R.id.bottom_account) {
                startActivity(new Intent(getApplicationContext(), AccountSettings.class));
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

        // Add button to add a new to-do item
        Button addButton = findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(v -> showAddToDoDialog());
    }

    private void showAddToDoDialog() {
        final EditText input = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New To-Do Item")
                .setMessage("Enter your to-do item:")
                .setView(input)
                .setPositiveButton("Add", (dialog1, which) -> {
                    String newToDo = input.getText().toString().trim();
                    if (!newToDo.isEmpty()) {
                        toDoList.add(newToDo);
                        toDoAdapter.notifyItemInserted(toDoList.size() - 1);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showEditDeleteDialog(int position) {
        String currentText = toDoList.get(position);
        final EditText input = new EditText(this);
        input.setText(currentText);

        new AlertDialog.Builder(this)
                .setTitle("Edit or Delete")
                .setMessage("Modify your task or delete it:")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedText = input.getText().toString().trim();
                    if (!updatedText.isEmpty()) {
                        toDoList.set(position, updatedText);
                        toDoAdapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    toDoList.remove(position);
                    toDoAdapter.notifyItemRemoved(position);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }
}
