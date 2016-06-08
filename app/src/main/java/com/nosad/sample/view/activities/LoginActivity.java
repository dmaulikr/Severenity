package com.nosad.sample.view.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.Toast;

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
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.managers.messaging.GCMManager;
import com.nosad.sample.engine.managers.messaging.RegistrationIntentService;
import com.nosad.sample.engine.network.RequestCallback;
import com.nosad.sample.engine.network.RestManager;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.FacebookUtils;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

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

    // The BroadcastReceiver that tracks network connectivity changes.
    private RestManager.NetworkReceiver mNetworkReceiver;

    public Profile profile;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;

    private View mProgressView;

    private Intent mMainActivityIntent;
    private boolean isAuthorizing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();
        
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        mMainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

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

        authorizeCurrentUser();
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

        mProgressView = findViewById(R.id.login_progress);
    }

    private void registerReceivers() {
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkReceiver = App.getRestManager().getNetworkReceiver();
        this.registerReceiver(mNetworkReceiver, filter);

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
        this.unregisterReceiver(mNetworkReceiver);
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
            App.getRestManager().authorizeUser(AccessToken.getCurrentAccessToken().getUserId(), new RequestCallback() {
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
                                    isAuthorizing = false;

                                    User newUser = createUserFromJSON(userObject);
                                    if (newUser != null) {
                                        App.getUserManager().setCurrentUser(newUser);
                                    }

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
                    if (data.has("name") && data.has("id") && data.has("email")) {
                        user.setEmail(data.getString("email"));
                        user.setName(data.getString("name"));

                        App.getRestManager().createUser(user, new RequestCallback() {
                            @Override
                            public void onResponseCallback(JSONObject response) {
                                if (response != null) {
                                    Log.d(Constants.TAG, response.toString());

                                    User newUser = createUserFromJSON(response);
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
     * Creates user in db
     *
     * @param response - JSON response to create user from.
     */
    private User createUserFromJSON(JSONObject response) {
        User user = new User();
        try {
            user.setCreatedDate(response.getString("createdDate"));
            user.setId(response.getString("userId"));
            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));

            JSONObject profileObject = response.getJSONObject("profile");
            user.setDistance(profileObject.getInt("distance"));
            user.setExperience(profileObject.getInt("experience"));
            user.setImmunity(profileObject.getInt("immunity"));
            user.setIntelligence(profileObject.getInt("intelligence"));
            user.setCredits(profileObject.getInt("credits"));
            user.setImplantHP(profileObject.getInt("implantHP"));
            user.setLevel(profileObject.getInt("level"));
            user.setMaxImmunity(profileObject.getInt("maxImmunity"));
            user.setMaxIntelligence(profileObject.getInt("maxIntelligence"));
            user.setViewRadius(profileObject.getInt("viewRadius") * 1.0);
            user.setActionRadius(profileObject.getInt("actionRadius") * 1.0);

            return App.getUserManager().addUser(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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
        startService(intent);
    }

    /**
     * Shows the progress UI and hides the login form.
     *
     * @param show - if true - show the progress, otherwise hide it.
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
                    startActivity(mMainActivityIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong :-(", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}

