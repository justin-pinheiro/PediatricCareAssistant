package com.example.pediatriccareassistant.model;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Child implements Serializable {
    String id, name, gender, notes;
    int birth_day, birth_month, birth_year;
    ArrayList<Measurement> measurements;

    public Child() {}

    public Child(String name, String gender, String notes, int birth_day, int birth_month, int birth_year) {
        this.name = name;
        this.gender = gender;
        this.notes = notes;
        this.birth_day = birth_day;
        this.birth_month = birth_month;
        this.birth_year = birth_year;
    }

    public static Child fromSnapshot(DataSnapshot dataSnapshot)
    {
        Child child = new Child();

        child.id = dataSnapshot.getKey();
        child.name = dataSnapshot.child("name").getValue(String.class);

        System.out.println("id: " + child.id);
        System.out.println("name: " + child.name);

        child.gender = dataSnapshot.child("gender").getValue(String.class);
        child.notes = dataSnapshot.child("notes").getValue(String.class);

        child.birth_day = dataSnapshot.child("birth_day").getValue(Integer.class);
        child.birth_month = dataSnapshot.child("birth_month").getValue(Integer.class);
        child.birth_year = dataSnapshot.child("birth_year").getValue(Integer.class);

        child.measurements = new ArrayList<>();
        DataSnapshot measurementsSnapshot = dataSnapshot.child("measurements");
        for (DataSnapshot measurementSnapshot : measurementsSnapshot.getChildren())
        {
            Measurement measurement = Measurement.fromSnapshot(measurementSnapshot);

            child.measurements.add(measurement);
        }

        return child;
    }

    public boolean isDateBeforeBirthday(Date date)
    {
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.set(birth_year, birth_month - 1, birth_day, 0, 0, 0);
        birthdayCalendar.set(Calendar.MILLISECOND, 0);

        Date birthday = birthdayCalendar.getTime();

        return date.before(birthday);
    }

    public String getFormattedBirthday()
    {
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.set(birth_year, birth_month - 1, birth_day);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

        return sdf.format(birthdayCalendar.getTime());
    }


    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getNotes() {
        return notes;
    }

    public int getBirth_day() {
        return birth_day;
    }

    public int getBirth_month() {
        return birth_month;
    }

    public int getBirth_year() {
        return birth_year;
    }

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setBirth_day(int birth_day) {
        this.birth_day = birth_day;
    }

    public void setBirth_month(int birth_month) {
        this.birth_month = birth_month;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }
}
