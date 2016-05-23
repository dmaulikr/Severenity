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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.MarkerInfoAdapter;
import com.nosad.sample.engine.managers.messaging.GCMManager;
import com.nosad.sample.engine.network.RequestCallback;
import com.nosad.sample.entity.GamePlace;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Novosad on 3/29/16.
 */
public class LocationManager implements LocationListener {
    private Location previousLocation;
    private Location currentLocation;
    private Context context;
    private GoogleApiClient googleApiClient;
    private float currentZoom = (Constants.MAX_ZOOM_LEVEL + Constants.MIN_ZOOM_LEVEL) / 2 + 1.0f;

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
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if ((cameraPosition.zoom < Constants.MAX_ZOOM_LEVEL || cameraPosition.zoom > Constants.MIN_ZOOM_LEVEL) && currentLocation != null) {
                    googleMap.getUiSettings().setZoomGesturesEnabled(false);
                    if (cameraPosition.zoom < Constants.MAX_ZOOM_LEVEL) {
                        currentZoom = Constants.MAX_ZOOM_LEVEL;
                    } else {
                        currentZoom = Constants.MIN_ZOOM_LEVEL;
                    }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Utils.latLngFromLocation(currentLocation), currentZoom));
                } else {
                    currentZoom = cameraPosition.zoom;
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);
                }
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (App.getSpellManager().isChipMode()) {
                    // Handle spells here if needed
                } else {
                    resetCameraLocation();
                }

                App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_PLACE_ACTIONS));
            }
        });

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                resetCameraLocation();
                App.getSpellManager().cancelChipMode();
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

                try {
                    JSONObject markerType = new JSONObject(marker.getSnippet());

                    if (markerType.getInt(Constants.OBJECT_TYPE_IDENTIFIER) == Constants.TYPE_PLACE) {

                        Intent intent = new Intent(Constants.INTENT_FILTER_SHOW_PLACE_ACTIONS);
                        intent.putExtra(Constants.PLACE_ID, markerType.getString(Constants.PLACE_ID));

                        App.getLocalBroadcastManager().sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        // set listener for tracking clicking on the info
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(Constants.INTENT_FILTER_SHOW_PLACE_INFO_DIALOG);
                intent.putExtra(Constants.OBJECT_INFO, marker.getSnippet());

                App.getLocalBroadcastManager().sendBroadcast(intent);
                App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_PLACE_ACTIONS));
            }
        });

        displayPlaceMarkerFromDB();
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public boolean isRequestingLocationUpdates() {
        return requestingLocationUpdates;
    }

    /**
     * Creates location request with interval 1-5 secs and high accuracy
     */
    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.INTERVAL_LOCATION_UPDATE);
        locationRequest.setFastestInterval(Constants.INTERVAL_FAST_LOCATION_UPDATE);
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
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(user.getId())))
                .title(user.getName())
                .snippet(user.getJSONUserInfo()));
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

        if (App.getPlacesManager().findPlaceByID(place.getId()) == null) {

            GamePlace place_inner = new GamePlace(
                    place.getId().toString(),
                    place.getName().toString(),
                    place.getLatLng());

            App.getPlacesManager().addPlace(place_inner);
            //App.getPlacesManager().addOwnerToPlace(place_inner.getPlaceID(), AccessToken.getCurrentAccessToken().getUserId());

            googleMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(String.format("%s", place.getName()))
                    .snippet(place_inner.getJSONPlaceInfo()));
        }
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
        String userJSONIdentifier = App.getUserManager().getCurrentUser().getJSONUserInfo();

        // TODO: Replace title with user ID or name
        currentUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(Utils.latLngFromLocation(location))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(profileId)))
                .title(title)
                .snippet(userJSONIdentifier));

        if (!isCameraFixed) {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(Utils.latLngFromLocation(location), currentZoom)
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
    private int interval = Constants.INTERVAL_LOCATION_UPDATE * 2;

    /**
     * Adds distance passed between 2 last locations if it is bigger than 1 meter.
     * Updates previous location to current to track next updates.
     */
    private void updateTotalDistancePassed() {
        if (previousLocation == null || currentLocation == null) {
            return;
        }

        String previousLocationStr = previousLocation.getLatitude() + "," + previousLocation.getLongitude();
        String currentLocationStr = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + previousLocationStr
                    + "&destination=" + currentLocationStr
                    + "&mode=walking&units=metric";

        previousLocation = currentLocation;
        App.getRestManager().createRequest(url, Request.Method.GET, null, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                if (response != null) {
                    try {
                        double metersPassed = 0;
                        JSONArray routes = response.getJSONArray("routes");
                        for (int i = 0; i < routes.length(); i++) {
                            JSONObject route = routes.getJSONObject(i);
                            JSONArray legs = route.getJSONArray("legs");
                            for (int j = 0; j < legs.length(); j++) {
                                JSONObject distance = legs.getJSONObject(j).getJSONObject("distance");
                                metersPassed += distance.getDouble("value");
                            }
                        }

                        if (metersPassed >= Constants.AVERAGE_WALKING_SPEED && metersPassed <= Constants.AVERAGE_RUNNING_SPEED) {
                            updateUserInfo(Double.valueOf(metersPassed).intValue());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    }

    private void updateUserInfo(int metersPassed) {
        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser != null) {
            currentUser.setDistance(
                App.getUserManager().getCurrentUser().getDistance() + metersPassed);

            currentUser.setExperience(
                currentUser.getExperience() +
                    metersPassed / Constants.EXPERIENCE_MULTIPLIER);

            int calculatedLevel = currentUser.getExperience() / Constants.LEVEL_MULTIPLIER;
            if (calculatedLevel > currentUser.getLevel()) {
                currentUser.setLevel(calculatedLevel);

                Intent levelUpIntent = new Intent(context, MainActivity.class);
                levelUpIntent.setAction(GCMManager.MESSAGE_RECEIVED);
                levelUpIntent.putExtra("level", String.valueOf(calculatedLevel));

                App.getLocalBroadcastManager().sendBroadcast(levelUpIntent);
                Utils.sendNotification(Constants.NOTIFICATION_MSG_LEVEL_UP + calculatedLevel, context, levelUpIntent, 0);
            }
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

    public void displayPlaceMarkerFromDB() {

        ArrayList<GamePlace> place_inner = App.getPlacesManager().getPlaces();
        if (place_inner == null)
            return;

        for (GamePlace pl: place_inner) {

            googleMap.addMarker(new MarkerOptions()
                    .position(pl.getPlacePos())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(String.format("%s", pl.getPlaceName()))
                    .snippet(pl.getJSONPlaceInfo()));
        }
    }
}
