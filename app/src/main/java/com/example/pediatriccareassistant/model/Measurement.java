package com.example.pediatriccareassistant.model;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Measurement implements Serializable {
    String weight_unit, height_unit, description, id;
    float weight, height;
    int day_of_month, month, year;

    public Measurement() {};

    public Measurement(String weight_unit, String height_unit, String description, float weight, float height, int day_of_month, int month, int year)
    {
        this.weight_unit = weight_unit;
        this.height_unit = height_unit;
        this.description = description;
        this.weight = weight;
        this.height = height;
        this.day_of_month = day_of_month;
        this.month = month;
        this.year = year;
    }

    public static Measurement fromSnapshot(DataSnapshot measurementSnapshot)
    {
        Measurement measurement = new Measurement();

        measurement.weight_unit =  measurementSnapshot.child("weight_unit").getValue(String.class);
        measurement.height_unit =  measurementSnapshot.child("height_unit").getValue(String.class);
        measurement.description =  measurementSnapshot.child("description").getValue(String.class);
        measurement.id = measurementSnapshot.getKey();

        measurement.weight = measurementSnapshot.child("weight").getValue(Float.class);
        measurement.height = measurementSnapshot.child("height").getValue(Float.class);

        measurement.day_of_month = measurementSnapshot.child("day_of_month").getValue(Integer.class);
        measurement.month = measurementSnapshot.child("month").getValue(Integer.class);
        measurement.year = measurementSnapshot.child("year").getValue(Integer.class);

        return measurement;
    }

    public String getId() {
        return id;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public String getHeight_unit() {
        return height_unit;
    }

    public String getDescription() {
        return description;
    }

    public float getWeight() {
        return weight;
    }

    public float getHeight() {
        return height;
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

    public String getFormatedDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day_of_month);

        return new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
    }

    public String getFormatedWeight()
    {
        return String.valueOf(weight) + " " + weight_unit;
    }

    public String getFormatedHeight()
    {
        return String.valueOf(height) + " " + height_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    public void setHeight_unit(String height_unit) {
        this.height_unit = height_unit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
