package com.baloyi.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baloyi.smartpantry.R;
import com.baloyi.smartpantry.adapter.RecipeAdapter;
import com.baloyi.smartpantry.db.DatabaseHelper;
import com.baloyi.smartpantry.logic.RecipeMatcher;
import com.baloyi.smartpantry.model.PantryItem;
import com.baloyi.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        tvEmpty = view.findViewById(R.id.tvEmptyRecipes);

        RecyclerView rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tapping a recipe sends its id to the detail screen inside an Intent
        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        rvRecipes.setAdapter(adapter);
    }

    // Re-run the strict matching every time this screen is shown,
    // so it always reflects the current pantry
    @Override
    public void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Recipe> allRecipes = dbHelper.getAllRecipes();
        List<PantryItem> pantry = dbHelper.getAllPantryItems();

        List<Recipe> suggested = RecipeMatcher.getSuggestedRecipes(allRecipes, pantry);

        adapter.setRecipes(suggested);
        tvEmpty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }
}