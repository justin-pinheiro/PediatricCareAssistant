package com.example.pediatriccareassistant.model;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Reminder implements Serializable {
    String id, title, description;
    int minute, hour, day_of_month, month, year, day_of_week;

    public Reminder() {}

    public static Reminder getMostRecentReminder(ArrayList<Reminder> reminders)
    {
        if (reminders.isEmpty()) {
            return null;
        }

        Reminder mostRecent = reminders.get(0);

        for (Reminder reminder : reminders) {
            if (reminder.compareTo(mostRecent) < 0) {
                mostRecent = reminder;
            }
        }

        return mostRecent;
    }

    public Reminder(String title, String description, int minute, int hour, int day_of_month, int month, int year) {
        this.title = title;
        this.description = description;
        this.minute = minute;
        this.hour = hour;
        this.day_of_month = day_of_month;
        this.month = month;
        this.year = year;
    }

    public static Reminder fromSnapshot(DataSnapshot snapshot)
    {
        Reminder reminder = new Reminder();

        reminder.id = snapshot.getKey();
        reminder.title = snapshot.child("title").getValue(String.class);
        reminder.description = snapshot.child("description").getValue(String.class);
        reminder.minute = snapshot.child("minute").getValue(Integer.class);
        reminder.hour = snapshot.child("hour").getValue(Integer.class);
        reminder.day_of_month = snapshot.child("day_of_month").getValue(Integer.class);
        reminder.month = snapshot.child("month").getValue(Integer.class);
        reminder.year = snapshot.child("year").getValue(Integer.class);

        return reminder;
    }

    // Method to compare reminders chronologically
    public int compareTo(Reminder other)
    {
        Calendar thisCalendar = createCalendarInstance(this);
        Calendar otherCalendar = createCalendarInstance(other);
        return thisCalendar.compareTo(otherCalendar);
    }

    // Helper method to create Calendar instance from Reminder fields
    private Calendar createCalendarInstance(Reminder reminder)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(reminder.getYear(), reminder.getMonth(), reminder.getDay_of_month(), reminder.getHour(), reminder.getMinute());
        calendar.set(Calendar.SECOND, 0); // Clear seconds to make sure exact minute/hour comparison

        return calendar;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setDay_of_month(int day_of_month) {
        this.day_of_month = day_of_month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getDay_of_month() {
        return day_of_month;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public String getId() {
        return id;
    }
}
