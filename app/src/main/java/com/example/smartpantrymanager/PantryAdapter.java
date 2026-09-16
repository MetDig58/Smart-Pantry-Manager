package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> pantryItems = new ArrayList<>();

    public void setPantryItems(List<PantryItem> newPantryItems) {
        pantryItems.clear();

        if (newPantryItems != null) {
            pantryItems.addAll(newPantryItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position
    ) {
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

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameText = itemView.findViewById(R.id.textPantryItemName);
            itemQuantityText = itemView.findViewById(R.id.textPantryItemQuantity);
            itemExpiryText = itemView.findViewById(R.id.textPantryItemExpiry);
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
        }
    }
}