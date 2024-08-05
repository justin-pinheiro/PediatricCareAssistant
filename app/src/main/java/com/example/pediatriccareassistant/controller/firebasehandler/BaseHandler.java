package com.example.pediatriccareassistant.controller.firebasehandler;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class BaseHandler {
    protected DatabaseReference getDatabaseReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }
}