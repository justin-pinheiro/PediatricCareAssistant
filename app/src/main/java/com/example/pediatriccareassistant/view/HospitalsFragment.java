package com.example.pediatriccareassistant.view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.HospitalRetriever;
import com.example.pediatriccareassistant.controller.adapter.HospitalsRecyclerViewAdapter;
import com.example.pediatriccareassistant.databinding.FragmentHospitalsBinding;
import com.example.pediatriccareassistant.model.Hospital;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class HospitalsFragment extends Fragment
{
    private static final Logger log = LoggerFactory.getLogger(HospitalsFragment.class);

    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentHospitalsBinding binding = FragmentHospitalsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.hospitals_recycler_view);
        progressBar = root.findViewById(R.id.hospitals_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        displayHospitals(root.getContext());

        return root;
    }

    /**
     * Displays nearby hospitals in the RecyclerView.
     *
     * @param context The context in which the fragment is running.
     */
    private void displayHospitals(Context context)
    {
        HospitalRetriever hospitalRetriever = new HospitalRetriever(context, getActivity());
        hospitalRetriever.getNearbyHospitals(10, 10000, new HospitalRetriever.HospitalsCallback() {
            @Override
            public void onHospitalsRetrieved(ArrayList<Hospital> hospitals)
            {
                progressBar.setVisibility(View.GONE);
                onHospitalsRetrievedSuccessfully(context, hospitals);
            }

            @Override
            public void onFailure(Exception e)
            {
                progressBar.setVisibility(View.GONE);
                log.error("An error occured: ", e);
            }
        });
    }

    /**
     * Handles the successful retrieval of nearby hospitals.
     *
     * @param context The context in which the fragment is running.
     * @param hospitals The list of retrieved hospitals.
     */
    private void onHospitalsRetrievedSuccessfully(Context context, ArrayList<Hospital> hospitals)
    {
        HospitalsRecyclerViewAdapter hospitalsAdapter = new HospitalsRecyclerViewAdapter(context, hospitals);

        recyclerView.setAdapter(hospitalsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Notify the adapter that data has changed
        new Handler(Looper.getMainLooper()).post(() -> hospitalsAdapter.notifyDataSetChanged());
    }

}