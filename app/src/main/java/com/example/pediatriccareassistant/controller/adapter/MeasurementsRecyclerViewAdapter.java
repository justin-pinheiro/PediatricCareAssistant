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
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.Measurement;
import com.example.pediatriccareassistant.view.AddMeasurementFragment;
import com.example.pediatriccareassistant.view.EditMeasurementFragment;
import com.example.pediatriccareassistant.view.ReadArticleFragment;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of measurements in a RecyclerView.
 * Each item in the RecyclerView represents a measurement entry with date, weight, and height.
 */
public class MeasurementsRecyclerViewAdapter extends RecyclerView.Adapter<MeasurementsRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private Child child;

    /**
     * Constructs a new MeasurementsRecyclerViewAdapter with default settings.
     */
    public MeasurementsRecyclerViewAdapter() {
        // Default constructor
    }

    /**
     * Constructs a new MeasurementsRecyclerViewAdapter with the specified context and list of measurements.
     *
     * @param context The context for inflating layouts
     * @param child The child whose measurements will be displayed
     */
    public MeasurementsRecyclerViewAdapter(Context context, Child child) {
        this.context = context;
        this.child = child;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_measurement_table_row_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Measurement measurement = child.getMeasurements().get(position);

        // Bind the measurement's date, weight, and height to the views
        holder.date.setText(measurement.getFormatedDate());
        holder.weight.setText(measurement.getFormatedWeight());
        holder.height.setText(measurement.getFormatedHeight());

        // Set click listener
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMeasurementFragment fragment = EditMeasurementFragment.newInstance(measurement, child);

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
        return child.getMeasurements().size();
    }

    /**
     * ViewHolder for a measurement item. Contains references to the date, weight, and height TextViews, as well as Image Button to click measurement.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView weight;
        TextView height;
        ImageButton button;

        /**
         * Constructs a new MyViewHolder.
         *
         * @param itemView The view representing a measurement item
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.measurement_date);
            weight = itemView.findViewById(R.id.measurement_weight);
            height = itemView.findViewById(R.id.measurement_height);
            button = itemView.findViewById(R.id.measurement_button);
        }
    }
}
