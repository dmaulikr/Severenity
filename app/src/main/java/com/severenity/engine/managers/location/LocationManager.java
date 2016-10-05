package com.severenity.engine.managers.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.MarkerInfoAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.GamePlace;
import com.severenity.entity.User;
import com.severenity.entity.UserMarkerInfo;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class is responsible to handle all location related logic as far as map and GPS connectivity
 * handling.
 *
 * Created by Novosad on 3/29/16.
 */
public class LocationManager implements LocationListener {
    public enum GPSSignal {
        Strong(R.mipmap.gps_strong),
        Weak(R.mipmap.gps_weak),
        Off(R.mipmap.gps_off);

        private int id;

        GPSSignal(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private Location previousLocation, currentLocation, mLocationOfLastSquareUpdate, mLocationOfLastPlacesUpdateFromGoogle;
    private Context context;
    private GoogleApiClient googleApiClient;
    private float currentZoom = (Constants.MAX_ZOOM_LEVEL + Constants.MIN_ZOOM_LEVEL) / 2 + 1.0f;

    private Marker currentUserMarker, mTempUsersPlaceMarker;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;

    public boolean requestingLocationUpdates = false;
    private boolean isCameraFixed = false;
    private boolean mIsUpdatingLocationProcessStopped = false;
    private Circle mViewCircle, mActionCircle;
    private LatLng mWestSouthPoint, mNorthEastPoint;
    private Map<String, Marker> mPlaceMarkersList = new HashMap<>();;
    private Map<String, UserMarkerInfo> mOtherUsersList = new HashMap<>();

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
     * @param map - current map will be replaced with this instance.
     */
    public void updateMap(GoogleMap map) {
        if (map == null) {
            return;
        }

        googleMap = map;
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_json));

            if (!success) {
                Log.e(Constants.TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(Constants.TAG, "Can't find style.", e);
        }

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
                if (latLng != null) {
                    if (App.getSpellManager().isChipMode()) {
                        // Handle spells here if needed
                    } else {
                        resetCameraLocation();
                    }

                    App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_USER_ACTIONS));
                }
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

                    Intent intent = null;
                    switch (markerType.getInt(Constants.OBJECT_TYPE_IDENTIFIER)) {
                        case Constants.TYPE_PLACE: {
                            String placeID = markerType.getString(Constants.PLACE_ID);
                            GamePlace place = App.getPlacesManager().findPlaceByID(placeID);
                            if (place == null) {
                                Log.d(Constants.TAG, "Was not able to find place with provided ID: " + placeID);
                                return false;
                            }

                            if (App.getUserManager().getCurrentUser() != null && Utils.distanceBetweenLocations(Utils.latLngFromLocation(currentLocation), place.getPlacePos()) <=
                                    App.getUserManager().getCurrentUser().getActionRadius()) {
                                intent = new Intent(Constants.INTENT_FILTER_SHOW_USER_ACTIONS);
                                intent.putExtra(Constants.OBJECT_TYPE_IDENTIFIER, Constants.TYPE_PLACE);
                                intent.putExtra(Constants.PLACE_ID, placeID);
                            } else {
                                intent = new Intent(Constants.INTENT_FILTER_HIDE_USER_ACTIONS);
                            }

                            break;
                        }

                        case Constants.TYPE_USER: {
                            String userID = markerType.getString(Constants.USER_ID);
                            UserMarkerInfo userMarkerinfo = mOtherUsersList.get(userID);
                            if (userMarkerinfo == null) {
                                Log.e(Constants.TAG, "Was not able to find user with ID: " + userID);
                                break;
                            }

                            if (Utils.distanceBetweenLocations(Utils.latLngFromLocation(currentLocation), userMarkerinfo.getMarker().getPosition()) <=
                                    App.getUserManager().getCurrentUser().getActionRadius()) {
                                intent = new Intent(Constants.INTENT_FILTER_SHOW_USER_ACTIONS);
                                intent.putExtra(Constants.OBJECT_TYPE_IDENTIFIER, Constants.TYPE_USER);
                                intent.putExtra(Constants.USER_ID, userID);
                            } else {
                                intent = new Intent(Constants.INTENT_FILTER_HIDE_USER_ACTIONS);
                            }

                            break;
                        }

                        default:
                            Log.d(Constants.TAG, "Unknown marker type: " + markerType.getInt(Constants.OBJECT_TYPE_IDENTIFIER));
                            break;
                    }

                    if (intent != null) {
                        App.getLocalBroadcastManager().sendBroadcast(intent);
                    } else {
                        Log.e(Constants.TAG, "message intent is null");
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
                intent.putExtra(Constants.OBJECT_INFO_AS_JSON, marker.getSnippet());

                App.getLocalBroadcastManager().sendBroadcast(intent);
                App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_USER_ACTIONS));
            }
        });

        // remove blue circle around location
        map.setMyLocationEnabled(false);
    }

    /**
     * Resets current camera position to current location
     */
    public void resetCameraLocation() {
        fixCameraAtLocation(Utils.latLngFromLocation(currentLocation));
        isCameraFixed = false;
        mIsUpdatingLocationProcessStopped = false;
        if (mTempUsersPlaceMarker != null) {
            mTempUsersPlaceMarker.remove();
        }
    }

    /**
     * Points camera to the specified location in the meanwhile stopping camera updates
     * to current user location.
     *
     * @param latLng - location to fix camera at.
     */
    public void fixCameraAtLocation(LatLng latLng) {
        isCameraFixed = true;
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    /**
     * Returns current state of location updates.
     *
     * @return true if we are receiving location updates, false otherwise.
     */
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
     * @param user   - user to display marker of
     * @param latLng - location to display user at
     */
    public void displayUserAt(User user, LatLng latLng) {
        // remove all markers that has not been updated within 10 seconds
        Iterator<UserMarkerInfo> it = mOtherUsersList.values().iterator();
        while (it.hasNext()) {
            Long currentTime = System.currentTimeMillis();
            UserMarkerInfo markerInfo = it.next();
            if ((currentTime - (markerInfo.getLastUpdate()) > 10000/*10 seconds*/)) {
                it.remove();
            }
        }

        if (!mOtherUsersList.containsKey(user.getId())) {

            Location userLocation = new Location("user");
            userLocation.setLatitude(latLng.latitude);
            userLocation.setLongitude(latLng.longitude);

            if (currentLocation != null && currentLocation.distanceTo(userLocation) <= App.getUserManager().getCurrentUser().getViewRadius()) {

                UserMarkerInfo markerInfo = new UserMarkerInfo(googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(user.getId())))
                        .title(user.getName())
                        .snippet(user.getJSONUserInfo())), user);

                mOtherUsersList.put(user.getId(), markerInfo);
            }
        } else {
            // Check if user is within current user's ViewCircle
            UserMarkerInfo markerInfo = mOtherUsersList.get(user.getId());
            markerInfo.getMarker().remove();
            markerInfo.setMarker(googleMap.addMarker(
                    new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(user.getId())))
                    .snippet(markerInfo.getUser().getJSONUserInfo())
                    .title(markerInfo.getUser().getName())
            ));
            markerInfo.setUpdateTime(System.currentTimeMillis());
        }
    }

    public void displayPlaceMarker(Place place) {
        for (Integer placeType : place.getPlaceTypes()) {
            Log.i(Constants.TAG, "Place type for " + place.getName() + " is: " + placeType);
        }

        GamePlace.PlaceType placeType;

        if (place.getPlaceTypes().contains(Place.TYPE_BUS_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_TRANSIT_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_SUBWAY_STATION)
                || place.getPlaceTypes().contains(Place.TYPE_STREET_ADDRESS)
                || place.getPlaceTypes().contains(Place.TYPE_ROUTE)
                || place.getPlaceTypes().contains(Place.TYPE_MOVING_COMPANY)
                || place.getPlaceTypes().contains(Place.TYPE_MOVIE_RENTAL)
                || place.getPlaceTypes().contains(Place.TYPE_CAR_DEALER)
                || place.getPlaceTypes().contains(Place.TYPE_CAR_RENTAL)
                || place.getPlaceTypes().contains(Place.TYPE_CAR_REPAIR)
                || place.getPlaceTypes().contains(Place.TYPE_CAR_WASH)
                || place.getPlaceTypes().contains(Place.TYPE_TAXI_STAND)
                || place.getPlaceTypes().contains(Place.TYPE_HAIR_CARE)
                || place.getPlaceTypes().contains(Place.TYPE_LAUNDRY)
                || place.getPlaceTypes().contains(Place.TYPE_OTHER)) {
            return;
        } else if (place.getPlaceTypes().contains(Place.TYPE_BICYCLE_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_CLOTHING_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_CONVENIENCE_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_DEPARTMENT_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_FURNITURE_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_HOME_GOODS_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_JEWELRY_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_PET_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_SHOE_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_SHOPPING_MALL)
                || place.getPlaceTypes().contains(Place.TYPE_BANK)
                || place.getPlaceTypes().contains(Place.TYPE_ATM)
                || place.getPlaceTypes().contains(Place.TYPE_CASINO)
                || place.getPlaceTypes().contains(Place.TYPE_INSURANCE_AGENCY)
                || place.getPlaceTypes().contains(Place.TYPE_GROCERY_OR_SUPERMARKET)
                || place.getPlaceTypes().contains(Place.TYPE_ACCOUNTING)) {
            // display money
            placeType = GamePlace.PlaceType.Money;
        } else if (place.getPlaceTypes().contains(Place.TYPE_MEAL_DELIVERY)
                || place.getPlaceTypes().contains(Place.TYPE_MEAL_TAKEAWAY)
                || place.getPlaceTypes().contains(Place.TYPE_BAKERY)
                || place.getPlaceTypes().contains(Place.TYPE_BAR)
                || place.getPlaceTypes().contains(Place.TYPE_RESTAURANT)
                || place.getPlaceTypes().contains(Place.TYPE_CAFE)
                || place.getPlaceTypes().contains(Place.TYPE_HOSPITAL)
                || place.getPlaceTypes().contains(Place.TYPE_DENTIST)
                || place.getPlaceTypes().contains(Place.TYPE_DOCTOR)
                || place.getPlaceTypes().contains(Place.TYPE_PHARMACY)) {
            // display HP recovery
            placeType = GamePlace.PlaceType.ImplantRecovery;
        } else if (place.getPlaceTypes().contains(Place.TYPE_ART_GALLERY)
                || place.getPlaceTypes().contains(Place.TYPE_BOOK_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_LIBRARY)
                || place.getPlaceTypes().contains(Place.TYPE_SCHOOL)
                || place.getPlaceTypes().contains(Place.TYPE_MUSEUM)
                || place.getPlaceTypes().contains(Place.TYPE_UNIVERSITY)) {
            // display energy amount rise
            placeType = GamePlace.PlaceType.EnergyIncrease;
        } else if (place.getPlaceTypes().contains(Place.TYPE_ELECTRICIAN)
                || place.getPlaceTypes().contains(Place.TYPE_ELECTRONICS_STORE)
                || place.getPlaceTypes().contains(Place.TYPE_HARDWARE_STORE)) {
            // display implant repair
            placeType = GamePlace.PlaceType.ImplantRepair;
        } else if (place.getPlaceTypes().contains(Place.TYPE_CEMETERY)
                || place.getPlaceTypes().contains(Place.TYPE_CHURCH)
                || place.getPlaceTypes().contains(Place.TYPE_INSURANCE_AGENCY)
                || place.getPlaceTypes().contains(Place.TYPE_HINDU_TEMPLE)
                || place.getPlaceTypes().contains(Place.TYPE_MOSQUE)
                || place.getPlaceTypes().contains(Place.TYPE_SYNAGOGUE)) {
            // display immunity amount rise
            placeType = GamePlace.PlaceType.ImmunityIncrease;
        } else if (place.getPlaceTypes().contains(Place.TYPE_GYM)
                || place.getPlaceTypes().contains(Place.TYPE_STADIUM)) {
            // display HP rise
            placeType = GamePlace.PlaceType.ImplantIncrease;
        } else {
            // display only experience got
            placeType = GamePlace.PlaceType.Default;
        }

        if (App.getPlacesManager().findPlaceByID(place.getId()) == null) {

            GamePlace placeInner = new GamePlace(
                    place.getId(),
                    place.getName().toString(),
                    place.getLatLng(),
                    placeType);

            App.getPlacesManager().addPlace(placeInner, true /*send to the server*/);
            displayPlaceMarkerFromDB(true);
            //rememberAndDisplayMarker(placeInner);
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

        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String title = currentUser.getName();
        String profileId = currentUser.getId();
        String userJSONIdentifier = currentUser.getJSONUserInfo();

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
//            animateMarker(currentUserMarker, Utils.latLngFromLocation(location));
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

        if (mIsUpdatingLocationProcessStopped) {
            currentLocation = location;
            return;
        }

        if (mLocationOfLastSquareUpdate != null &&
                location.distanceTo(mLocationOfLastSquareUpdate) >= Constants.DISTANCE_TO_UPDATE_POSITIONS_CONSTS) {

            googleMap.clear();
            mViewCircle = null;
            mActionCircle = null;

            mLocationOfLastSquareUpdate = updateSquarePointsForFilteringLocations();
            displayPlaceMarkerFromDB(true);
        }

        if (mLocationOfLastPlacesUpdateFromGoogle != null && App.getUserManager().getCurrentUser() != null &&
                location.distanceTo(mLocationOfLastPlacesUpdateFromGoogle) >= App.getUserManager().getCurrentUser().getViewRadius()) {

            mLocationOfLastPlacesUpdateFromGoogle = currentLocation;

            App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_REQUEST_PLACES));
            displayPlaceMarkerFromDB(true);
        }

        currentLocation = location;

        updateMarker(location);
        displayUserActionAndViewCircles();

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

                        // Use very fast or average walking speed.
                        if (metersPassed >= Constants.AVERAGE_WALKING_SPEED && metersPassed <= Constants.MIN_RUNNING_SPEED / 2) {
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
        App.getQuestManager().updateQuestProgress("distance", String.valueOf(metersPassed));
        App.getUserManager().updateCurrentUserProgress(App.getUserManager().getCurrentUser().getId(), metersPassed);
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
                if (currentLocation != null) {
                    mLocationOfLastSquareUpdate = updateSquarePointsForFilteringLocations();
                    mLocationOfLastPlacesUpdateFromGoogle = mLocationOfLastSquareUpdate;
                    // TODO: afedechko: Think about this case in future. For now do not delete local DB
                    //App.getPlacesManager().clearPlacesAndOwnersData();
                    App.getPlacesManager().getPlacesFromServer(Utils.latLngFromLocation(currentLocation), 1000, placesRequestCallback);
                }
                previousLocation = currentLocation;
            } else {
                stopLocationUpdates();
            }
        }
    };

    /**
     * placesRequestCallback object used to handle response from the server about the places.
     * When Google API is connected Client sends request to the server to get places in
     * certain radius. This object will handle the response, parse JSON object and store
     * in DB.
     */
    private RequestCallback placesRequestCallback = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {
            if (response == null) {
                Log.e(Constants.TAG, "Incorrect response data.");
            } else {
                try {
                    String responseStatus = response.getString("result");
                    switch (responseStatus) {
                        case "success": {
                            Log.e(Constants.TAG, response.toString());
                            JSONArray array = response.getJSONArray("data");
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject place = array.getJSONObject(i);
                                if (!place.has("placeId") || !place.has("name")
                                    || !place.has("location") || !place.has("owners")
                                    || !place.has("type")) {
                                    Log.e(Constants.TAG, "Incorrect place info passed from the server.");
                                    continue;
                                }

                                String placeID   = place.getString("placeId");
                                String placeName = place.getString("name");
                                LatLng coordinates = new LatLng(place.getJSONObject("location").getJSONArray("coordinates").getDouble(1),
                                        place.getJSONObject("location").getJSONArray("coordinates").getDouble(0));
                                GamePlace.PlaceType placeType = GamePlace.PlaceType.values()[place.getInt("type")];

                                GamePlace gamePlace = new GamePlace(placeID, placeName, coordinates, placeType);
                                App.getPlacesManager().addPlace(gamePlace, false /*do not send to the server*/);

                                JSONArray owners = place.getJSONArray("owners");
                                for (int j = 0; j < owners.length(); ++j) {
                                    App.getPlacesManager().addOwnerToPlace(placeID, owners.getString(j));
                                }
                            }

                            displayPlaceMarkerFromDB(true);
                            App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_REQUEST_PLACES));
                            break;
                        }

                        default: {
                            Log.e(Constants.TAG, "Not success response status.");
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            if (response == null) {
                Log.e(Constants.TAG, "Request Places fails. Error responce is empty");
            } else {
                Log.e(Constants.TAG, response.toString());
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

    /**
     * Creates marker bitmap from the user profiles avatar.
     *
     * @param profileId - id of the FB account of the user.
     * @return bitmap image to set as icon for {@link Marker}.
     */
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

    /**
     * Displays markers for all places stored in local db.
     *
     * @param squareLimited - determines if the amount of places displayed should be limited by
     *                      square radius.
     */
    public void displayPlaceMarkerFromDB(boolean squareLimited) {
        if (App.getUserManager().getCurrentUser() == null) {
            return;
        }

        ArrayList<GamePlace> placesToShow;

        if (squareLimited) {
            placesToShow = App.getPlacesManager().getLimitedPlaces(Utils.latLngFromLocation(currentLocation),
                    mWestSouthPoint,
                    mNorthEastPoint,
                    App.getUserManager().getCurrentUser().getViewRadius());
        } else {
            placesToShow = App.getPlacesManager().getPlaces();
        }

        if (placesToShow == null) {
            return;
        }

        mPlaceMarkersList.clear();

        for (GamePlace place : placesToShow) {
            rememberAndDisplayMarker(place);
        }
    }

    /**
     * Draws both action circles (where user can perform actions) and view circles (where user
     * is able to observe other map items).
     */
    private void displayUserActionAndViewCircles() {

        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        if (mViewCircle == null) {

            CircleOptions viewCircle = new CircleOptions()
                    .fillColor(Constants.VIEW_CIRCLE_SHADE_COLOR)
                    .strokeColor(Constants.VIEW_CIRCLE_STOKE_COLOR)
                    .radius(currentUser.getViewRadius())
                    .center(Utils.latLngFromLocation(currentLocation))
                    .strokeWidth(Constants.VIEW_CIRCLE_BORDER_SIZE);

            mViewCircle = googleMap.addCircle(viewCircle);
        } else {
            mViewCircle.setCenter(Utils.latLngFromLocation(currentLocation));
        }

        if (mActionCircle == null) {
            CircleOptions actionCircle = new CircleOptions()
                    .fillColor(Constants.ACTION_CIRCLE_SHADE_COLOR)
                    .strokeColor(Constants.ACTION_CIRCLE_STOKE_COLOR)
                    .radius(currentUser.getActionRadius())
                    .center(Utils.latLngFromLocation(currentLocation))
                    .strokeWidth(Constants.ACTION_CIRCLE_BORDER_SIZE);

            mActionCircle = googleMap.addCircle(actionCircle);
        } else {
            mActionCircle.setCenter(Utils.latLngFromLocation(currentLocation));
        }
    }

    /**
     * Gets the left bottom and right top position of abstract square for request less amount
     * off places.
     *
     * @return the position where the last update has happened
     */
    private Location updateSquarePointsForFilteringLocations() {

        mWestSouthPoint = Utils.getPositionInMeter(Utils.latLngFromLocation(currentLocation), 100, Constants.WS_DIRECTION);
        mNorthEastPoint = Utils.getPositionInMeter(Utils.latLngFromLocation(currentLocation), 100, Constants.EN_DIRECTION);

        return currentLocation;
    }

    /**
     * Adds temporary marker to the map and navigates to it.
     * This marker will be deleted once user select map to relocate to it's
     * current place,
     *
     * @param placeID  id of the place to be displayed.
     */
    public void showPlaceAtPosition(String placeID) {

        GamePlace place = App.getPlacesManager().findPlaceByID(placeID);
        if (mTempUsersPlaceMarker != null) {
            mTempUsersPlaceMarker.remove();
        }

        mTempUsersPlaceMarker = googleMap.addMarker(new MarkerOptions()
                .position(place.getPlacePos())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(String.format("%s", place.getPlaceName()))
                .snippet(place.getJSONPlaceInfo()));

        mTempUsersPlaceMarker.showInfoWindow();

        fixCameraAtLocation(mTempUsersPlaceMarker.getPosition());

        mIsUpdatingLocationProcessStopped = true;
    }

    /**
     * Creates Marker for the place and remembers the marker locally. In case
     * of further changes.
     *
     * @param place - place for which marker to be created
     */
    private void rememberAndDisplayMarker(GamePlace place) {
        int resourceId = getPlaceResourceImage(place);

        mPlaceMarkersList.put(place.getPlaceID(),
                googleMap.addMarker(new MarkerOptions()
                        .position(place.getPlacePos())
                        .icon(BitmapDescriptorFactory.fromBitmap(Utils.getScaledMarker(resourceId, context)))
                        .title(String.format("%s", place.getPlaceName()))
                        .snippet(place.getJSONPlaceInfo())));
    }

    private int getPlaceResourceImage(GamePlace place) {
        int resourceId = R.drawable.place_experience_violet;
        boolean hasOwner = place.hasOwner(App.getUserManager().getCurrentUser().getId());
        switch (place.getPlaceType()) {
            case Default:
                if (hasOwner) {
                    resourceId = R.drawable.place_experience_blue;
                } else {
                    resourceId = R.drawable.place_experience_violet;
                }
                break;
            case Money:
                if (hasOwner) {
                    resourceId = R.drawable.place_money_blue;
                } else {
                    resourceId = R.drawable.place_money_violet;
                }
                break;
            case ImplantRecovery:
                if (hasOwner) {
                    resourceId = R.drawable.place_implant_recovery_blue;
                } else {
                    resourceId = R.drawable.place_implant_recovery_violet;
                }
                break;
            case ImplantRepair:
                if (hasOwner) {
                    resourceId = R.drawable.place_implant_repair_blue;
                } else {
                    resourceId = R.drawable.place_implant_repair_violet;
                }
                break;
            case ImplantIncrease:
                if (hasOwner) {
                    resourceId = R.drawable.place_implant_increase_blue;
                } else {
                    resourceId = R.drawable.place_implant_increase_violet;
                }
                break;
            case ImmunityIncrease:
                if (hasOwner) {
                    resourceId = R.drawable.place_immunity_increase_blue;
                } else {
                    resourceId = R.drawable.place_immunity_increase_violet;
                }
                break;
            case EnergyIncrease:
                if (hasOwner) {
                    resourceId = R.drawable.place_energy_increase_blue;
                } else {
                    resourceId = R.drawable.place_energy_increase_violet;
                }
                break;
        }

        return resourceId;
    }

    /**
     * Changes marker color based on it's state. Red for regular markers; Yellow for captured.
     *
     * @param placeID  - place for which marker to be changed
     */
    public void markPlaceMarkerAsCapturedUncaptured(String placeID) {
        if (mPlaceMarkersList.containsKey(placeID)) {
            Marker marker = mPlaceMarkersList.get(placeID);
            GamePlace place = App.getPlacesManager().findPlaceByID(placeID);
            int resourceId = getPlaceResourceImage(place);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(Utils.getScaledMarker(resourceId, context)));

        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection projection = googleMap.getProjection();
        Point startPoint = projection.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = projection.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();
        marker.setVisible(true);

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public interface OnGPSStateChangedListener {
        void onGPSStateChangedListener(GPSSignal signal);
    }

    private static ArrayList<OnGPSStateChangedListener> gpsStateChangedListeners = new ArrayList<>();

    /**
     * Handles GPS connectivity and sets current signal level, either strong, weak or off (disconnected)
     */
    public static class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateWithGPSSignal(context);
        }
    }

    /**
     * Notifies all listeners with current GPS signal level.
     */
    public static void updateWithGPSSignal(Context context) {
        android.location.LocationManager locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GPSSignal signal;
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            // "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
            signal = GPSSignal.Strong;
        } else if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "Device only. Uses GPS to determine location";
            signal = GPSSignal.Weak;
        } else {
            // "Battery saving. Uses Wi-Fi and mobile networks to determine location";
            signal = GPSSignal.Off;
        }

        for (OnGPSStateChangedListener listener : gpsStateChangedListeners) {
            listener.onGPSStateChangedListener(signal);
        }
    }

    /**
     * Adds new observer for the gps connection change.
     *
     * @param listener - {@link OnGPSStateChangedListener} listener instance to add.
     */
    public void addOnGPSStateChangedListener(OnGPSStateChangedListener listener) {
        gpsStateChangedListeners.add(listener);
    }

    /**
     * Removes observer from the listeners list.
     *
     * @param listener - {@link OnGPSStateChangedListener} listener instance to remove.
     */
    public void removeOnGPSStateChangedListener(OnGPSStateChangedListener listener) {
        gpsStateChangedListeners.remove(listener);
    }
}