package com.example.pediatriccareassistant.model;

import android.graphics.Bitmap;

public class Hospital
{
    String name, location, distance, travel_time, mapsLink;
    Bitmap photo;

    public Hospital(String name, String location, String mapsLink, String distance_in_km, String travel_time_in_minutes, Bitmap photo) {
        this.name = name;
        this.location = location;
        this.mapsLink = mapsLink;
        this.distance = distance_in_km;
        this.travel_time = travel_time_in_minutes;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getMapsLink() {
        return mapsLink;
    }

    public String getDistance() {
        return distance;
    }

    public String getTravel_time() {
        return travel_time;
    }

    public Bitmap getPhoto() {
        return photo;
    }
}
