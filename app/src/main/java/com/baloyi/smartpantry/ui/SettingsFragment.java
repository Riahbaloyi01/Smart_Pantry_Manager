package com.baloyi.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.baloyi.smartpantry.R;
import com.baloyi.smartpantry.util.AppSettings;

public class SettingsFragment extends Fragment {

    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs", "tbsp", "tsp"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchCompat switchExpiry = view.findViewById(R.id.switchExpiry);
        Spinner spDefaultUnit = view.findViewById(R.id.spDefaultUnit);

        // Show the saved values first
        switchExpiry.setChecked(AppSettings.isExpiryAlertsOn(requireContext()));

        spDefaultUnit.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, UNITS));
        String saved = AppSettings.getDefaultUnit(requireContext());
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(saved)) {
                spDefaultUnit.setSelection(i);
            }
        }

        // Save a change as soon as the user makes it
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                AppSettings.setExpiryAlertsOn(requireContext(), isChecked));

        spDefaultUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                AppSettings.setDefaultUnit(requireContext(), UNITS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}