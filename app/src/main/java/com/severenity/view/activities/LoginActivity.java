package com.severenity.view.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.managers.messaging.GCMManager;
import com.severenity.engine.managers.messaging.RegistrationIntentService;
import com.severenity.engine.network.NetworkManager;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.FacebookUtils;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A login screen that offers login via Facebook account.
 */
public class LoginActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public Profile profile;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;

    private ProgressDialog mProgressView;
    private TextView tvConnectionState;

    private Intent mMainActivityIntent;
    private boolean isAuthorizing = false;

    private NetworkManager.OnConnectionChangedListener onConnectionChangedListener = new NetworkManager.OnConnectionChangedListener() {
        @Override
        public void onConnectionChanged(boolean connected) {
            updateConnectionState();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();
        
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        mMainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                profile = Profile.getCurrentProfile();
                mProfileTracker.stopTracking();
            }
        };

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                authorizeCurrentUser();
            }
        };

        mAccessTokenTracker.startTracking();
        mProfileTracker.startTracking();

        initViews();
        registerReceivers();
    }

    private void initViews() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.btnLoginWithFacebook);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                authorizeCurrentUser();
            }

            @Override
            public void onCancel() {
                Log.w(Constants.TAG, "Facebook login attempt cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                Log.e(Constants.TAG, "Facebook login attempt failed");
            }
        });

        tvConnectionState = (TextView) findViewById(R.id.tvConnectionState);
    }

    private void registerReceivers() {
        App.getNetworkManager().addOnConnectionChangedListener(onConnectionChangedListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GCMManager.REGISTRATION_PROCESS);
        App.getLocalBroadcastManager().registerReceiver(gcmReceiver, intentFilter);
    }

    private boolean playServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(Constants.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE
                    },
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private boolean checkPermission() {
        boolean result;

        if (!playServicesAvailable()) {
            return false;
        }

        int phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        result =
            phoneState == PackageManager.PERMISSION_GRANTED &&
            fineLocation == PackageManager.PERMISSION_GRANTED;

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(Constants.TAG, "Request for " + permissions[i] + " is granted. ");
                    } else {
                        Log.i(Constants.TAG, "Request for " + permissions[i] + " is not granted. ");
                        finish();
                    }
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregisters BroadcastReceiver when app is destroyed.
        App.getLocalBroadcastManager().unregisterReceiver(gcmReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.getLocalBroadcastManager().unregisterReceiver(gcmReceiver);
    }

    private void authorizeCurrentUser() {
        if (checkPermission() && AccessToken.getCurrentAccessToken() != null && !isAuthorizing) {
            isAuthorizing = true;
            showProgress(true);
            App.getUserManager().authorizeUser(AccessToken.getCurrentAccessToken().getUserId(), new RequestCallback() {
                @Override
                public void onResponseCallback(JSONObject response) {
                    try {
                        String result = response.getString("result");

                        switch (result) {
                            case "success":
                                JSONObject userObject = response.getJSONObject("user");
                                JSONArray devices = userObject.getJSONArray("devices");
                                String userId = userObject.getString("userId");

                                // If device was not registered to the user - start registration service
                                if (devices.length() == 0) {
                                    startDeviceRegistrationService(userId);
                                } else {
                                    if (!checkDeviceRegistrationToken(devices.getJSONObject(0).getString("registrationId"))) {
                                        startDeviceRegistrationService(userId);
                                        return;
                                    }

                                    isAuthorizing = false;

                                    User userFromJson = Utils.createUserFromJSON(userObject);
                                    if (App.getUserManager().getUser(userFromJson) != null) {
                                        App.getUserManager().updateCurrentUserLocallyWithUser(userFromJson);
                                    } else {
                                        App.getUserManager().setCurrentUser(App.getUserManager().addUser(userFromJson));
                                    }

                                    App.getWebSocketManager().sendAuthenticatedToServer();
                                    App.getNetworkManager().removeOnConnectionChangedListener(onConnectionChangedListener);
                                    startActivity(mMainActivityIntent);
                                }
                                break;
                            case "continue":
                                isAuthorizing = false;
                                if (response.getInt("reason") == 1) {
                                    createUser();
                                } else {
                                    Log.e(Constants.TAG, "Unknown reason value.");
                                }
                                break;
                            case "error":
                                Log.e(Constants.TAG, "Error handling is not implemented yet.");
                                break;
                            default:
                                Log.e(Constants.TAG, "Unknown result value.");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorCallback(NetworkResponse response) {
                    Log.e(Constants.TAG, response != null ? response.toString() : "Response is null");
                }
            });
        }
    }

    /**
     * Triggers user creation on the server and stores
     * newly created user in local db.
     */
    private void createUser() {
        FacebookUtils.getFacebookUserById(AccessToken.getCurrentAccessToken().getUserId(), "id,name,email", new FacebookUtils.Callback() {
            @Override
            public void onResponse(GraphResponse response) {
                final User user = new User();
                user.setId(AccessToken.getCurrentAccessToken().getUserId());
                try {
                    JSONObject data = response.getJSONObject();
                    if (data.has("name") && data.has("id")) {
                        if (data.has("email")) {
                            user.setEmail(data.getString("email"));
                        }

                        user.setName(data.getString("name"));

                        App.getUserManager().createUser(user, new RequestCallback() {
                            @Override
                            public void onResponseCallback(JSONObject response) {
                                if (response != null) {
                                    Log.d(Constants.TAG, response.toString());

                                    User newUser = Utils.createUserFromJSON(response);
                                    if (newUser != null) {
                                        App.getUserManager().setCurrentUser(newUser);
                                        authorizeCurrentUser();
                                    }
                                } else {
                                    Log.e(Constants.TAG, "User create has null response.");
                                }
                            }

                            @Override
                            public void onErrorCallback(NetworkResponse response) {
                                if (response != null) {
                                    Log.e(Constants.TAG, response.toString());
                                } else {
                                    Log.e(Constants.TAG, "User create error has null response.");
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Starts {@link RegistrationIntentService} to register current device.
     *
     * @param userId - id of the user to bind device to.
     */
    private void startDeviceRegistrationService(String userId) {
        Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_ID, Utils.getDeviceId(this));
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_NAME, Utils.getDeviceName());
        intent.putExtra(Constants.INTENT_EXTRA_USER_ID, userId);
        intent.putExtra(Constants.INTENT_EXTRA_REGISTRATION_ID, App.getCurrentFCMToken());
        startService(intent);
    }

    /**
     * Checks current device token against one used on the server.
     *
     * @param token - token of the current device assigned to user
     * @return true if token is the same as current, false otherwise.
     */
    private boolean checkDeviceRegistrationToken(String token) {
        return token.equals(App.getCurrentFCMToken());
    }

    /**
     * Updates connection state label look and feel and performs actions on connectivity change.
     */
    private void updateConnectionState() {
        if (App.getNetworkManager().isConnected()) {
            tvConnectionState.setText(getResources().getString(R.string.connected));
            tvConnectionState.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            Utils.expandOrCollapse(tvConnectionState, false, false);
            authorizeCurrentUser();
        } else {
            tvConnectionState.setText(getResources().getString(R.string.disconnected));
            tvConnectionState.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            Utils.expandOrCollapse(tvConnectionState, true, false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     *
     * @param show - if true - show the progress, otherwise hide it.
     */
    private void showProgress(final boolean show) {
        if (!isFinishing()) {
            if (show) {
                mProgressView = ProgressDialog.show(
                        LoginActivity.this,
                        getResources().getString(R.string.authentication),
                        getResources().getString(R.string.authentication_in_progress),
                        true);
            } else {
                mProgressView.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateConnectionState();

        if (AccessToken.getCurrentAccessToken() == null) {
            LoginManager.getInstance().logOut();
        }

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GCMManager.REGISTRATION_PROCESS)) {
                showProgress(false);
                isAuthorizing = false;

                String result = intent.getStringExtra("result");
                if (result.equals("success")) {
                    App.getWebSocketManager().sendAuthenticatedToServer();
                    App.getNetworkManager().removeOnConnectionChangedListener(onConnectionChangedListener);
                    startActivity(mMainActivityIntent);
                } else {
                    Log.wtf(Constants.TAG, "Critical error on the server.");
                }
            }
        }
    };
}

