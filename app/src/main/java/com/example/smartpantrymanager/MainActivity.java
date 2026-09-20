package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration pantryListener;

    private PantryAdapter pantryAdapter;
    private RecyclerView pantryRecyclerView;
    private TextView emptyPantryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        emptyPantryText = findViewById(R.id.textEmptyPantry);

        pantryAdapter = new PantryAdapter(new PantryAdapter.OnPantryItemActionListener() {
            @Override
            public void onDeleteClick(PantryItem pantryItem) {
                deletePantryItem(pantryItem);
            }

            @Override
            public void onEditClick(PantryItem pantryItem) {
                showEditMessage(pantryItem);
            }
        });

        pantryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pantryRecyclerView.setAdapter(pantryAdapter);

        Button addIngredientButton = findViewById(R.id.buttonAddIngredient);

        addIngredientButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        firestore = FirebaseFirestore.getInstance();
        listenForPantryItems();
    }

    private void listenForPantryItems() {
        pantryListener = firestore.collection("pantryItems")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(
                                this,
                                R.string.pantry_load_failed,
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    List<PantryItem> pantryItems = new ArrayList<>();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : snapshot.getDocuments()) {

                        PantryItem pantryItem = document.toObject(PantryItem.class);

                        if (pantryItem != null) {
                            pantryItem.setId(document.getId());
                            pantryItems.add(pantryItem);
                        }
                    }

                    pantryAdapter.setPantryItems(pantryItems);
                    updateEmptyState(pantryItems);
                });
    }

    private void updateEmptyState(List<PantryItem> pantryItems) {
        boolean hasPantryItems = !pantryItems.isEmpty();

        if (hasPantryItems) {
            pantryRecyclerView.setVisibility(View.VISIBLE);
            emptyPantryText.setVisibility(View.GONE);
        } else {
            pantryRecyclerView.setVisibility(View.GONE);
            emptyPantryText.setVisibility(View.VISIBLE);
        }
    }

    private void deletePantryItem(PantryItem pantryItem) {
        if (pantryItem.getId() == null || pantryItem.getId().isEmpty())  {
            Toast.makeText(
                    this,
                    R.string.ingredient_delete_failed,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        firestore.collection("pantryItems")
                .document(pantryItem.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            R.string.ingredient_deleted,
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(
                            this,
                            R.string.ingredient_delete_failed,
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showEditMessage(PantryItem pantryItem) {
        Toast.makeText(
                this,
                "Edit selected for: " + pantryItem.getName(),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryListener != null) {
            pantryListener.remove();
        }
    }
}