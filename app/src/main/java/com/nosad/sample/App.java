package com.nosad.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.nosad.sample.engine.managers.data.MessageManager;
import com.nosad.sample.engine.managers.data.PlacesManager;
import com.nosad.sample.engine.managers.data.QuestManager;
import com.nosad.sample.engine.managers.data.UserManager;
import com.nosad.sample.engine.managers.game.ChipManager;
import com.nosad.sample.engine.managers.location.LocationManager;
import com.nosad.sample.engine.managers.messaging.GCMManager;
import com.nosad.sample.engine.network.RestManager;
import com.nosad.sample.engine.network.WebSocketManager;
import com.nosad.sample.helpers.GoogleApiHelper;
import com.nosad.sample.utils.FontsOverride;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.LoginActivity;

/**
 * Created by Novosad on 3/24/16.
 */
public class App extends MultiDexApplication {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;
    private LocalBroadcastManager localBroadcastManager;
    private UserManager userManager;
    private ChipManager chipManager;
    private WebSocketManager webSocketManager;
    private MessageManager msgManager;
    private QuestManager questManager;
    private GCMManager gcmManager;
    private RestManager restManager;
    private PlacesManager mPlacesManager;

    private static App mInstance;
    private static Context mContext;

    private SharedPreferences sharedPrefereces;

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/zekton.ttf");

        FacebookSdk.sdkInitialize(this);

        mInstance = this;
        mContext = getApplicationContext();
        googleApiHelper = new GoogleApiHelper(mContext);
        locationManager = new LocationManager(mContext);
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        userManager = new UserManager(mContext);
        chipManager = new ChipManager(mContext);
        webSocketManager = new WebSocketManager(mContext);
        restManager = new RestManager(mContext);
        questManager = new QuestManager(mContext);
        gcmManager = new GCMManager(mContext);
        if (webSocketManager.createSocket(Constants.HOST, true)) {
            webSocketManager.subscribeForMessageEvent();
        }
        msgManager = new MessageManager(mContext);

        sharedPrefereces = getSharedPreferences("Severenity", MODE_PRIVATE);
        mPlacesManager = new PlacesManager(mContext);
    }

    public SharedPreferences getSharedPreferencesInstance() {
        return this.sharedPrefereces;
    }

    public static SharedPreferences getSharedPreferences() {
        return getInstance().getSharedPreferencesInstance();
    }

    public static void setCurrentFCMToken(String token) {
        getSharedPreferences().edit().putString(Constants.INTENT_EXTRA_REGISTRATION_ID, token).apply();
    }

    public static String getCurrentFCMToken() {
        return getSharedPreferences().getString(Constants.INTENT_EXTRA_REGISTRATION_ID, null);
    }

    public void logOut() {
        locationManager.stopLocationUpdates();

        LoginManager.getInstance().logOut();
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginActivity);
        webSocketManager.disconnectSocket();
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

    public static GCMManager getGCMManager() {
        return getInstance().getGCMManagerInstance();
    }

    public GCMManager getGCMManagerInstance() {
        return this.gcmManager;
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

    public RestManager getRestManagerInstance() {
        return this.restManager;
    }

    public static RestManager getRestManager() {
        return getInstance().getRestManagerInstance();
    }

    public ChipManager getSpellManagerInstance() {
        return this.chipManager;
    }

    public static ChipManager getSpellManager() {
        return getInstance().getSpellManagerInstance();
    }

    public WebSocketManager getWebSocketManagerInstance() {
        return this.webSocketManager;
    }

    public static WebSocketManager getWebSocketManager() {
        return getInstance().getWebSocketManagerInstance();
    }

    public MessageManager getMessageManagerInstance() {
        return this.msgManager;
    }

    public static MessageManager getMessageManager() {
        return getInstance().getMessageManagerInstance();
    }

    public QuestManager getQuestManagerInstance() {
        return this.questManager;
    }

    public static QuestManager getQuestManager() {
        return getInstance().getQuestManagerInstance();
    }

    public PlacesManager getPlacesManagerInstance() {
        return this.mPlacesManager;
    }

    public static PlacesManager getPlacesManager() {
        return getInstance().getPlacesManagerInstance();
    }
}
