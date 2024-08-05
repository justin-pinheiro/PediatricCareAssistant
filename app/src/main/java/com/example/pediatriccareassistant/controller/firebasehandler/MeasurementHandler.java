package com.example.pediatriccareassistant.controller.firebasehandler;

import androidx.annotation.NonNull;

import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.Measurement;
import com.example.pediatriccareassistant.model.callback.MeasurementCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MeasurementHandler extends BaseHandler {

    private static final MeasurementHandler instance = new MeasurementHandler();

    private MeasurementHandler() { }

    public static MeasurementHandler getInstance() {
        return instance;
    }

    public void retrieveAllMeasurementFromChildId(String userId, String childName, MeasurementCallback callback)
    {
        DatabaseReference database = getDatabaseReference("users/" + userId + "/children/" + childName + "/measurements");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onMeasurementRetrieved(snapshot.getValue(Measurement.class));
                } else {
                    callback.onNoMeasurementFound(new NullPointerException());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onNoMeasurementFound(error.toException());
            }
        });
    }

    public void writeNewMeasurement(String userId, String childName, Measurement measurement) {
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("children").child(childName).child("measurements").child(currentTime).setValue(measurement);
    }

    public void editMeasurement(String userId, Child child, Measurement measurement) {
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("children").child(child.getId()).child("measurements").child(measurement.getId()).setValue(measurement);
    }

    public void removeMeasurement(String userId, Child child, Measurement measurement) {
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("children").child(child.getId()).child("measurements").child(measurement.getId()).setValue(null);
    }
}
