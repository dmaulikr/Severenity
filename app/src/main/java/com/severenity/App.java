package com.severenity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.severenity.engine.managers.data.MessageManager;
import com.severenity.engine.managers.data.PlacesManager;
import com.severenity.engine.managers.data.QuestManager;
import com.severenity.engine.managers.data.TeamManager;
import com.severenity.engine.managers.data.TeamQuestManager;
import com.severenity.engine.managers.data.UserManager;
import com.severenity.engine.managers.game.SkillManager;
import com.severenity.engine.managers.location.LocationManager;
import com.severenity.engine.managers.messaging.GCMManager;
import com.severenity.engine.network.NetworkManager;
import com.severenity.engine.network.RestManager;
import com.severenity.engine.network.WebSocketManager;
import com.severenity.utils.helpers.GoogleApiHelper;
import com.severenity.utils.FontsOverride;
import com.severenity.utils.common.Constants;
import com.severenity.view.activities.LoginActivity;

import io.realm.Realm;

/**
 * Main app file. Handles all managers and is central point for access to the managers.
 *
 * Created by Novosad on 3/24/16.
 */
public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private LocationManager locationManager;
    private LocalBroadcastManager localBroadcastManager;
    private UserManager userManager;
    private SkillManager skillManager;
    private WebSocketManager webSocketManager;
    private MessageManager msgManager;
    private QuestManager questManager;
    private GCMManager gcmManager;
    private RestManager restManager;
    private PlacesManager mPlacesManager;
    private TeamManager mTeamManager;
    private NetworkManager mNetworkManager;
    private TeamQuestManager teamQuestManager;

    private static App mInstance;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/zekton.ttf");

        Realm.init(this);

        mInstance = this;
        Context mContext = getApplicationContext();
        googleApiHelper = new GoogleApiHelper();
        locationManager = new LocationManager(mContext);
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        userManager = new UserManager(mContext);
        skillManager = new SkillManager(mContext);
        webSocketManager = new WebSocketManager();
        restManager = new RestManager(mContext);
        questManager = new QuestManager(mContext);
        teamQuestManager = new TeamQuestManager(mContext);
        gcmManager = new GCMManager();
        msgManager = new MessageManager(mContext);
        mNetworkManager = new NetworkManager(mContext);

        sharedPreferences = getSharedPreferences("Severenity", MODE_PRIVATE);
        mPlacesManager = new PlacesManager(mContext);
        mTeamManager = new TeamManager();
    }

    public static SharedPreferences getSharedPreferences() {
        return getInstance().sharedPreferences;
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

    public static GoogleApiClient getGoogleApiClient() {
        return getInstance().googleApiHelper.getGoogleApiClient();
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().googleApiHelper;
    }

    public static LocationManager getLocationManager() {
        return getInstance().locationManager;
    }

    public static GCMManager getGCMManager() {
        return getInstance().gcmManager;
    }

    public static LocalBroadcastManager getLocalBroadcastManager() {
        return getInstance().localBroadcastManager;
    }

    public static UserManager getUserManager() {
        return getInstance().userManager;
    }

    public static RestManager getRestManager() {
        return getInstance().restManager;
    }

    public static SkillManager getSpellManager() {
        return getInstance().skillManager;
    }

    public static WebSocketManager getWebSocketManager() {
        return getInstance().webSocketManager;
    }

    public static MessageManager getMessageManager() {
        return getInstance().msgManager;
    }

    public static QuestManager getQuestManager() {
        return getInstance().questManager;
    }

    public static TeamQuestManager getTeamQuestManager() {
        return getInstance().teamQuestManager;
    }

    public static PlacesManager getPlacesManager() {
        return getInstance().mPlacesManager;
    }

    public static TeamManager getTeamManager() {
        return getInstance().mTeamManager;
    }

    public static NetworkManager getNetworkManager() {
        return getInstance().mNetworkManager;
    }
}
