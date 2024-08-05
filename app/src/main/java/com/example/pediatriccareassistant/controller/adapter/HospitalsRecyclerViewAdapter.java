package com.example.pediatriccareassistant.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Hospital;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of hospitals in a RecyclerView.
 * Each item in the RecyclerView represents a hospital with its name, location, distance, travel time, image, and a button to view on a map.
 */
public class HospitalsRecyclerViewAdapter extends RecyclerView.Adapter<HospitalsRecyclerViewAdapter.HospitalViewHolder> {

    private Context context;
    private ArrayList<Hospital> hospitals;

    /**
     * Constructs a new HospitalsRecyclerViewAdapter with default settings.
     */
    public HospitalsRecyclerViewAdapter() {
        // Default constructor
    }

    /**
     * Constructs a new HospitalsRecyclerViewAdapter with the specified context and list of hospitals.
     *
     * @param context The context for inflating layouts and handling intents
     * @param hospitals The list of hospitals to be displayed
     */
    public HospitalsRecyclerViewAdapter(Context context, ArrayList<Hospital> hospitals) {
        this.context = context;
        this.hospitals = hospitals;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_hospital_view, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);

        // Bind the hospital's details to the views
        holder.name.setText(hospital.getName());
        holder.location.setText(hospital.getLocation());
        holder.distance.setText(String.valueOf(hospital.getDistance()));
        holder.travel_time.setText(String.valueOf(hospital.getTravel_time()));

        // Load the hospital image using Glide
        Glide.with(holder.image.getContext())
                .load(hospital.getPhoto())
                .into(holder.image);

        // Set click listener to open the hospital's location in a map
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Hospital clicked");
                if (hospital.getMapsLink() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hospital.getMapsLink()));
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    /**
     * ViewHolder for a hospital item. Contains references to the name, location, distance, travel time, image, and button views.
     */
    public static class HospitalViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView location;
        TextView distance;
        TextView travel_time;
        ImageView image;
        ImageButton button;

        /**
         * Constructs a new HospitalViewHolder.
         *
         * @param itemView The view representing a hospital item
         */
        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hospital_name);
            location = itemView.findViewById(R.id.hospital_location);
            distance = itemView.findViewById(R.id.hospital_distance);
            travel_time = itemView.findViewById(R.id.hospital_travel_time);
            image = itemView.findViewById(R.id.hospital_map_image);
            button = itemView.findViewById(R.id.hospital_button);
        }
    }
}