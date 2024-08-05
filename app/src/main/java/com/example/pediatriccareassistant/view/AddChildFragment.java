package com.example.pediatriccareassistant.view;

import android.app.DatePickerDialog;
import android.os.Bundle;

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
import com.example.pediatriccareassistant.controller.firebasehandler.ChildHandler;
import com.example.pediatriccareassistant.databinding.FragmentAddChildBinding;
import com.example.pediatriccareassistant.model.Child;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddChildFragment extends Fragment {

    private EditText name_edit;
    private EditText birthday_edit;
    private EditText notes_edit;
    private Spinner gender_spinner;
    private Button save_child_button;
    private ImageButton birthday_picker;
    private TextView name_error;
    private TextView date_error;

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentAddChildBinding binding = FragmentAddChildBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        name_edit = root.findViewById(R.id.add_child_name_edit);
        birthday_edit = root.findViewById(R.id.add_child_birthday_edit);
        birthday_picker = root.findViewById(R.id.add_child_birthday_image_button);
        gender_spinner = root.findViewById(R.id.add_child_gender_spinner);
        notes_edit = root.findViewById(R.id.add_child_notes_edit);
        save_child_button = root.findViewById(R.id.add_child_save_button);

        /* @todo handle errors elsewhere */
        name_error = root.findViewById(R.id.add_child_name_error_text);
        name_error.setVisibility(View.GONE);
        date_error = root.findViewById(R.id.add_child_date_error_text);
        date_error.setVisibility(View.GONE);

        // @todo handle different date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        birthday_edit.setText(dateFormat.format(Calendar.getInstance().getTime()));

        birthday_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickBirthdayDate(v);
            }
        });

        save_child_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveChild();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        setGenderSpinner(root);
        return root;
    }

    private void setGenderSpinner(View root)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.birth_genders, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(adapter);
    }

    private void saveChild() throws ParseException {
        if (formContainsErrors()) return;

        String nameString = name_edit.getText().toString();
        String birthdayString = birthday_edit.getText().toString();
        String genderString = gender_spinner.getSelectedItem().toString();
        String notesString = notes_edit.getText().toString();

        Calendar calendar = Calendar.getInstance();

        // @todo handle date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar.setTime(dateFormat.parse(birthdayString));

        int day = Integer.parseInt(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        int month = Integer.parseInt(String.format("%02d", calendar.get(Calendar.MONTH) + 1));
        int year = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR))); // Year as string

        Child child = new Child(nameString, genderString, notesString, day, month, year);

        ChildHandler.getInstance().writeNewChild(AuthenticationController.getInstance().getUserUid(), child);

        // go back to the previous window
        ((AppCompatActivity) root.getContext()).getSupportFragmentManager().popBackStackImmediate();
    }


    private boolean formContainsErrors()
    {
        boolean hasErrors = false;

        String nameText = name_edit.getText().toString().trim();

        if (nameText.isEmpty()) {
            name_error.setText("Name cannot be empty.");
            name_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        } else {
            name_error.setVisibility(View.GONE);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);
        String dateString = birthday_edit.getText().toString();
        Date date = dateFormat.parse(dateString, new ParsePosition(0));

        if (date == null || !dateFormat.format(date).equals(dateString)) {
            date_error.setText("Date is not valid.");
            date_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else date_error.setVisibility(View.GONE);
        return hasErrors;
    }


    private void pickBirthdayDate(View view)
    {
        String today_month = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String today_day = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        String today_year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), (view1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month-1, dayOfMonth);
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

            birthday_edit.setText(formattedDate);
        }, Integer.parseInt(today_year), Integer.parseInt(today_month)-1, Integer.parseInt(today_day));

        dialog.show();
    }
}