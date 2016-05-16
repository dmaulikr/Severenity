package com.nosad.sample.engine.managers.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
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
import com.nosad.sample.engine.adapters.MarkerInfoAdapter;
import com.nosad.sample.engine.network.RequestCallback;
import com.nosad.sample.engine.network.RestManager;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

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

    private MarkerInfoAdapter mMarkerInfoAdapter = new MarkerInfoAdapter();

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public LocationManager(Context context) {
        this.context = context;
        this.googleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        createLocationRequest();

        checkDistanceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTotalDistancePassed();
                checkDistanceHandler.postDelayed(this, interval);
            }
        }, interval);
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
                    // Handle spells here if needed
                } else {
                    resetCameraLocation();
                }
            }
        });

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                resetCameraLocation();
                App.getSpellManager().cancelSpellMode();
                return true;
            }
        });

        // set adapter for custom view
        if (mMarkerInfoAdapter != null) {
            googleMap.setInfoWindowAdapter(mMarkerInfoAdapter);
        }

        // Set this to stick with current marker for some time
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                fixCameraAtLocation(marker.getPosition());
                return false;
            }
        });

        // set listener for tracking clicking on the info
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Toast.makeText(App.getInstance().getApplicationContext(), marker.getId().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Resets current camera position to current location
     */
    public void resetCameraLocation() {
        fixCameraAtLocation(Utils.latLngFromLocation(currentLocation));
        isCameraFixed = false;
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

    public boolean isRequestingLocationUpdates() {
        return requestingLocationUpdates;
    }

    /**
     * Creates location request with interval 1-5 secs and high accuracy
     */
    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(user.getId())))
                .title(user.getId()));
    }

    public void displayPlaceMarker(Place place) {
        for (Integer placeType : place.getPlaceTypes()) {
            Log.i(Constants.TAG, "Place type for " + place.getName() + " is: " + placeType);
        }

        if (place.getPlaceTypes().contains(Place.TYPE_BUS_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_TRANSIT_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_SUBWAY_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_STREET_ADDRESS)
                || place.getPlaceTypes().contains(Place.TYPE_ROUTE)) {
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

        App.getWebSocketManager().subscribeForLocationEvent();
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

        App.getWebSocketManager().unSubscribeFromLocationEvents();
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
        String profileId = App.getUserManager().getCurrentUser().getId();

        // TODO: Replace title with user ID or name
        currentUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(Utils.latLngFromLocation(location))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(profileId)))
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
    }

    private Handler checkDistanceHandler = new Handler();
    private int interval = 5000;

    /**
     * Adds distance passed between 2 last locations if it is bigger than 1 meter.
     * Updates previous location to current to track next updates.
     */
    private void updateTotalDistancePassed() {
        if (previousLocation == null || currentLocation == null) {
            return;
        }

        final double metersPassed = SphericalUtil.computeDistanceBetween(
            Utils.latLngFromLocation(previousLocation),
            Utils.latLngFromLocation(currentLocation)
        );

        previousLocation = currentLocation;
        App.getUserManager().getCurrentUser().setDistance(
                App.getUserManager().getCurrentUser().getDistance() + Double.valueOf(metersPassed).intValue());


        String previousLocationStr = previousLocation.getLatitude() + "," + previousLocation.getLongitude();
        String currentLocationStr = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + previousLocationStr
                    + "&destination=" + currentLocationStr
                    + "&mode=driving&units=metric";

        App.getRestManager().createRequest(url, Request.Method.GET, null, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                if (response != null) {
                    Log.d(Constants.TAG, "Directions API: " + response.toString());
                } else {
                    Log.d(Constants.TAG, "Directions API response is null.");
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null) {
                    Log.d(Constants.TAG, "Error: " + response.toString());
                } else {
                    Log.d(Constants.TAG, "Error: directions API response is null.");
                }
            }
        });

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

    private Bitmap getMarkerBitmapFromView(String profileId) {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.photo_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.ivPhotoMarker);
        Picasso.with(context).load("https://graph.facebook.com/" + profileId + "/picture?type=normal").into(markerImageView);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null) {
            drawable.draw(canvas);
        }
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}
