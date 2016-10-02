package com.severenity.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.severenity.App;
import com.severenity.utils.common.Constants;

/**
 * Created by Novosad on 3/24/16.
 */
public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = GoogleApiHelper.class.getSimpleName();

    GoogleApiClient mGoogleApiClient;
    private Context context;

    public GoogleApiHelper(Context context) {
        this.context = context;
        buildGoogleApiClient();
    }

    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            broadcastConnectedState();
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //You are connected do what ever you want
        //Like i get last known location
        broadcastConnectedState();
        // test
    }

    private void broadcastConnectedState() {
        Intent intent = new Intent(Constants.INTENT_FILTER_GAC);
        intent.putExtra(Constants.EXTRA_GAC_CONNECTED, isConnected());
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: googleApiClient.connect()");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());
    }
}