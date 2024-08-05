package com.example.pediatriccareassistant.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pediatriccareassistant.model.Hospital;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * HospitalRetriever is responsible for retrieving nearby hospitals based on the user's current location.
 */
public class HospitalRetriever
{
    public static final String[] SEARCH_TYPES_STRINGS = {"hospital", "doctor"};

    private final MapsController mapsController;
    private ArrayList<Hospital> hospitals;

    private Context context;
    private Activity activity;

    /**
     * Constructs a new HospitalRetriever with the specified context and activity.
     *
     * @param context The application context.
     * @param activity The activity from which the retriever is being called.
     */
    public HospitalRetriever (Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;

        mapsController = new MapsController(context);
        hospitals = new ArrayList<>();
    }

    /**
     * Callback interface for retrieving hospitals.
     */
    public interface HospitalsCallback {
        void onHospitalsRetrieved(ArrayList<Hospital> hospitals);
        void onFailure(Exception e);
    }

    /**
     * Retrieves nearby hospitals and returns the result through the specified callback.
     *
     * @param callback The callback to return the result.
     */
    public void getNearbyHospitals(int number, int metersRadius, HospitalsCallback callback) {
        mapsController.getCurrentLocation(activity, new MapsController.LocationCallback() {
            @Override
            public void onLocationFound(Location location) {
                List<String> includedTypes = Arrays.asList(SEARCH_TYPES_STRINGS);
                mapsController.performNearbySearch(location.getLatitude(), location.getLongitude(), includedTypes, metersRadius, number, new MapsController.PlacesCallback() {
                    @Override
                    public void onPlacesFound(List<Place> places) {
                        processPlaces(places, location, callback);
                    }

                    @Override
                    public void onPlacesError(Exception exception) {
                        callback.onFailure(exception);
                    }
                });
            }

            @Override
            public void onLocationError(Exception exception) {
                callback.onFailure(exception);
            }
        });
    }


    /**
     * Processes the list of places retrieved from the nearby search.
     *
     * @param places The list of places retrieved.
     * @param location The current location of the user.
     * @param callback The callback to return the result.
     */
    private void processPlaces(List<Place> places, Location location, HospitalsCallback callback) {
        for (Place place : places) {
            mapsController.calculateDistanceAndTime(location.getLatitude(), location.getLongitude(), Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude, new MapsController.DistanceMatrixCallback() {
                @Override
                public void onDistanceMatrixSuccess(String distance, String duration) {
                    fetchPlacePhotoAndAddHospital(place, distance, duration, callback, places.size());
                }

                @Override
                public void onDistanceMatrixError(Exception exception) {
                    Log.e("MapsController", "Error occurred: " + exception.getMessage(), exception);
                    callback.onFailure(exception);
                }
            });
        }
    }

    /**
     * Fetches the photo of the place and adds the hospital to the list.
     *
     * @param place The place for which the photo is being fetched.
     * @param distance The distance to the place.
     * @param duration The duration to the place.
     * @param callback The callback to return the result.
     */
    private void fetchPlacePhotoAndAddHospital(Place place, String distance, String duration, HospitalsCallback callback, int placesCount)
    {
        mapsController.fetchPlacePhoto(place, new MapsController.PhotoCallback() {
            @Override
            public void onPhotoFetched(Bitmap bitmap) {
                Hospital hospital = new Hospital(place.getName(), place.getAddress(), mapsController.getPlaceMapsLink(place), distance, duration, bitmap);
                hospitals.add(hospital);

                // Check if all hospitals have been processed
                if (hospitals.size() == placesCount) {
                    callback.onHospitalsRetrieved(hospitals);
                }
            }

            @Override
            public void onPhotoFetchError(Exception exception) {
                Log.e("MapsController", "Error occurred: " + exception.getMessage(), exception);
                callback.onFailure(exception);
            }
        });
    }
}
