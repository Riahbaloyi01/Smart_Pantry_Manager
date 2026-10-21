package com.baloyi.smartpantry.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.baloyi.smartpantry.R;
import com.baloyi.smartpantry.db.DatabaseHelper;
import com.baloyi.smartpantry.model.Recipe;
import com.baloyi.smartpantry.model.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    // Key used to receive the recipe id from the Intent
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Read the recipe id that the list screen put into the Intent
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);

        tvTitle.setText(recipe.getName());
        tvSteps.setText(recipe.getSteps());

        // Build the ingredient list, one line per ingredient
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            double q = ing.getQuantity();
            String qty = (q == Math.floor(q)) ? String.valueOf((long) q) : String.valueOf(q);
            String name = ing.getName().substring(0, 1).toUpperCase() + ing.getName().substring(1);
            sb.append("\u2022  ").append(name).append(" - ")
                    .append(qty).append(" ").append(ing.getUnit()).append("\n");
        }
        tvIngredients.setText(sb.toString().trim());
    }
}