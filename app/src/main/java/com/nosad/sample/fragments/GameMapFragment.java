package com.nosad.sample.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.nosad.sample.R;
import com.nosad.sample.common.Constants;
import com.nosad.sample.network.WebSocketManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameMapFragment.OnPauseGameListener} interface
 * to handle interaction events.
 */
public class GameMapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private SupportMapFragment mapFragment;

    private AppCompatActivity activity;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker currentUserMarker;

    private boolean accessFineLocationGranted = false;

    private boolean requestingLocationUpdates = false;

    private OnPauseGameListener onPauseGameListener;

    public GameMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            accessFineLocationGranted = true;
        } else {
            requestPermissions(
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                LOCATION_PERMISSION_REQUEST_CODE
            );
        }

        if (accessFineLocationGranted) {
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                buildGoogleApiClient();
                createLocationRequest();
                googleApiClient.connect();
                break;
            default: break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
    }

    @Override
    public void onResume() {
        Log.v(Constants.TAG, this.toString() + " onResume()");
        super.onResume();
        if (googleMap == null) {
            googleMap = mapFragment.getMap();
        }

        googleMap.setMyLocationEnabled(true);

        if (accessFineLocationGranted) {
            googleApiClient.connect();
        }
        // TODO: Reenable websocket connection when we'll get to server side
//        WebSocketManager.instance.createWebSocket(Constants.WS_ADDRESS, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Button btnMainMenu = (Button) view.findViewById(R.id.btnMainMenu);
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPauseGameListener != null) {
                    onPauseGameListener.onPauseGame();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) getActivity();
        try {
            onPauseGameListener = (OnPauseGameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        Log.v(Constants.TAG, this.toString() + " onPause()");
        super.onPause();

        if (accessFineLocationGranted) {
            stopLocationUpdates();
            googleApiClient.disconnect();
        }

        // TODO: Reenable websocket connection when we'll get to server side
//        WebSocketManager.instance.disconnectWebSocketClient();
    }

    /**
     * Stops location updates if started before and removes flag about requesting location
     * updates.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        requestingLocationUpdates = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPauseGameListener = null;
    }

    /**
     * Builds Google API client and registers for listening to connection success/fail updates.
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Creates location request with interval 1-5 secs and high accuracy
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Initializes location updates according to created location request and sets
     * flag about requesting location updates.
     */
    protected void startLocationUpdates() {
        if (locationRequest == null) {
            Log.e(Constants.TAG, "Location request not created.");
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        requestingLocationUpdates = true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constants.TAG, "Connected to Google API");

        if (!requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Constants.TAG, "Connection suspended to Google API");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Constants.TAG, "Connection failed to Google API");
    }

    /**
     * Removes old marker and place new one according to provided location.
     *
     * @param location - marker will placed at provided location.
     */
    public void updateMarker(Location location) {
        if (googleMap == null && location == null) {
            return;
        }

        if (currentUserMarker != null) {
            currentUserMarker.remove();
        }

        // TODO: Replace title with user ID or name
        currentUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Me"));
    }

    /**
     * Updates user info according to location changes
     * and notifies server with new clients location.
     *
     * @param location - location received from LocationListener
     */
    @Override
    public void onLocationChanged(Location location) {
        updateMarker(location);
        // TODO: Reenable websocket connection when we'll get to server side
//        WebSocketManager.instance.sendLocationToServer(location);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPauseGameListener {
        void onPauseGame();
    }

}