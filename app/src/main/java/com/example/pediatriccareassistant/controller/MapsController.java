package com.example.pediatriccareassistant.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Hospital;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.libraries.places.api.net.SearchNearbyResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class MapsController {

    private final String apiKey;
    private final PlacesClient placesClient;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    public MapsController(Context context) {
        this.context = context;
        this.apiKey = context.getString(R.string.maps_key);
        Places.initialize(context, apiKey);
        this.placesClient = Places.createClient(context);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void getCurrentLocation(Activity activity, LocationCallback callback)
    {
        checkLocationPermission(activity);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocationFound(location);
                    } else {
                        callback.onLocationError(new NullPointerException());
                    }
                })
                .addOnFailureListener(callback::onLocationError);
    }

    public void performNearbySearch(double latitude, double longitude, List<String> includedTypes, double radius, int resultsNumber, PlacesCallback callback) {
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS);

        LatLng center = new LatLng(latitude, longitude);
        CircularBounds circle = CircularBounds.newInstance(center, radius);

        final SearchNearbyRequest searchNearbyRequest =
                SearchNearbyRequest.builder(circle, placeFields)
                        .setIncludedTypes(includedTypes)
                        .setMaxResultCount(resultsNumber)
                        .build();

        placesClient.searchNearby(searchNearbyRequest)
                .addOnSuccessListener(response -> handleSuccessResponse(response, callback))
                .addOnFailureListener(exception -> handleFailureResponse(exception, callback));
    }

    private void handleSuccessResponse(SearchNearbyResponse response, PlacesCallback callback) {
        List<Place> nearbyPlaces = response.getPlaces();

        if (nearbyPlaces.isEmpty()) {
            Log.d("MapsController", "No places found.");
        } else {
            for (Place place : nearbyPlaces) {
                Log.d("MapsController", "Place found: " + place.getName() + ", Address: " + place.getAddress());
            }
        }

        callback.onPlacesFound(nearbyPlaces);
    }

    private void handleFailureResponse(Exception exception, PlacesCallback callback) {
        Log.e("MapsController", "Error occurred: " + exception.getMessage(), exception);
        callback.onPlacesError(exception);
    }

    public void calculateDistanceAndTime(double originLat, double originLng, double destLat, double destLng, DistanceMatrixCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s,%s&destinations=%s,%s&key=%s",
                originLat, originLng, destLat, destLng, apiKey
        );

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("MapsController", "Distance Matrix API call failed: " + e.getMessage(), e);
                callback.onDistanceMatrixError(e);
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onDistanceMatrixError(new IOException("Unexpected code " + response));
                    return;
                }

                String responseData = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

                try {
                    JsonArray rows = jsonObject.getAsJsonArray("rows");
                    JsonObject elements = rows.get(0).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();

                    JsonObject distance = elements.getAsJsonObject("distance");
                    JsonObject duration = elements.getAsJsonObject("duration");

                    String distanceText = distance.get("text").getAsString();
                    String durationText = duration.get("text").getAsString();

                    callback.onDistanceMatrixSuccess(distanceText, durationText);
                } catch (Exception e) {
                    callback.onDistanceMatrixError(e);
                }
            }
        });
    }

    public void fetchPlacePhoto(Place place, PhotoCallback callback) {
        List<PhotoMetadata> photoMetadata = place.getPhotoMetadatas();

        if (photoMetadata != null && !photoMetadata.isEmpty()) {
            PhotoMetadata photo = photoMetadata.get(0);

            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photo)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                callback.onPhotoFetched(bitmap);
            }).addOnFailureListener((exception) -> {
                callback.onPhotoFetchError(exception);
            });
        } else {
            callback.onPhotoFetchError(new Exception("No photo metadata available"));
        }
    }

    public String getPlaceMapsLink(Place place) {
        String address = place.getAddress();
        if (address != null) {
            return "https://www.google.com/maps/place/?q=" + address;
        } else {
            return null;
        }
    }

    public interface DistanceMatrixCallback {
        void onDistanceMatrixSuccess(String distance, String duration);
        void onDistanceMatrixError(Exception exception);
    }

    public interface PlacesCallback {
        void onPlacesFound(List<Place> places);
        void onPlacesError(Exception exception);
    }

    public interface LocationCallback {
        void onLocationFound(Location location);
        void onLocationError(Exception exception);
    }

    public interface PhotoCallback {
        void onPhotoFetched(Bitmap bitmap);
        void onPhotoFetchError(Exception exception);
    }
}
