package com.baloyi.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.baloyi.smartpantry.R;
import com.baloyi.smartpantry.db.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    // Keys used to pass data between screens inside the Intent
    public static final String EXTRA_ID = "item_id";
    public static final String EXTRA_NAME = "item_name";
    public static final String EXTRA_QUANTITY = "item_quantity";
    public static final String EXTRA_UNIT = "item_unit";
    public static final String EXTRA_EXPIRY = "item_expiry";

    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs", "tbsp", "tsp"};

    private DatabaseHelper dbHelper;
    private TextInputLayout tilName, tilQuantity;
    private TextInputEditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private long itemId = -1; // -1 means we are adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tvTitle);
        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnClearExpiry = findViewById(R.id.btnClearExpiry);

        spUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, UNITS));

        // If the Intent carries an item id, we are editing: fill the form with its data
        itemId = getIntent().getLongExtra(EXTRA_ID, -1);
        if (itemId != -1) {
            tvTitle.setText("Edit ingredient");
            etName.setText(getIntent().getStringExtra(EXTRA_NAME));

            double q = getIntent().getDoubleExtra(EXTRA_QUANTITY, 0);
            etQuantity.setText(q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q));

            String unit = getIntent().getStringExtra(EXTRA_UNIT);
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(unit)) {
                    spUnit.setSelection(i);
                }
            }
            etExpiry.setText(getIntent().getStringExtra(EXTRA_EXPIRY));
        } else {
            tvTitle.setText("Add ingredient");
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearExpiry.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void saveItem() {
        // Clear any old error messages
        tilName.setError(null);
        tilQuantity.setError(null);

        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        boolean valid = true;

        // Validation 1: the name must not be empty
        if (name.isEmpty()) {
            tilName.setError("Please enter an ingredient name");
            valid = false;
        }

        // Validation 2: the quantity must be a number greater than zero
        double quantity = 0;
        if (qtyText.isEmpty()) {
            tilQuantity.setError("Please enter a quantity");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(qtyText);
                if (quantity <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        if (!valid) {
            return; // stop here, nothing is saved
        }

        String unit = spUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();

        if (itemId == -1) {
            dbHelper.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // go back to the list
    }
}