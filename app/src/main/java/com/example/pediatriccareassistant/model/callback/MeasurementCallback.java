package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Measurement;

public interface MeasurementCallback {
    void onMeasurementRetrieved(Measurement measurement);
    void onNoMeasurementFound(Exception e);
}
