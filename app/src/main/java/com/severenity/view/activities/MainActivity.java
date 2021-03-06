package com.severenity.view.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.managers.location.LocationManager;
import com.severenity.engine.managers.messaging.FCMListener;
import com.severenity.engine.managers.messaging.GCMManager;
import com.severenity.engine.network.NetworkManager;
import com.severenity.entity.quest.Quest;
import com.severenity.entity.user.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.custom.SplitToolbar;
import com.severenity.view.dialogs.PlacesInfoDialog;
import com.severenity.view.fragments.GameMapFragment;
import com.severenity.view.fragments.PlayerFragment;
import com.severenity.view.fragments.QuestsFragment;
import com.severenity.view.fragments.ShopFragment;
import com.severenity.view.fragments.TeamQuestsFragment;
import com.severenity.view.fragments.clans.ClansFragment;
import com.squareup.picasso.Picasso;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.utils.SpotlightListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.text.DateFormat.getDateInstance;

public class MainActivity extends AppCompatActivity
        implements PlacesInfoDialog.OnRelocateMapListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private GoogleApiClient googleApiClient;
    private FirebaseAnalytics firebaseAnalytics;

    private SplitToolbar toolbarBottom;
    private Toolbar toolbarTop;

    private CircleImageView userProfilePicture;
    private TextView tvEnergyValue, tvCreditsValue, tvExperienceValue, tvLevelValue;
    private TextView tvConnectionState;
    private ImageView ivGPSState;

    /**    Tutorial items       **/
    private PreferencesManager mPreferencesManager;
    private ImageView ivTutorialBtn;
    private ActionMenuItemView shopItem;
    private ActionMenuItemView chatItem;
    private ActionMenuItemView mapItem;
    private ActionMenuItemView profileItem;
    private ActionMenuItemView questsItem;
    private ActionMenuItemView teamQuestsItem;
    private SpotlightView.Builder[] spotLightViewArr;
    private int spotLightCounter = 0;
    /**                         **/

    private FragmentManager fragmentManager;

    private ShopFragment shopFragment = new ShopFragment();
    private ClansFragment clansFragment = new ClansFragment();
    private PlayerFragment playerFragment = new PlayerFragment();
    private QuestsFragment questsFragment = QuestsFragment.newInstance();
    private GameMapFragment gameMapFragment = new GameMapFragment();
    private TeamQuestsFragment teamQuestsFragment = new TeamQuestsFragment();
    private String shopFragmentTag = ShopFragment.class.getSimpleName();
    private String clansFragmentTag = ClansFragment.class.getSimpleName();
    private String playerFragmentTag = PlayerFragment.class.getSimpleName();
    private String gameMapFragmentTag = GameMapFragment.class.getSimpleName();
    private String questsFragmentTag = QuestsFragment.class.getSimpleName();
    private String teamQuestsFragmentTag = TeamQuestsFragment.class.getSimpleName();
    private PlacesInfoDialog mPlaceInfoDialog;

    private ArrayList<Fragment> allFragments = new ArrayList<>();

    private boolean activityActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext());
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);

        mPreferencesManager = new PreferencesManager(MainActivity.this);
        initToolbars();
        initFragments();
        initSocketSubscriptions();
        initReceivers();

        processNewIntent(getIntent());

        toolbarBottom.findViewById(R.id.menu_map).callOnClick();

        buildGoogleApiClient();
        App.getGoogleApiHelper().setGoogleApiClient(googleApiClient);
    }

    private void checkForFirstLaunch() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        if (!sPref.getBoolean("isFirstLaunch", true)) {
            return;
        }

        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isFirstLaunch", false);
        ed.apply();
        mPreferencesManager.resetAll();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Tutorial")
                .setMessage("Wanna watch tutorial?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_tutorial");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "watched");
                        bundle.putString(FirebaseAnalytics.Param.VALUE, "yes");
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, bundle);
                        showTutorial();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_tutorial");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "watched");
                        bundle.putString(FirebaseAnalytics.Param.VALUE, "no");
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, bundle);
                    }
                })
                .create();
        dialog.show();
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .enableAutoManage(this, 0, this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(Constants.INTENT_FILTER_GAC);
        intent.putExtra(Constants.EXTRA_GAC_CONNECTED, App.getGoogleApiClient().isConnected());
        App.getLocalBroadcastManager().sendBroadcast(intent);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForFirstLaunch();
            }
        }, 3000);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(Constants.TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(Constants.TAG, "Connection lost.  Reason: Service Disconnected");
        } else {
            Log.d(Constants.TAG, "onConnectionSuspended: reason " + i);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Constants.TAG, "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());
    }

    /**
     * Method calls tutorial show and setups spot light view.
     */
    private void showTutorial() {
        spotLightViewArr = new SpotlightView.Builder[]{
                tutorialItem(shopItem, shopItem.getId() + "", getString(R.string.menu_shop), getString(R.string.tutorial_shop_body)),
                tutorialItem(profileItem, profileItem.getId() + "", getString(R.string.menu_profile), getString(R.string.tutorial_profile_body)),
                tutorialItem(mapItem, mapItem.getId() + "", getString(R.string.menu_map), getString(R.string.tutorial_map_body)),
                tutorialItem(chatItem, chatItem.getId() + "", getString(R.string.title_chat), getString(R.string.tutorial_chat_body)),
                tutorialItem(questsItem, questsItem.getId() + "", getString(R.string.menu_quests), getString(R.string.tutorial_quests_body)),
                tutorialItem(toolbarTop, toolbarTop.getId() + "", getString(R.string.tutorial_toolbar_top), getString(R.string.tutorial_toolbar_top_body)),
                tutorialItem(userProfilePicture, userProfilePicture.getId() + "", getString(R.string.tutorial_avatar), getString(R.string.tutorial_avatar_body)),
                tutorialItem(ivTutorialBtn, ivTutorialBtn.getId() + "", getString(R.string.repeat_tutorial), getString(R.string.repeat_tutorial_body))
        };
        spotLightViewArr[spotLightCounter].show();
    }

    private SpotlightView.Builder tutorialItem(View view, String usageId, String tvText, String headingTvText) {
        return new SpotlightView.Builder(this)
                .introAnimationDuration(200)
                .enableRevalAnimation(true)
                .performClick(true)
                .fadeinTextDuration(200)
                .headingTvColor(ContextCompat.getColor(this, R.color.violet))
                .headingTvSize(32)
                .headingTvText(tvText)
                .subHeadingTvColor(ContextCompat.getColor(this, R.color.white))
                .subHeadingTvSize(16)
                .subHeadingTvText(headingTvText)
                .maskColor(ContextCompat.getColor(this, R.color.black))
                .target(view)
                .lineAnimDuration(200)
                .lineAndArcColor(ContextCompat.getColor(this, R.color.violet))
                .dismissOnTouch(true)
                .enableDismissAfterShown(true)
                .usageId(usageId)
                .setListener(new SpotlightListener() {
                    @Override
                    public void onUserClicked(String s) {
                        spotLightCounter++;
                        if (spotLightCounter < spotLightViewArr.length) {
                            spotLightViewArr[spotLightCounter].show();
                        } else {
                            spotLightCounter = 0;
                        }
                    }
                });
    }

    private void initReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GCMManager.MESSAGE_RECEIVED);
        intentFilter.addAction(GCMManager.QUEST_RECEIVED);
        App.getLocalBroadcastManager().registerReceiver(gcmMessageReceiver, intentFilter);

        App.getNetworkManager().addOnConnectionChangedListener(new NetworkManager.OnConnectionChangedListener() {
            @Override
            public void onConnectionChanged(boolean connected) {
                updateConnectionState();
            }
        });

        App.getLocationManager().addOnGPSStateChangedListener(new com.severenity.engine.managers.location.LocationManager.OnGPSStateChangedListener() {
            @Override
            public void onGPSStateChangedListener(com.severenity.engine.managers.location.LocationManager.GPSSignal signal) {
                ivGPSState.setImageDrawable(getResources().getDrawable(signal.getId()));
            }
        });

        LocationManager.updateWithGPSSignal(this);
    }

    /**
     * Updates connection state label look and feel and performs actions on connectivity change.
     */
    private void updateConnectionState() {
        if (App.getNetworkManager().isConnected()) {
            tvConnectionState.setText(getResources().getString(R.string.connected));
            tvConnectionState.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            Utils.expandOrCollapse(tvConnectionState, false, false);
        } else {
            tvConnectionState.setText(getResources().getString(R.string.disconnected));
            tvConnectionState.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            Utils.expandOrCollapse(tvConnectionState, true, false);
        }
    }

    /**
     * Updates status label with text provided.
     *
     * @param text - text to show over the label.
     * @param show - if true - label will be shown, otherwise collapsed.
     */
    private void updateStatusLabelWith(String text, boolean show) {
        if (!App.getNetworkManager().isConnected()) {
            return;
        }

        tvConnectionState.setText(text);
        tvConnectionState.setBackgroundColor(getResources().getColor(
            show ? android.R.color.holo_orange_dark : android.R.color.holo_green_dark)
        );
        Utils.expandOrCollapse(tvConnectionState, show, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (shopFragment.getPurchaseHelper() == null) {
            return;
        }

        // Pass on the activity result to the helper for handling
        if (!shopFragment.getPurchaseHelper().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(Constants.TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /**
     * Initializes all socket subscriptions to listen for events from server.
     */
    private void initSocketSubscriptions() {
        if (App.getWebSocketManager().isConnected()) {
            App.getWebSocketManager().subscribeForEvents();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processNewIntent(intent);
    }

    /**
     * Processes provided intent.
     *
     * @param intent - intent to process.
     */
    private void processNewIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        // Notification message received
        if (intent.getAction() != null && intent.getAction().equals(GCMManager.MESSAGE_RECEIVED)) {
            String message = intent.getStringExtra("message");
            Utils.showAlertDialog(message, this);
        }

        // Quest received
        if (intent.getAction() != null && intent.getAction().equals(GCMManager.QUEST_RECEIVED)) {
            handleQuestIntent(intent);
        }

        // Level up
        if (intent.getAction() != null && intent.getAction().equals(GCMManager.LEVEL_UP_RECEIVED)) {
            String level = intent.getStringExtra("level");
            Utils.showAlertDialog(Constants.NOTIFICATION_MSG_LEVEL_UP + level, this);
        }
    }

    /**
     * Initializes all fragments used withing activity.
     */
    private void initFragments() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, gameMapFragment, gameMapFragmentTag)
                .add(R.id.container, shopFragment, shopFragmentTag)
                .add(R.id.container, playerFragment, playerFragmentTag)
                .add(R.id.container, clansFragment, clansFragmentTag)
                .add(R.id.container, questsFragment, questsFragmentTag)
                .add(R.id.container, teamQuestsFragment, teamQuestsFragmentTag).commit();

        allFragments.addAll(Arrays.asList(
                shopFragment,
                clansFragment,
                playerFragment,
                questsFragment,
                gameMapFragment,
                teamQuestsFragment)
        );
    }

    /**
     * Initializes top and bottom toolbars with appropriate items.
     */
    private void initToolbars() {
        toolbarTop = findViewById(R.id.toolbarTop);
        GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (object == null) {
                                return;
                            }

                            Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=normal").into(userProfilePicture);
                            userProfilePicture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
                                    bundle.putString(FirebaseAnalytics.Param.VALUE, "profile_from_top_toolbar");
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

                                    profileItem.callOnClick();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();

        userProfilePicture = toolbarTop.findViewById(R.id.mapUserAvatar);

        tvCreditsValue = toolbarTop.findViewById(R.id.tvCreditsValue);
        tvEnergyValue = toolbarTop.findViewById(R.id.tvEnergyValue);
        tvExperienceValue = toolbarTop.findViewById(R.id.tvExperienceValue);
        tvLevelValue = toolbarTop.findViewById(R.id.tvLevelValue);
        ivTutorialBtn = toolbarTop.findViewById(R.id.ivTutorialBtn);

        toolbarBottom = findViewById(R.id.toolbarBottom);
        toolbarBottom.inflateMenu(R.menu.toolbar_menu);
        tvConnectionState = findViewById(R.id.tvConnectionStateMainActivity);
        ivGPSState = toolbarTop.findViewById(R.id.ivGPSStateMainActivity);
        ivGPSState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();
                cal.add(Calendar.HOUR, -1);
                long startTime = cal.getTimeInMillis();

                java.text.DateFormat dateFormat = getDateInstance();
                Log.i(Constants.TAG, "Range Start: " + dateFormat.format(startTime));
                Log.i(Constants.TAG, "Range End: " + dateFormat.format(endTime));

                DataReadRequest readRequest = new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                        .bucketByTime(30, TimeUnit.MINUTES)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

                PendingResult<DataReadResult> dataReadResult = Fitness.HistoryApi.readData(googleApiClient, readRequest);
                dataReadResult.setResultCallback(new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(@NonNull DataReadResult dataReadResult) {
                        for (Bucket bucket : dataReadResult.getBuckets()) {
                            DataSet dataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA);
                            Utils.dumpDataSet(dataSet);
                        }
                    }
                });

                Toast.makeText(getApplicationContext(), "Your current GPS status.", Toast.LENGTH_SHORT).show();
            }
        });

        shopItem = toolbarBottom.findViewById(R.id.menu_shop);
        chatItem = toolbarBottom.findViewById(R.id.menu_chat);
        mapItem = toolbarBottom.findViewById(R.id.menu_map);
        profileItem = toolbarBottom.findViewById(R.id.menu_profile);
        questsItem = toolbarBottom.findViewById(R.id.menu_quests);
        teamQuestsItem = toolbarBottom.findViewById(R.id.menu_team_quests);

        ivTutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreferencesManager.resetAll();
                showTutorial();
            }
        });
        setSupportActionBar(toolbarTop);

        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                deselectMenu();
                switch (item.getItemId()) {
                    case R.id.menu_shop:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_shop_selected, getTheme()));
                            }
                        });
                        showShop();
                        return true;
                    case R.id.menu_profile:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_profile_selected, getTheme()));
                            }
                        });
                        showProfile();
                        return true;
                    case R.id.menu_map:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_map_selected, getTheme()));
                            }
                        });
                        showMap();
                        return true;
                    case R.id.menu_chat:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_chat_selected, getTheme()));
                            }
                        });
                        showTeams();
                        return true;
                    case R.id.menu_quests:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_quests_selected, getTheme()));
                            }
                        });
                        showQuests();
                        return true;
                    case R.id.menu_team_quests:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_team_quests_selected, getTheme()));
                            }
                        });
                        showTeamQuests();
                        return true;
                    default:
                        break;
                }

                return false;
            }
        });

        if (App.getUserManager().getCurrentUser().getTeamId().isEmpty()) {
            toolbarBottom.getMenu().findItem(R.id.menu_team_quests).setVisible(false);
        } else {
            toolbarBottom.getMenu().findItem(R.id.menu_team_quests).setVisible(true);
        }
    }

    /**
     * Handles behaviour when user selects one of the menu items we have to deselect others.
     */
    private void deselectMenu() {
        for (int i = 0; i < toolbarBottom.getMenu().size(); i++) {
            MenuItem item = toolbarBottom.getMenu().getItem(i);
            switch (item.getItemId()) {
                case R.id.menu_shop:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_shop, getTheme()));
                    break;
                case R.id.menu_profile:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_profile, getTheme()));
                    break;
                case R.id.menu_map:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_map, getTheme()));
                    break;
                case R.id.menu_chat:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_chat, getTheme()));
                    break;
                case R.id.menu_quests:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_quests, getTheme()));
                    break;
                case R.id.menu_team_quests:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_team_quests, getTheme()));
                    break;
                default: break;
            }
        }
    }

    /**
     * Hides all fragments in <code>allFragments</code> list.
     */
    private void hideAllFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        for (Fragment f : allFragments) {
            ft.hide(f);
        }
        ft.commit();
    }

    /**
     * Shows specific fragment and hides others.
     *
     * @param fragment - {@link Fragment} to show.
     */
    public void showFragment(final Fragment fragment) {
        if (fragment.isVisible()) {
            return;
        }

        hideAllFragments();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        ft.show(fragment).commit();
    }

    private void showMap() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "map");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        showFragment(gameMapFragment);
    }

    private void showProfile() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "profile");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        showFragment(playerFragment);
    }

    private void showShop() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "shop");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        showFragment(shopFragment);
    }

    private void showTeams() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "teams");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        showFragment(clansFragment);
    }

    private void showQuests() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "quests");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        showFragment(questsFragment);
    }

    private void showTeamQuests() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dislay");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "team_quests");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        showFragment(teamQuestsFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();

        activityActive = false;

        App.getUserManager().updateCurrentUserLocallyWithUser(App.getUserManager().getCurrentUser());
        App.getLocationManager().stopLocationUpdates();

        App.getLocalBroadcastManager().unregisterReceiver(updateUIReceiver);
        App.getLocalBroadcastManager().unregisterReceiver(App.getLocationManager().getGoogleApiClientReceiver());
        App.getLocalBroadcastManager().unregisterReceiver(showPlaceInfoDialog);
        App.getLocalBroadcastManager().unregisterReceiver(statusLabelUpdater);
        App.getLocalBroadcastManager().unregisterReceiver(onTeamChangedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityActive = true;
        updateUIInfo();

        App.getLocalBroadcastManager().registerReceiver(
            updateUIReceiver,
            new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI)
        );

        App.getLocalBroadcastManager().registerReceiver(
            App.getLocationManager().getGoogleApiClientReceiver(),
            new IntentFilter(Constants.INTENT_FILTER_GAC)
        );

        App.getLocalBroadcastManager().registerReceiver(
            showPlaceInfoDialog,
            new IntentFilter(Constants.INTENT_FILTER_SHOW_PLACE_INFO_DIALOG)
        );

        App.getLocalBroadcastManager().registerReceiver(
                statusLabelUpdater,
            new IntentFilter(Constants.INTENT_FILTER_UPDATE_STATUS_LABEL)
        );

        App.getLocalBroadcastManager().registerReceiver(onTeamChangedReceiver,
                new IntentFilter(Constants.INTENT_FILTER_TEAM_CHANGED));
    }

    /**
     * GCM receiver for the message sent from {@link FCMListener}
     * Reacts to registration and messages.
     */
    private BroadcastReceiver gcmMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GCMManager.MESSAGE_RECEIVED) && activityActive) {
                String message = intent.getStringExtra("message");
                Utils.showAlertDialog(message, MainActivity.this);
            } else if (intent.getAction().equals(GCMManager.QUEST_RECEIVED) && activityActive) {
                handleQuestIntent(intent);
            } else if (intent.getAction().equals(GCMManager.LEVEL_UP_RECEIVED) && activityActive) {
                String level = intent.getStringExtra("level");
                Utils.showAlertDialog(Constants.NOTIFICATION_MSG_LEVEL_UP + level, MainActivity.this);
            }
        }
    };

    private BroadcastReceiver onTeamChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean showTeamQuestsTab = intent.getExtras().getBoolean(Constants.INTENT_EXTRA_SHOW_TEAM_QUESTS);
            toolbarBottom.getMenu().findItem(R.id.menu_team_quests).setVisible(showTeamQuestsTab);
        }
    };

    /**
     * Handles intent when the new quest arrives. Shows prompt with accept / decline for the user.
     *
     * @param intent - specific new quest intent with quest in extras.
     */
    private void handleQuestIntent(Intent intent) {
        final Quest q = App.getQuestManager().getQuestFromIntent(intent);
        final NotificationManager notificationManager =
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        Utils.showPromptDialog(Constants.NOTIFICATION_MSG_NEW_QUEST, MainActivity.this, new Utils.PromptCallback() {
            @Override
            public void onAccept() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        App.getQuestManager().onQuestAccepted(q);
                    }
                });
                notificationManager.cancel(0);
            }

            @Override
            public void onDecline() {
                notificationManager.cancel(0);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.rvQuests) {
            getMenuInflater().inflate(R.menu.list_menu, menu);
        }
    }

    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUIInfo();
        }
    };

    /**
     * Updates user profile characteristics info in the toolbar.
     */
    private void updateUIInfo() {
        User user = App.getUserManager().getCurrentUser();
        if (user == null) {
            return;
        }

        tvCreditsValue.setText(String.format(getResources().getString(R.string.credits_value), user.getCredits()));
        tvEnergyValue.setText(String.format(getResources().getString(R.string.energy_value), user.getEnergy()));
        tvExperienceValue.setText(String.format(getResources().getString(R.string.experience_value), user.getExperience()));
        tvLevelValue.setText(String.format(getResources().getString(R.string.level_value), user.getLevel()));
    }

    private BroadcastReceiver showPlaceInfoDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            try {
                JSONObject objectInfo = new JSONObject(extra.getString(Constants.OBJECT_INFO_AS_JSON));

                switch (objectInfo.getInt(Constants.OBJECT_TYPE_IDENTIFIER)) {
                    case Constants.TYPE_PLACE: {
                        boolean showRelocationButton = extra.getBoolean(PlacesInfoDialog.SHOW_RELOCATION_BUTTON, false);
                        mPlaceInfoDialog = PlacesInfoDialog.newInstance(objectInfo.getString(Constants.PLACE_ID),
                                showRelocationButton);
                        FragmentManager fm = getSupportFragmentManager();
                        mPlaceInfoDialog.show(fm, "placeInfoDialog");
                        break;
                    }

                    default: {
                        Log.d(Constants.TAG, "Unsupported object type: " + objectInfo.getInt(Constants.OBJECT_TYPE_IDENTIFIER) + " for displaying info dialog.");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver statusLabelUpdater = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            updateStatusLabelWith(intent.getStringExtra("text"), extras.getBoolean("show"));
        }
    };

    @Override
    public void OnRelocate(String placeID) {
        if (mPlaceInfoDialog != null) {
            mPlaceInfoDialog.dismiss();

            mapItem.callOnClick();
            App.getLocationManager().showPlaceAtPosition(placeID);
        }
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_back_button");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "tapped");
            bundle.putString(FirebaseAnalytics.Param.VALUE, "twice");
            firebaseAnalytics.logEvent("EXIT", bundle);

            finish(); // finish activity
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "severenity_back_button");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "tapped");
            bundle.putString(FirebaseAnalytics.Param.VALUE, "once");
            firebaseAnalytics.logEvent("EXIT", bundle);

            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}