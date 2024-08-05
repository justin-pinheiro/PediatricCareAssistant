package com.example.pediatriccareassistant.controller.firebasehandler;

import androidx.annotation.NonNull;
import com.example.pediatriccareassistant.model.callback.DataCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CategoryHandler extends BaseHandler {

    private static final CategoryHandler instance = new CategoryHandler();

    private CategoryHandler() { }

    public static CategoryHandler getInstance() {
        return instance;
    }

    public void retrieveCategoriesName(DataCallback<ArrayList<String>> callback) {
        DatabaseReference database = getDatabaseReference("categories");

        database.get().addOnCompleteListener(task -> {
            ArrayList<String> categories = new ArrayList<>();
            //@todo handle hardcoded string
            categories.add("All categories");

            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    categories.add(String.valueOf(categorySnapshot.child("Title").getValue()));
                }

                callback.onSuccess(categories);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
