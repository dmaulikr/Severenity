package com.nosad.sample.engine.managers.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.entity.Spell;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Created by Novosad on 3/29/16.
 */
public class LocationManager implements LocationListener {
    private Location previousLocation;
    private Location currentLocation;
    private Context context;
    private GoogleApiClient googleApiClient;

    private Marker currentUserMarker, otherUserMarker;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;

    public boolean requestingLocationUpdates = false;
    private boolean isSpellMode = false;
    private boolean isCameraFixed = false;
    private boolean isMoving = false;

    private long timeSinceLastUpdate = 0;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable stopMoving = new Runnable() {
        @Override
        public void run() {
            isMoving = false;
            updateTotalDistancePassed();
            Toast.makeText(context, "Stopped moving.", Toast.LENGTH_SHORT).show();
        }
    };

    public LocationManager(Context context) {
        this.context = context;
        this.googleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        createLocationRequest();
        timeSinceLastUpdate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    /**
     * Updates the active map with the map got from fragment
     *
     * @param map
     */
    public void updateMap(GoogleMap map) {
        if (map == null) {
            return;
        }

        googleMap = map;
        googleMap.setMyLocationEnabled(true);
        googleMap.setPadding(0, 200, 0, 0);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (App.getSpellManager().isSpellMode()) {
                    if (App.getSpellManager().getCurrentSpell().is(Spell.SpellType.Ward)) {
                        placeWard(latLng);
                    }

                    if (App.getSpellManager().getCurrentSpell().is(Spell.SpellType.PowerWave)) {
                        showExplosionAt(googleMap.getProjection().toScreenLocation(latLng));
                    }
                }
            }
        });

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                isCameraFixed = false;
                App.getSpellManager().cancelSpellMode();
                return true;
            }
        });
    }

    /**
     * Points camera to the specified location in the meanwhile stopping camera updates
     * to current user location.
     *
     * @param latLng
     */
    public void fixCameraAtLocation(LatLng latLng) {
        isCameraFixed = true;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
    }

    private Marker placeWard(LatLng latLng) {
        Marker wardMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_spell_ward))
                .title(App.getSpellManager().getCurrentSpell().getTitle()));

        App.getSpellManager().addWard(wardMarker);

        return wardMarker;
    }

    private void showExplosionAt(Point p) {
        Intent intent = new Intent("explosion");
        intent.putExtra("point", p);
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }

    public boolean isRequestingLocationUpdates() {
        return requestingLocationUpdates;
    }

    /**
     * Creates location request with interval 1-5 secs and high accuracy
     */
    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     *
     * @param user - user to display marker of
     * @param latLng - location to display user at
     */
    public void displayUserAt(User user, LatLng latLng) {
        if (otherUserMarker != null) {
            otherUserMarker.remove();
        }

        otherUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(user.getId()));
    }

    public void displayPlaceMarker(Place place) {
        for (Integer placeType : place.getPlaceTypes()) {
            Log.i(Constants.TAG, "Place type for " + place.getName() + " is: " + placeType);
        }

        if (place.getPlaceTypes().contains(Place.TYPE_BUS_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_TRANSIT_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_SUBWAY_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_STREET_ADDRESS)) {
            return;
        }

        googleMap.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(String.format("%s", place.getName())));
    }

    /**
     * Initializes location updates according to created location request and sets
     * flag about requesting location updates.
     */
    public void startLocationUpdates() {
        if (locationRequest == null) {
            createLocationRequest();
        }

        if (isRequestingLocationUpdates() || !googleApiClient.isConnected()) {
            Log.e(Constants.TAG, "Location request not created: "
                    + "\nRequesting location updates: " + isRequestingLocationUpdates()
                    + "\nGoogle api client connected: " + googleApiClient.isConnected());
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        requestingLocationUpdates = true;

        App.getWebSocketManager().createSocket(Constants.HOST, true);
    }


    /**
     * Stops location updates if started before and removes flag about requesting location
     * updates.
     */
    public void stopLocationUpdates() {
        if (!googleApiClient.isConnected()) {
            requestingLocationUpdates = false;
            return;
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        requestingLocationUpdates = false;

        App.getWebSocketManager().disconnectSocket();
    }

    /**
     * Removes old marker and place new one according to provided location.
     *
     * @param location - marker will placed at provided location.
     */
    public void updateMarker(Location location) {
        if (currentUserMarker != null) {
            currentUserMarker.remove();
        }

        String title = App.getUserManager().getCurrentUser().getName();

        // TODO: Replace title with user ID or name
        currentUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(Utils.latLngFromLocation(location))
                .title(title));

        if (!isCameraFixed) {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(Utils.latLngFromLocation(location), 17.0f)
            );
        }
    }

    /**
     * Updates user info according to location changes
     * and notifies server with new clients location.
     *
     * @param location - location received from LocationListener
     */
    @Override
    public void onLocationChanged(Location location) {
        if (googleMap == null || location == null) {
            return;
        }

        currentLocation = location;
        updateMarker(location);

        App.getWebSocketManager().sendLocationToServer(location);

        long currentUpdate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (isMoving && timeSinceLastUpdate - currentUpdate >= 60) {
            updateTotalDistancePassed();
        }
    }

    /**
     * Adds distance passed between 2 last locations if it is bigger than 1 meter.
     * Updates previous location to current to track next updates.
     */
    private void updateTotalDistancePassed() {
        if (previousLocation == null || currentLocation == null) {
            return;
        }

        double metersPassed = SphericalUtil.computeDistanceBetween(
                Utils.latLngFromLocation(previousLocation),
                Utils.latLngFromLocation(currentLocation)
        );

        previousLocation = currentLocation;
        timeSinceLastUpdate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        App.getUserManager().getCurrentUser().setDistance(
                App.getUserManager().getCurrentUser().getDistance() + Double.valueOf(metersPassed).intValue());

        updateUserInfo();
    }

    private void updateUserInfo() {
        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser != null) {
            currentUser.setExperience(
                currentUser.getExperience() +
                App.getUserManager().getCurrentUser().getDistance() / Constants.EXPERIENCE_MULTIPLIER);
            currentUser.setLevel(currentUser.getExperience() / Constants.LEVEL_MULTIPLIER);
        }

        App.getUserManager().updateCurrentUserInDB();
        App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_UPDATE_UI));
    }

    /**
     * Receives Google Play client connected / disconnected state to react
     * appropriately.
     */
    private BroadcastReceiver googleApiClientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean googleApiClientConnected = intent.getBooleanExtra(Constants.EXTRA_GAC_CONNECTED, false);
            if (googleApiClientConnected) {
                startLocationUpdates();
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                previousLocation = currentLocation;
            } else {
                stopLocationUpdates();
            }
        }
    };

    /**
     * Returns local broadcast receiver Google Play services connection state updates
     *
     * @return googleApiClientReceiver which handles Google Play services connection state updates.
     */
    public BroadcastReceiver getGoogleApiClientReceiver() {
        return googleApiClientReceiver;
    }

    private BroadcastReceiver stepsCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isMoving) {
                Toast.makeText(context, "Started moving.", Toast.LENGTH_SHORT).show();
                isMoving = true;
            }
            handler.removeCallbacks(stopMoving);
            handler.postDelayed(stopMoving, 3000);
        }
    };

    public BroadcastReceiver getStepsCountReceiver() {
        return stepsCountReceiver;
    }
}
