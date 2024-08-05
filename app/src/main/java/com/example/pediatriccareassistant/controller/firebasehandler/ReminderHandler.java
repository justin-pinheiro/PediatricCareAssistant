package com.example.pediatriccareassistant.controller.firebasehandler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pediatriccareassistant.model.Reminder;
import com.example.pediatriccareassistant.model.callback.ReminderCallback;
import com.example.pediatriccareassistant.model.callback.RemindersCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReminderHandler extends BaseHandler {

    private static final ReminderHandler instance = new ReminderHandler();

    private ReminderHandler() { }

    public static ReminderHandler getInstance() {
        return instance;
    }

    public void retrieveReminders(String userId, RemindersCallback callback)
    {
        DatabaseReference database = getDatabaseReference("users/" + userId + "/reminders");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Reminder> reminders = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    reminders.add(Reminder.fromSnapshot(dataSnapshot));
                }

                callback.onRemindersRetrieved(reminders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onRemindersNotFound(error.toException());
            }
        });
    }

    public void retrieveNextReminderFromUserId(String userId, ReminderCallback callback) {
        DatabaseReference database = getDatabaseReference("users/" + userId + "/reminders");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Reminder> reminders = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    reminders.add(Reminder.fromSnapshot(dataSnapshot));
                }

                Reminder mostRecentReminder = Reminder.getMostRecentReminder(reminders);

                if (mostRecentReminder != null) {
                    callback.onReminderRetrieved(mostRecentReminder);
                } else {
                    callback.onReminderNotFound(new NullPointerException());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onReminderNotFound(error.toException());
            }
        });
    }

    public void writeNewReminder(String userId, Reminder reminder) {
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
        DatabaseReference database = getDatabaseReference("users");
        database.child(userId).child("reminders").child(currentTime).setValue(reminder);
    }

    public void editReminder(String userUid, Reminder reminder) {

        System.out.println(userUid);
        System.out.println(reminder.getId());

        DatabaseReference database = getDatabaseReference("users");
        database.child(userUid).child("reminders").child(reminder.getId()).setValue(reminder);
    }

    public void deleteReminder(String userUid, Reminder reminder) {
        DatabaseReference database = getDatabaseReference("users");
        database.child(userUid).child("reminders").child(reminder.getId()).setValue(null);
    }
}
