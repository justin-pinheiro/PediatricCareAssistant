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
import android.widget.Toast;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.firebasehandler.ChildHandler;
import com.example.pediatriccareassistant.databinding.FragmentEditChildBinding;
import com.example.pediatriccareassistant.model.Child;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditChildFragment extends Fragment {

    private static final String ARG_CHILD = "child";

    private EditText name_edit;
    private EditText birthday_edit;
    private EditText notes_edit;
    private Spinner gender_spinner;
    private Button save_child_button;
    private ImageButton birthday_picker;

    private TextView delete_child_textview;
    private TextView name_error;
    private TextView date_error;

    Child child;

    private View root;

    public static EditChildFragment newInstance(Child child) {
        EditChildFragment fragment = new EditChildFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentEditChildBinding binding = FragmentEditChildBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        name_edit = root.findViewById(R.id.edit_child_name_edit);
        name_edit.setText(child.getName());
        birthday_edit = root.findViewById(R.id.edit_child_birthday_edit);
        birthday_picker = root.findViewById(R.id.edit_child_birthday_image_button);
        gender_spinner = root.findViewById(R.id.edit_child_gender_spinner);
        notes_edit = root.findViewById(R.id.edit_child_notes_edit);
        notes_edit.setText(child.getNotes());
        save_child_button = root.findViewById(R.id.edit_child_save_button);


        /* @todo handle errors elsewhere */
        name_error = root.findViewById(R.id.edit_child_name_error_text);
        name_error.setVisibility(View.GONE);
        date_error = root.findViewById(R.id.edit_child_date_error_text);
        date_error.setVisibility(View.GONE);


        // @todo handle different date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar birthday = Calendar.getInstance();
        birthday.set(child.getBirth_year(), child.getBirth_month()-1, child.getBirth_day());
        birthday_edit.setText(dateFormat.format(birthday.getTime()));

        birthday_picker.setOnClickListener(v -> pickBirthdayDate(v));

        save_child_button.setOnClickListener(v -> {
            try {
                saveChild();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        delete_child_textview = root.findViewById(R.id.edit_child_delete_text);
        delete_child_textview.setOnClickListener(v -> {
            deleteChild();
            // go back to the previous window
            ((AppCompatActivity) root.getContext()).getSupportFragmentManager().popBackStackImmediate();
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

        int position = adapter.getPosition(child.getGender());
        gender_spinner.post(() -> gender_spinner.setSelection(Math.max(position, 0)));
    }

    private void deleteChild()
    {
        ChildHandler.getInstance().removeChild(AuthenticationController.getInstance().getUserUid(), child);
        Toast.makeText(root.getContext(), "Child deleted successfully!", Toast.LENGTH_SHORT).show();
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

        child.setName(nameString);
        child.setBirth_day(day);
        child.setBirth_month(month);
        child.setBirth_year(year);
        child.setGender(genderString);
        child.setNotes(notesString);

        ChildHandler.getInstance().editChild(AuthenticationController.getInstance().getUserUid(), child);

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