package com.iffecode.lifecycle_v6;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;


public class EditProfileFragment extends Fragment {


    private EditText nameInput;
    private EditText heightInput;
    private EditText weightInput;
    private Spinner genderSpinner;
    private Button dateButton;
    private Button updateButton;



    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Nullable //något kan vara null och måste inte ha ett värde
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile,
                container,
                false);

        nameInput = view.findViewById(R.id.nameInput);
        heightInput = view.findViewById(R.id.heightInput);
        weightInput = view.findViewById(R.id.weightInput);

        genderSpinner = view.findViewById(R.id.genderSpinner);
        dateButton = view.findViewById(R.id.dateButton);
        updateButton = view.findViewById(R.id.updateButton);


        String[] genders = {"Male", "Female", "Other"};

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1, genders
        );

        genderSpinner.setAdapter(genderAdapter);


        dateButton.setOnClickListener(v -> {
            Calendar calender = Calendar.getInstance();

            int year = calender.get(Calendar.YEAR);
            int month = calender.get(Calendar.MONTH);
            int day = calender.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedYear + "-" + String.format("%02d", selectedMonth
                        + 1) + "-" + String.format("%02d", selectedDay);

                        dateButton.setText(date);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });

        updateButton.setOnClickListener(v -> {

            String name = nameInput.getText().toString();
            String height = heightInput.getText().toString();
            String weight = weightInput.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();
            String dateOfBirth = dateButton.getText().toString();

            SharedPreferences preferences =
                    requireActivity().getSharedPreferences(
                            "profile",
                            0
                    );

            SharedPreferences.Editor editor = preferences.edit();

            if (!name.isEmpty()) {
                editor.putString("name", name);
            }

            if (!height.isEmpty()) {
                editor.putString("height", height);
            }

            if (!weight.isEmpty()) {
                editor.putString("weight", weight);
            }

            if (!gender.isEmpty()) {
                editor.putString("gender", gender);
            }

            if (!dateOfBirth.isEmpty() && !dateOfBirth.equals("Select Date")) {
                editor.putString("dateOfBirth", dateOfBirth);
            }

            editor.apply();


            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;

    }
}