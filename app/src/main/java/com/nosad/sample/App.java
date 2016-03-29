package com.nosad.sample;

import android.app.Application;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.nosad.sample.engine.managers.location.LocationManager;
import com.nosad.sample.helpers.GoogleApiHelper;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 3/24/16.
 */
public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;
    private LocalBroadcastManager localBroadcastManager;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        googleApiHelper = new GoogleApiHelper(getApplicationContext());
        locationManager = new LocationManager(getApplicationContext());
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    public LocationManager getLocationManagerInstance() {
        return this.locationManager;
    }

    public static LocationManager getLocationManager() {
        return getInstance().getLocationManagerInstance();
    }

    public LocalBroadcastManager getLocalBroadcastManagerInstance() {
        return this.localBroadcastManager;
    }

    public static LocalBroadcastManager getLocalBroadcastManager() {
        return getInstance().getLocalBroadcastManagerInstance();
    }
}
