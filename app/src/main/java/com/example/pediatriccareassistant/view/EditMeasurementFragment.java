package com.example.pediatriccareassistant.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.firebasehandler.MeasurementHandler;
import com.example.pediatriccareassistant.databinding.FragmentEditMeasurementBinding;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.Measurement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class EditMeasurementFragment extends Fragment
{
    private static final String ARG_CHILD = "child";
    private static final String ARG_MEASUREMENT = "measurement";
    private static final Logger log = LoggerFactory.getLogger(EditMeasurementFragment.class);

    private EditText weight_edit;
    private EditText height_edit;
    private EditText description_edit;

    private TextView page_title;
    private TextView date;
    private TextView delete_measurement;
    private TextView weight_error;
    private TextView height_error;
    private TextView date_error;

    private Spinner weight_unit_spinner;
    private Spinner height_unit_spinner;

    private Button save_measurement;

    private static Child child;
    private Measurement measurement;

    private View root;

    public static EditMeasurementFragment newInstance(Measurement measurement, Child child) {
        EditMeasurementFragment fragment = new EditMeasurementFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHILD, child);
        args.putSerializable(ARG_MEASUREMENT, measurement);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            child = (Child) getArguments().getSerializable(ARG_CHILD);
            measurement = (Measurement) getArguments().getSerializable(ARG_MEASUREMENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentEditMeasurementBinding binding = FragmentEditMeasurementBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        page_title = root.findViewById(R.id.edit_measurement_title);
        page_title.setText("Editing measurement from " + child.getName());

        weight_edit = root.findViewById(R.id.edit_measurement_weight_edit);
        weight_edit.setText(String.valueOf(measurement.getWeight()));
        height_edit = root.findViewById(R.id.edit_measurement_height_edit);
        height_edit.setText(String.valueOf(measurement.getHeight()));
        description_edit = root.findViewById(R.id.edit_measurement_description_edit);
        description_edit.setText(String.valueOf(measurement.getDescription()));
        date = root.findViewById(R.id.edit_measurement_date);
        // @todo handle different date formats
        date.setText(measurement.getFormatedDate());

        /* @todo handle errors elsewhere */
        weight_error = root.findViewById(R.id.edit_measurement_weight_error_text);
        weight_error.setVisibility(View.GONE);
        height_error = root.findViewById(R.id.edit_measurement_height_error_text);
        height_error.setVisibility(View.GONE);
        date_error = root.findViewById(R.id.edit_measurement_date_error_text);
        date_error.setVisibility(View.GONE);

        save_measurement = root.findViewById(R.id.edit_measurement_save_button);
        save_measurement.setOnClickListener(v -> {
            if (!formContainsErrors()) {
                try {
                    saveMeasurement();
                    // go back to the previous window
                    ((AppCompatActivity) root.getContext()).getSupportFragmentManager().popBackStackImmediate();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        delete_measurement = root.findViewById(R.id.edit_measurement_delete_text);
        delete_measurement.setOnClickListener(v -> {
            deleteMeasurement();
            // go back to the previous window
            ((AppCompatActivity) root.getContext()).getSupportFragmentManager().popBackStackImmediate();
        });

        setWeightUnitSpinner(root);
        setHeightUnitSpinner(root);

        return root;
    }

    private void deleteMeasurement()
    {
        MeasurementHandler.getInstance().removeMeasurement(AuthenticationController.getInstance().getUserUid(), child, measurement);
        Toast.makeText(root.getContext(), "Measurement deleted successfully!", Toast.LENGTH_SHORT).show();
    }

    private void saveMeasurement() throws ParseException
    {
        float weight = Float.parseFloat(weight_edit.getText().toString());
        float height = Float.parseFloat(height_edit.getText().toString());
        String descriptionString = description_edit.getText().toString();
        String weightUnitString = weight_unit_spinner.getSelectedItem().toString();
        String heightUnitString = height_unit_spinner.getSelectedItem().toString();

        System.out.println(weight);

        measurement.setHeight(height);
        measurement.setWeight(weight);
        measurement.setHeight_unit(heightUnitString);
        measurement.setWeight_unit(weightUnitString);
        measurement.setDescription(descriptionString);

        MeasurementHandler.getInstance().editMeasurement(AuthenticationController.getInstance().getUserUid(), child, measurement);
    }


    private boolean formContainsErrors() {
        boolean hasErrors = false;

        String weightText = weight_edit.getText().toString().trim();
        String heightText = height_edit.getText().toString().trim();

        if (weightText.isEmpty()) {
            weight_error.setText("Weight cannot be empty.");
            weight_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        } else {
            weight_error.setVisibility(View.GONE);
        }

        if (heightText.isEmpty()) {
            height_error.setText("Height cannot be empty.");
            height_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        } else {
            height_error.setVisibility(View.GONE);
        }

        return hasErrors;
    }


    private void setWeightUnitSpinner(View root)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.weight_units, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        weight_unit_spinner = root.findViewById(R.id.edit_measurement_weight_unit_spinner);
        weight_unit_spinner.setAdapter(adapter);

        int position = adapter.getPosition(measurement.getWeight_unit());
        weight_unit_spinner.post(() -> weight_unit_spinner.setSelection(Math.max(position, 0)));
    }

    private void setHeightUnitSpinner(View root)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.height_units, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        height_unit_spinner = root.findViewById(R.id.edit_measurement_height_unit_spinner);
        height_unit_spinner.setAdapter(adapter);

        int position = adapter.getPosition(measurement.getHeight_unit());
        height_unit_spinner.post(() -> height_unit_spinner.setSelection(Math.max(position, 0)));
    }
}