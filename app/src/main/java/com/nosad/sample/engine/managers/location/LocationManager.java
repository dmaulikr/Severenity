package com.nosad.sample.engine.managers.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.nosad.sample.App;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 3/29/16.
 */
public class LocationManager implements LocationListener {
    private Location previousLocation;
    private Location currentLocation;
    private Context context;
    private GoogleApiClient googleApiClient;
    private Marker currentUserMarker;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;
    public boolean requestingLocationUpdates = false;

    private int totalMetersPassed = 0;

    public Location getCurrentLocation() {
        return currentLocation;
    }
    public int getTotalMetersPassed() {
        return totalMetersPassed;
    }

    public LocationManager(Context context) {
        this.context = context;
        this.googleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        createLocationRequest();
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

        if (googleMap == null) {
            googleMap = map;
        }
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }

    public boolean isRequestingLocationUpdates() {
        return requestingLocationUpdates;
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

        // TODO: Reenable websocket connection when we'll get to server side
//        WebSocketManager.instance.disconnectWebSocketClient();
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

        // TODO: Reenable websocket connection when we'll get to server side
//        WebSocketManager.instance.createWebSocket(Constants.WS_ADDRESS, true);
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

        // TODO: Replace title with user ID or name
        currentUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(Utils.latLngFromLocation(location))
                .title("Me"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Utils.latLngFromLocation(location), 17.0f));
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
        updateTotalDistancePassed();

        // TODO: Reenable websocket connection when we'll get to server side
//        WebSocketManager.instance.sendLocationToServer(location);
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

        if (metersPassed >= 10) {
            previousLocation = currentLocation;
            totalMetersPassed += metersPassed;
        }
    }

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

    public BroadcastReceiver getGoogleApiClientReceiver() {
        return googleApiClientReceiver;
    }
}
