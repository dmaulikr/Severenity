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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.network.NetworkManager;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

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
        LoginButton loginButton = findViewById(R.id.btnLoginWithFacebook);
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

        tvConnectionState = findViewById(R.id.tvConnectionState);
    }

    private void registerReceivers() {
        App.getNetworkManager().addOnConnectionChangedListener(onConnectionChangedListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENT_FILTER_AUTHENTICATION);
        App.getLocalBroadcastManager().registerReceiver(authenticationReceiver, intentFilter);
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

    /**
     * Requests permission to proceed with application (GPS, Internet etc.)
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
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

        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        result = fineLocation == PackageManager.PERMISSION_GRANTED;

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
        App.getLocalBroadcastManager().unregisterReceiver(authenticationReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.getLocalBroadcastManager().unregisterReceiver(authenticationReceiver);
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

    private void authorizeCurrentUser() {
        if (checkPermission() && AccessToken.getCurrentAccessToken() != null && !isAuthorizing) {
            isAuthorizing = true;
            showProgress(true);
            App.getUserManager().authorizeCurrentUser();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_FILTER_AUTHENTICATION)) {
                showProgress(false);
                isAuthorizing = false;

                String result = intent.getStringExtra("result");
                if (result.equalsIgnoreCase("success")) {
                    App.getWebSocketManager().sendAuthenticatedToServer();
                    App.getNetworkManager().removeOnConnectionChangedListener(onConnectionChangedListener);
                    startActivity(mMainActivityIntent);
                } else if (result.equalsIgnoreCase("continue")) {
                    // Add continue authentication feedback
                } else if (result.equalsIgnoreCase("error")) {
                    showProgress(false);
                    isAuthorizing = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_error), Toast.LENGTH_SHORT).show();
                } else {
                    Log.wtf(Constants.TAG, "Critical error on the server.");
                }
            }
        }
    };
}

