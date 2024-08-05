package com.example.pediatriccareassistant.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Reminder;
import com.example.pediatriccareassistant.view.EditMeasurementFragment;
import com.example.pediatriccareassistant.view.EditReminderFragment;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of reminders in a RecyclerView.
 */
public class RemindersRecyclerViewAdapter extends RecyclerView.Adapter<RemindersRecyclerViewAdapter.ReminderViewHolder> {

    private Context context;
    private ArrayList<Reminder> reminders;

    /**
     * Constructs a new RemindersRecyclerViewAdapter with default settings.
     */
    public RemindersRecyclerViewAdapter() {
        // Default constructor
    }

    /**
     * Constructs a new RemindersRecyclerViewAdapter with the specified context and list of reminders.
     *
     * @param context The context for inflating layouts
     * @param reminders The list of reminders to be displayed
     */
    public RemindersRecyclerViewAdapter(Context context, ArrayList<Reminder> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_reminder_view, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        holder.title.setText(reminder.getTitle());
        holder.description.setText(reminder.getDescription());
        holder.time.setText(reminder.getHour()+":"+reminder.getMinute());
        holder.date.setText(reminder.getDay_of_month()+"/"+reminder.getMonth()+"/"+reminder.getYear());

        // Set click listener
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditReminderFragment fragment = EditReminderFragment.newInstance(reminder);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.main_fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    /**
     * ViewHolder for a reminder item. Contains references to the title, description, time, and date TextViews.
     */
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        TextView time;
        TextView date;
        ImageButton button;

        /**
         * Constructs a new ReminderViewHolder.
         *
         * @param itemView The view representing a reminder item
         */
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reminder_title);
            description = itemView.findViewById(R.id.reminder_description);
            time = itemView.findViewById(R.id.reminder_time);
            date = itemView.findViewById(R.id.reminder_date);
            button = itemView.findViewById(R.id.reminder_button);
        }
    }
}