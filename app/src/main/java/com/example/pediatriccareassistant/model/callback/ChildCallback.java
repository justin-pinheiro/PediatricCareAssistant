package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Child;

public interface ChildCallback {
    void onChildRetrieved(Child child);
    void onChildNotFound();
}
