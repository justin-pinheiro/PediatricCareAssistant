package com.example.pediatriccareassistant.view;

import android.app.DatePickerDialog;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.firebasehandler.MeasurementHandler;
import com.example.pediatriccareassistant.databinding.FragmentAddMeasurementBinding;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.Measurement;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMeasurementFragment extends Fragment
{
    private static final String ARG_CHILD = "child";

    private EditText date_edit;
    private EditText weight_edit;
    private EditText height_edit;
    private EditText description_edit;

    private TextView page_title;
    private TextView date_error;
    private TextView weight_error;
    private TextView height_error;

    private Spinner weight_unit_spinner;
    private Spinner height_unit_spinner;

    private ImageButton date_picker_button;
    private Button save_measurement;

    private Child child;

    private View root;

    public static AddMeasurementFragment newInstance(Child child) {
        AddMeasurementFragment fragment = new AddMeasurementFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHILD, child);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            child = (Child) getArguments().getSerializable(ARG_CHILD);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentAddMeasurementBinding binding = FragmentAddMeasurementBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        page_title = root.findViewById(R.id.add_measurement_title);
        page_title.setText("Adding measurement for " + child.getName());

        weight_edit = root.findViewById(R.id.add_measurement_weight_edit);
        height_edit = root.findViewById(R.id.add_measurement_height_edit);
        description_edit = root.findViewById(R.id.add_measurement_description_edit);
        date_edit = root.findViewById(R.id.add_measurement_date_edit);

        // @todo handle different date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date_edit.setText(dateFormat.format(Calendar.getInstance().getTime()));

        /* @todo handle errors elsewhere */
        weight_error = root.findViewById(R.id.add_measurement_weight_error_text);
        weight_error.setVisibility(View.GONE);
        height_error = root.findViewById(R.id.add_measurement_height_error_text);
        height_error.setVisibility(View.GONE);
        date_error = root.findViewById(R.id.add_measurement_date_error_text);
        date_error.setVisibility(View.GONE);

        date_picker_button = root.findViewById(R.id.add_measurement_date_image_button);
        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickReminderDate(v);
            }
        });

        save_measurement = root.findViewById(R.id.add_measurement_save_button);
        save_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveMeasurement();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        setWeightUnitSpinner(root);
        setHeightUnitSpinner(root);

        return root;
    }

    private void saveMeasurement() throws ParseException
    {
        if (formContainsErrors()) return;

        String dateString = date_edit.getText().toString();
        float weight = Float.parseFloat(weight_edit.getText().toString());
        float height = Float.parseFloat(height_edit.getText().toString());
        String descriptionString = description_edit.getText().toString();
        String weightUnitString = weight_unit_spinner.getSelectedItem().toString();
        String heightUnitString = height_unit_spinner.getSelectedItem().toString();

        Calendar calendar = Calendar.getInstance();

        // @todo handle date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar.setTime(dateFormat.parse(dateString));

        int day = Integer.parseInt(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        int month = Integer.parseInt(String.format("%02d", calendar.get(Calendar.MONTH) + 1));
        int year = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR))); // Year as string

        Measurement measurement = new Measurement(weightUnitString, heightUnitString, descriptionString, weight, height, day, month, year);

        MeasurementHandler.getInstance().writeNewMeasurement(AuthenticationController.getInstance().getUserUid(), child.getId(), measurement);

        // go back to the previous window
        ((AppCompatActivity) root.getContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private boolean formContainsErrors()
    {
        boolean hasErrors = false;

        if (weight_edit.getText().toString().isEmpty()) {
            weight_error.setText("Weight can not be empty.");
            weight_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else weight_error.setVisibility(View.GONE);

        if (height_edit.getText().toString().isEmpty()) {
            height_error.setText("Height can not be empty.");
            height_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else height_error.setVisibility(View.GONE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);
        String dateString = date_edit.getText().toString();
        Date date = dateFormat.parse(dateString, new ParsePosition(0));

        if (date == null || !dateFormat.format(date).equals(dateString)) {
            date_error.setText("Date is not valid.");
            date_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else if (child.isDateBeforeBirthday(date)) {
            date_error.setText("Date is before birthday.");
            date_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else date_error.setVisibility(View.GONE);

        return hasErrors;
    }

    private void setWeightUnitSpinner(View root)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.weight_units, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        weight_unit_spinner = root.findViewById(R.id.add_measurement_weight_unit_spinner);
        weight_unit_spinner.setAdapter(adapter);
    }

    private void setHeightUnitSpinner(View root)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.height_units, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        height_unit_spinner = root.findViewById(R.id.add_measurement_height_unit_spinner);
        height_unit_spinner.setAdapter(adapter);
    }

    public void pickReminderDate(View view)
    {
        String today_month = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String today_day = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        String today_year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), (view1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month-1, dayOfMonth);
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

            date_edit.setText(formattedDate);
        }, Integer.parseInt(today_year), Integer.parseInt(today_month)-1, Integer.parseInt(today_day));

        dialog.show();
    }

}