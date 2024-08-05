package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Reminder;

import java.util.ArrayList;

public interface RemindersCallback {
    void onRemindersRetrieved(ArrayList<Reminder> reminders);
    void onRemindersNotFound(Exception e);
}
