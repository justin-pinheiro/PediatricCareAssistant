package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Reminder;

public interface ReminderCallback {
    void onReminderRetrieved(Reminder reminder);
    void onReminderNotFound(Exception e);
}
