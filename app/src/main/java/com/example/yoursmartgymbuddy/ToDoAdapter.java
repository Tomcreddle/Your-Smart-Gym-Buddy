package com.example.yoursmartgymbuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private List<String> toDoList;
    private OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onItemEdit(int position);
        void onItemDelete(int position);
    }

    public ToDoAdapter(List<String> toDoList, OnItemInteractionListener listener) {
        this.toDoList = toDoList;
        this.listener = listener;
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, int position) {
        String item = toDoList.get(position);
        holder.toDoText.setText(item);

        holder.editButton.setOnClickListener(v -> listener.onItemEdit(holder.getAdapterPosition()));
        holder.deleteButton.setOnClickListener(v -> listener.onItemDelete(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder {
        TextView toDoText;
        Button editButton, deleteButton;

        public ToDoViewHolder(View itemView) {
            super(itemView);
            toDoText = itemView.findViewById(R.id.textItem);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
