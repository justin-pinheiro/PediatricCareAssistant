package com.example.pediatriccareassistant.controller;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PediatricChronicCare extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("articles_embeddings");
        database.keepSynced(true);
    }
}
