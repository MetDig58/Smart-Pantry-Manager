package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemActionListener {
        void onDeleteClick(PantryItem pantryItem);
        void onEditClick(PantryItem pantryItem);
    }

    private final List<PantryItem> pantryItems = new ArrayList<>();
    private final OnPantryItemActionListener actionListener;

    public PantryAdapter(OnPantryItemActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setPantryItems(List<PantryItem> newPantryItems) {
        pantryItems.clear();

        if (newPantryItems != null) {
            pantryItems.addAll(newPantryItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view, actionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem pantryItem = pantryItems.get(position);
        holder.bind(pantryItem);
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemNameText;
        private final TextView itemQuantityText;
        private final TextView itemExpiryText;
        private final Button deleteButton;
        private final Button editButton;
        private final OnPantryItemActionListener actionListener;

        PantryViewHolder(@NonNull View itemView, OnPantryItemActionListener actionListener) {
            super(itemView);
            this.actionListener = actionListener;

            itemNameText = itemView.findViewById(R.id.textPantryItemName);
            itemQuantityText = itemView.findViewById(R.id.textPantryItemQuantity);
            itemExpiryText = itemView.findViewById(R.id.textPantryItemExpiry);
            deleteButton = itemView.findViewById(R.id.buttonDeletePantryItem);
            editButton = itemView.findViewById(R.id.buttonEditPantryItem);
        }

        void bind(PantryItem pantryItem) {
            itemNameText.setText(pantryItem.getName());

            String quantityText = String.format(
                    Locale.getDefault(),
                    "Quantity: %s %s",
                    pantryItem.getQuantity(),
                    pantryItem.getUnit()
            );
            itemQuantityText.setText(quantityText);

            String expiryDate = pantryItem.getExpiryDate();

            if (expiryDate == null || expiryDate.trim().isEmpty()) {
                itemExpiryText.setText(R.string.expiry_not_provided);
            } else {
                itemExpiryText.setText("Expiry: " + expiryDate);
            }
            deleteButton.setOnClickListener(
                    view -> actionListener.onDeleteClick(pantryItem)
            );
            editButton.setOnClickListener(
                    view -> actionListener.onEditClick(pantryItem)
            );
        }
    }
}