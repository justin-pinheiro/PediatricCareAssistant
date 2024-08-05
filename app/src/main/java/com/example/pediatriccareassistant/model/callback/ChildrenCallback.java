package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Child;

import java.util.ArrayList;

public interface ChildrenCallback {
    void onChildrenRetrieved(ArrayList<Child> children);
    void onChildrenNotFound();
}
