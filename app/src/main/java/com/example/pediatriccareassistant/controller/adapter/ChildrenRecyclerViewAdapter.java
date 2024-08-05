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

import com.example.pediatriccareassistant.view.EditChildFragment;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Child;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of children in a RecyclerView.
 * Each item in the RecyclerView represents a child with a name, age, and number of measurements.
 */
public class ChildrenRecyclerViewAdapter extends RecyclerView.Adapter<ChildrenRecyclerViewAdapter.ChildViewHolder> {

    private Context context;
    private ArrayList<Child> children;

    /**
     * Constructs a new ChildrenRecyclerViewAdapter with the specified context and list of children.
     *
     * @param context The context for inflating layouts
     * @param children The list of children to be displayed
     */
    public ChildrenRecyclerViewAdapter(Context context, ArrayList<Child> children) {
        this.context = context;
        this.children = children;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_child_view, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = children.get(position);

        // Bind the child's name, age, and number of measurements to the views
        holder.name.setText(child.getName());
        holder.birthday.setText(child.getFormattedBirthday());
        holder.measurements.setText(String.valueOf(child.getMeasurements().size()) + " measurements");

        // Set click listener
        holder.button.setOnClickListener(v -> {
            EditChildFragment fragment = EditChildFragment.newInstance(child);

            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.main_fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    /**
     * ViewHolder for a child item. Contains references to the name, age, and measurements TextViews.
     */
    public static class ChildViewHolder extends RecyclerView.ViewHolder {

        ImageButton button;
        TextView name;
        TextView birthday;
        TextView measurements;

        /**
         * Constructs a new ChildViewHolder.
         *
         * @param itemView The view representing a child item
         */
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.child_name);
            birthday = itemView.findViewById(R.id.child_birthday);
            measurements = itemView.findViewById(R.id.child_measurements);
            button = itemView.findViewById(R.id.child_button);
        }
    }
}
