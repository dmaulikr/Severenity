package com.nosad.sample.view.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.network.RestManager;
import com.nosad.sample.utils.common.Constants;

import java.util.Arrays;

/**
 * A login screen that offers login via Facebook account.
 */
public class LoginActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_CODE = 1;

    // The BroadcastReceiver that tracks network connectivity changes.
    private RestManager.NetworkReceiver receiver;

    public Profile profile;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private CallbackManager callbackManager;

    private View mProgressView;

    private Intent mainActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();
        
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                profile = Profile.getCurrentProfile();
                profileTracker.stopTracking();
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                updateWithCurrentAccessToken();
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        initViews();
        registerReceivers();

        updateWithCurrentAccessToken();
    }

    private void initViews() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.btnLoginWithFacebook);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                updateWithCurrentAccessToken();
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

        mProgressView = findViewById(R.id.login_progress);
    }

    private void registerReceivers() {
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = App.getRestManager().getNetworkReceiver();
        this.registerReceiver(receiver, filter);
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
        requestPermissions(
            new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
            },
            PERMISSION_REQUEST_CODE
        );
    }

    private boolean checkPermission() {
        boolean result;

        if (!playServicesAvailable()) {
            return false;
        }

        int phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (phoneState == PackageManager.PERMISSION_GRANTED && fineLocation == PackageManager.PERMISSION_GRANTED) {
            result = true;
        } else {
            result = false;
        }

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
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private void updateWithCurrentAccessToken() {
        if (checkPermission()) {
            if (AccessToken.getCurrentAccessToken() != null) {
                startActivity(mainActivityIntent);
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update if connection is available
        App.getRestManager().updateConnectedFlags();

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

