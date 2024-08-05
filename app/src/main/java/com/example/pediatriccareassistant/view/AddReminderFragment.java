package com.example.pediatriccareassistant.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.firebasehandler.ReminderHandler;
import com.example.pediatriccareassistant.model.Reminder;
import com.example.pediatriccareassistant.databinding.FragmentAddReminderBinding;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReminderFragment extends Fragment
{
    private EditText title_edit;
    private EditText description_edit;
    private EditText date_edit;
    private EditText time_edit;

    private TextView title_error;
    private TextView description_error;
    private TextView date_error;
    private TextView time_error;

    private ImageButton date_picker_button;
    private ImageButton time_picker_button;
    private Button save_reminder;

    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentAddReminderBinding binding = FragmentAddReminderBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        title_edit = root.findViewById(R.id.add_reminder_title_edit);
        description_edit = root.findViewById(R.id.add_reminder_description_edit);
        date_edit = root.findViewById(R.id.add_reminder_date_edit);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date_edit.setText(dateFormat.format(Calendar.getInstance().getTime()));
        time_edit = root.findViewById(R.id.add_reminder_time_edit);
        time_edit.setText("00:00");

        title_error = root.findViewById(R.id.add_reminder_title_error_text);
        title_error.setVisibility(View.GONE);
        description_error = root.findViewById(R.id.add_reminder_description_error_text);
        description_error.setVisibility(View.GONE);
        date_error = root.findViewById(R.id.add_reminder_date_error_text);
        date_error.setVisibility(View.GONE);
        time_error = root.findViewById(R.id.add_reminder_time_error_text);
        time_error.setVisibility(View.GONE);

        date_picker_button = root.findViewById(R.id.add_reminder_date_image_button);
        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickReminderDate(v);
            }
        });

        time_picker_button = root.findViewById(R.id.add_reminder_time_image_button);
        time_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickReminderTime(v);
            }
        });

        save_reminder = root.findViewById(R.id.create_reminder_save_reminder_button);
        save_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveReminder();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return root;
    }

    private void saveReminder() throws ParseException {
        if (!formContainsErrors())
        {
            String title = title_edit.getText().toString();
            String description = description_edit.getText().toString();
            String dateString = date_edit.getText().toString();
            String timeString = time_edit.getText().toString();

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            calendar.setTime(dateFormat.parse(dateString));
            String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)); // Format day with leading zero
            String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1); // Format month with leading zero
            String year = String.valueOf(calendar.get(Calendar.YEAR)); // Year as string

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            calendar.setTime(timeFormat.parse(timeString));
            String hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)); // Format hours with leading zero
            String minutes = String.format("%02d", calendar.get(Calendar.MINUTE)); // Format minutes with leading zero

            Reminder reminder = new Reminder(title, description, Integer.parseInt(minutes), Integer.parseInt(hours), Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year));

            ReminderHandler.getInstance().writeNewReminder(AuthenticationController.getInstance().getUserUid(), reminder);


            // go back to the previous window
            ((AppCompatActivity) root.getContext()).getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private boolean formContainsErrors()
    {
        boolean hasErrors = false;

        if (title_edit.getText().toString().isEmpty()) {
            title_error.setText("Title can not be empty.");
            title_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else title_error.setVisibility(View.GONE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);
        String dateString = date_edit.getText().toString();
        Date date = dateFormat.parse(dateString, new ParsePosition(0));

        if (date == null || !dateFormat.format(date).equals(dateString)) {
            date_error.setText("Date is not valid.");
            date_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else date_error.setVisibility(View.GONE);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeFormat.setLenient(false);
        String timeString = time_edit.getText().toString();
        Date time = timeFormat.parse(timeString, new ParsePosition(0));

        if (time == null || !dateFormat.format(date).equals(dateString)) {
            time_error.setText("Time is not valid.");
            time_error.setVisibility(View.VISIBLE);
            hasErrors = true;
        }
        else time_error.setVisibility(View.GONE);

        return hasErrors;
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

    public void pickReminderTime(View view)
    {
        String today_hours = new SimpleDateFormat("hh", Locale.getDefault()).format(new Date());
        String today_minutes = new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());

        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), (view1, hours, minutes) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            time_edit.setText(formattedTime);
        }, Integer.parseInt(today_hours), Integer.parseInt(today_minutes), true);

        timePickerDialog.show();
    }
}