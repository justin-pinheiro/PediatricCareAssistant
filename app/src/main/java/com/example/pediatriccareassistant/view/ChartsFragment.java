package com.example.pediatriccareassistant.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.ChartDrawer;
import com.example.pediatriccareassistant.controller.TrackerFragmentController;
import com.example.pediatriccareassistant.controller.firebasehandler.PercentileHandler;
import com.example.pediatriccareassistant.databinding.FragmentChartsBinding;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.callback.DataCallback;
import com.example.pediatriccareassistant.model.Measurement;
import com.example.pediatriccareassistant.model.PercentileMeasure;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A fragment that displays charts based on selected data.
 */
public class ChartsFragment extends Fragment implements TrackerFragmentController.OnChildSelectedListener {

    private static final String ARG_CHILD = "child";
    private LineChart chart;
    private Spinner chartChoice;
    private View root;
    private ArrayList<PercentileMeasure> percentiles;
    private Child child;
    private ChartDrawer chartDrawer;
    private LinearLayout chartsLayout;
    private TextView noMeasurementsText;
    private TextView chartDescription;
    private FloatingActionButton shareButton;
    private ProgressBar progressBar;

    public static ChartsFragment newInstance(Child child) {
        ChartsFragment fragment = new ChartsFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentChartsBinding binding = FragmentChartsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        shareButton = root.findViewById(R.id.charts_share_button);
        chartsLayout = root.findViewById(R.id.charts_layout);
        noMeasurementsText = root.findViewById(R.id.charts_no_measurements);
        noMeasurementsText.setVisibility(View.GONE);
        chart = root.findViewById(R.id.charts_line_chart);
        chartChoice = root.findViewById(R.id.charts_choice_spinner);
        chartDescription = root.findViewById(R.id.charts_chart_description);
        progressBar = root.findViewById(R.id.charts_progress_bar);

        setChartSpinnerValues();
        onChartSelected();

        shareButton.setOnClickListener(v -> {
            shareChart();
        });

        percentiles = new ArrayList<>();
        chartDrawer = new ChartDrawer(root.getContext());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reloadChart();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadChart();
    }

    /**
     * Reloads the chart based on the current selection.
     */
    public void reloadChart()
    {
        if (child == null || child.getMeasurements().size() < 2)
        {
            if (noMeasurementsText != null && chartsLayout != null) {
                noMeasurementsText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                chartsLayout.setVisibility(View.GONE);
            }
        }
        else {
            if (noMeasurementsText != null && chartsLayout != null) {
                noMeasurementsText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                chartsLayout.setVisibility(View.VISIBLE);
            }

            if (isAdded()) {
                int position = chartChoice.getSelectedItemPosition();
                if (position != AdapterView.INVALID_POSITION) {
                    loadChart(position);
                }
            }
        }
    }

    private void loadChart(int position) {

        switch (position) {
            case 0:
                chartDescription.setText(R.string.WHO_weight_chart_description);
                PercentileHandler.getInstance().retrieveBoysWeightPercentile(percentiles, new DataCallback<ArrayList<PercentileMeasure>>() {
                    @Override
                    public void onSuccess(ArrayList<PercentileMeasure> data) {
                        progressBar.setVisibility(View.GONE);
                        if (child != null)
                            chartDrawer.updateChart(chart, percentiles, child, PercentileMeasure.ChartType.WEIGHT);
                    }
                    @Override public void onFailure(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                break;
            case 1:
                chartDescription.setText(R.string.WHO_height_chart_description);
                PercentileHandler.getInstance().retrieveBoysHeightPercentile(percentiles, new DataCallback<ArrayList<PercentileMeasure>>() {
                    @Override
                    public void onSuccess(ArrayList<PercentileMeasure> data) {
                        progressBar.setVisibility(View.GONE);
                        chartDrawer.updateChart(chart, percentiles, child, PercentileMeasure.ChartType.HEIGHT);
                    }
                    @Override public void onFailure(Exception e)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                break;
            case 2:
                chartDescription.setText(R.string.WHO_height_weight_chart_description);
                PercentileHandler.getInstance().retrieveBoysWeightHeightPercentile(percentiles, new DataCallback<ArrayList<PercentileMeasure>>() {
                    @Override
                    public void onSuccess(ArrayList<PercentileMeasure> data) {
                        progressBar.setVisibility(View.GONE);
                        chartDrawer.updateChart(chart, percentiles, child, PercentileMeasure.ChartType.HEIGHT_WEIGHT);
                    }
                    @Override public void onFailure(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }

    private void onChartSelected() {
        chartChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (child != null) {
                    reloadChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setChartSpinnerValues() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(),
                R.array.chart_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chartChoice.setAdapter(adapter);
    }

    @Override
    public void onChildSelected(Child child) {
        this.child = child;
        reloadChart();
    }

    private void shareChart() {
        Bitmap bitmap = chart.getChartBitmap();
        try {
            File file = new File(requireContext().getExternalCacheDir(), "chart.png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            Uri uri = FileProvider.getUriForFile(requireContext(), "com.example.pediatriccareassistant.fileprovider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Chart"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
