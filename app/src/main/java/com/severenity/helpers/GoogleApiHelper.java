package com.severenity.helpers;

import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.severenity.App;
import com.severenity.utils.common.Constants;

/**
 * Helps with creation of the google api client and managing APIs + connection state.
 *
 * Created by Novosad on 3/24/16.
 */
public class GoogleApiHelper {
    private GoogleApiClient mGoogleApiClient;

    public GoogleApiHelper() {
        // Empty constructor for App class.
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Intent intent = new Intent(Constants.INTENT_FILTER_GAC);
            intent.putExtra(Constants.EXTRA_GAC_CONNECTED, false);
            App.getLocalBroadcastManager().sendBroadcast(intent);
        }
    }
}