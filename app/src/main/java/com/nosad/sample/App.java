package com.nosad.sample;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.nosad.sample.engine.managers.data.UserManager;
import com.nosad.sample.engine.managers.game.SpellManager;
import com.nosad.sample.engine.managers.location.LocationManager;
import com.nosad.sample.entity.User;
import com.nosad.sample.helpers.GoogleApiHelper;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 3/24/16.
 */
public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;
    private LocalBroadcastManager localBroadcastManager;
    private UserManager userManager;
    private SpellManager spellManager;

    private static App mInstance;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mContext = getApplicationContext();
        googleApiHelper = new GoogleApiHelper(mContext);
        locationManager = new LocationManager(mContext);
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        userManager = new UserManager(mContext);
        spellManager = new SpellManager(mContext);
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

    public UserManager getUserManagerInstance() {
        return this.userManager;
    }

    public static UserManager getUserManager() {
        return getInstance().getUserManagerInstance();
    }

    public SpellManager getSpellManagerInstance() {
        return this.spellManager;
    }

    public static SpellManager getSpellManager() {
        return getInstance().getSpellManagerInstance();
    }
}
