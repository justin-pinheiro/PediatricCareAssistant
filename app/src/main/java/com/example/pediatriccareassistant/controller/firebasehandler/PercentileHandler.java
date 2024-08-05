package com.example.pediatriccareassistant.controller.firebasehandler;

import androidx.annotation.NonNull;

import com.example.pediatriccareassistant.model.PercentileMeasure;
import com.example.pediatriccareassistant.model.callback.DataCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PercentileHandler extends BaseHandler {

    private static final PercentileHandler instance = new PercentileHandler();

    private PercentileHandler() { }

    public static PercentileHandler getInstance() {
        return instance;
    }

    public void retrievePercentileMeasures(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback, String growthChartName, PercentileMeasure.ChartType type) {
        DatabaseReference database = getDatabaseReference("WHO_growth_charts/" + growthChartName);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                percentiles.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PercentileMeasure percentile = PercentileMeasure.fromSnapshot(dataSnapshot, type);
                    percentiles.add(percentile);
                }

                callback.onSuccess(percentiles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void retrieveBoysHeightPercentile(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback) {
        retrievePercentileMeasures(percentiles, callback, "lhfa_boys_p_exp", PercentileMeasure.ChartType.HEIGHT);
    }

    public void retrieveBoysWeightPercentile(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback) {
        retrievePercentileMeasures(percentiles, callback, "wfa_boys_p_exp", PercentileMeasure.ChartType.WEIGHT);
    }

    public void retrieveBoysWeightHeightPercentile(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback) {
        retrievePercentileMeasures(percentiles, callback, "wfh_boys_p_exp", PercentileMeasure.ChartType.HEIGHT_WEIGHT);
    }

    public void retrieveGirlsHeightPercentile(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback) {
        retrievePercentileMeasures(percentiles, callback, "lhfa_girls_p_exp", PercentileMeasure.ChartType.HEIGHT);
    }

    public void retrieveGirlsWeightPercentile(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback) {
        retrievePercentileMeasures(percentiles, callback, "wfa_girls_p_exp", PercentileMeasure.ChartType.WEIGHT);
    }

    public void retrieveGirlsWeightHeightPercentile(ArrayList<PercentileMeasure> percentiles, DataCallback<ArrayList<PercentileMeasure>> callback) {
        retrievePercentileMeasures(percentiles, callback, "wfh_girls_p_exp", PercentileMeasure.ChartType.HEIGHT_WEIGHT);
    }
}
