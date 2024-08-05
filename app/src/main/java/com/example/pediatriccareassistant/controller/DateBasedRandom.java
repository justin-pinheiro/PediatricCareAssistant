package com.example.pediatriccareassistant.controller;

import java.util.Calendar;

public class DateBasedRandom {

    public static int getRandomNumberByDate(Calendar date, int min, int max)
    {
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        int hash = (day * 10000) + (month * 100) + year; // Simple hash based on date components
        hash = hash % (max - min + 1);

        return min + (hash >= 0 ? hash : -hash); // Ensure positive number within range
    }
}