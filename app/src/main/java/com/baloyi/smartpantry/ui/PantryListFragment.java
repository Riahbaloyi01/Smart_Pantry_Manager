package com.baloyi.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baloyi.smartpantry.R;
import com.baloyi.smartpantry.adapter.PantryAdapter;
import com.baloyi.smartpantry.db.DatabaseHelper;
import com.baloyi.smartpantry.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PantryListFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        tvEmpty = view.findViewById(R.id.tvEmpty);

        RecyclerView rvPantry = view.findViewById(R.id.rvPantry);
        rvPantry.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PantryAdapter(new ArrayList<>(), new PantryAdapter.OnItemActionListener() {
            @Override
            public void onEdit(PantryItem item) {
                Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
                intent.putExtra(AddEditIngredientActivity.EXTRA_ID, item.getId());
                intent.putExtra(AddEditIngredientActivity.EXTRA_NAME, item.getName());
                intent.putExtra(AddEditIngredientActivity.EXTRA_QUANTITY, item.getQuantity());
                intent.putExtra(AddEditIngredientActivity.EXTRA_UNIT, item.getUnit());
                intent.putExtra(AddEditIngredientActivity.EXTRA_EXPIRY, item.getExpiryDate());
                startActivity(intent);
            }

            @Override
            public void onDelete(PantryItem item) {
                confirmDelete(item);
            }
        });
        rvPantry.setAdapter(adapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));
    }

    // Reload the list every time the screen comes back into view
    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.setItems(items);
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmDelete(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}