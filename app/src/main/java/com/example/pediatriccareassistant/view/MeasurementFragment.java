package com.example.pediatriccareassistant.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.firebasehandler.ChildHandler;
import com.example.pediatriccareassistant.model.callback.ChildCallback;
import com.example.pediatriccareassistant.controller.TrackerFragmentController;
import com.example.pediatriccareassistant.controller.adapter.MeasurementsRecyclerViewAdapter;
import com.example.pediatriccareassistant.databinding.FragmentMeasurementBinding;
import com.example.pediatriccareassistant.model.Child;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MeasurementFragment extends Fragment implements TrackerFragmentController.OnChildSelectedListener
{
    LinearLayout table_layout;
    TextView no_measurements_text;
    FloatingActionButton add_measurement_button;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentMeasurementBinding binding = FragmentMeasurementBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        add_measurement_button = root.findViewById(R.id.measurements_add_measurement_button);
        table_layout = root.findViewById(R.id.measurements_table_layout);
        no_measurements_text = root.findViewById(R.id.measurements_no_measurements);
        no_measurements_text.setVisibility(View.GONE);

        no_measurements_text.setVisibility(View.VISIBLE);
        table_layout.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onChildSelected(Child child)
    {
        ChildHandler.getInstance().retrieveChildFromUser(AuthenticationController.getInstance().getUserUid(), child.getId(), new ChildCallback() {
            @Override
            public void onChildRetrieved(Child child)
            {
                if (child.getMeasurements().isEmpty())
                {
                    no_measurements_text.setVisibility(View.VISIBLE);
                    table_layout.setVisibility(View.GONE);
                }
                else {
                    no_measurements_text.setVisibility(View.GONE);
                    table_layout.setVisibility(View.VISIBLE);

                    MeasurementsRecyclerViewAdapter measurementAdapter = new MeasurementsRecyclerViewAdapter(root.getContext(), child);

                    RecyclerView recyclerView = root.findViewById(R.id.measurements_recycler_view);
                    recyclerView.setAdapter(measurementAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                }

                add_measurement_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        AddMeasurementFragment fragment = AddMeasurementFragment.newInstance(child);

                        FragmentManager fragmentManager = ((AppCompatActivity) root.getContext()).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onChildNotFound() {
                System.out.println("NO CHILD FOUND");
            }
        });
    }
}