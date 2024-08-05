package com.example.pediatriccareassistant.controller.firebasehandler;

import androidx.annotation.NonNull;
import com.example.pediatriccareassistant.model.Child;
import com.example.pediatriccareassistant.model.callback.ChildCallback;
import com.example.pediatriccareassistant.model.callback.ChildrenCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChildHandler extends BaseHandler {

    private static final ChildHandler instance = new ChildHandler();

    private ChildHandler() { }

    public static ChildHandler getInstance() {
        return instance;
    }

    public void retrieveChildrenFromUser(String idUser, ChildrenCallback callback) {
        DatabaseReference database = getDatabaseReference("users/" + idUser + "/children");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Child> children = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Child child = Child.fromSnapshot(dataSnapshot);
                    children.add(child);
                }

                callback.onChildrenRetrieved(children);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onChildrenNotFound();
            }
        });
    }

    public void retrieveChildFromUser(String idUser, String childName, ChildCallback callback) {
        DatabaseReference database = getDatabaseReference("users/" + idUser + "/children");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Child child = Child.fromSnapshot(snapshot.child(childName));
                callback.onChildRetrieved(child);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onChildNotFound();
            }
        });
    }

    public void writeNewChild(String userId, Child child) {
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("children").child(currentTime).setValue(child);
    }

    public void editChild(String userId, Child child) {
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("children").child(child.getId()).setValue(child);
    }

    public void removeChild(String userId, Child child) {
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("children").child(child.getId()).setValue(null);
    }
}
