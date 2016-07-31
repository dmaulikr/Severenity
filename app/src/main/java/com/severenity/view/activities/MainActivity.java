package com.severenity.view.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.MapsInitializer;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.managers.data.EnergyRecoveryManager;
import com.severenity.engine.managers.messaging.FCMListener;
import com.severenity.engine.managers.messaging.GCMManager;
import com.severenity.entity.GamePlace;
import com.severenity.entity.User;
import com.severenity.entity.quest.CaptureQuest;
import com.severenity.entity.quest.CollectQuest;
import com.severenity.entity.quest.DistanceQuest;
import com.severenity.entity.quest.Quest;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.Dialogs.PlacesInfoDialog;
import com.severenity.view.custom.SplitToolbar;
import com.severenity.view.fragments.GameMapFragment;
import com.severenity.view.fragments.ClansFragment;
import com.severenity.view.fragments.PlayerFragment;
import com.severenity.view.fragments.QuestsFragment;
import com.severenity.view.fragments.ShopFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements PlacesInfoDialog.OnRelocateMapListener {
    private FrameLayout container;
    private SplitToolbar toolbarBottom;
    private Toolbar toolbarTop;

    private ProfilePictureView userProfilePicture;
    private TextView tvEnergyValue, tvImmunityValue, tvExperienceValue, tvLevelValue;
    private ImageView ivTutorialBtn;

    private FragmentManager fragmentManager;

    private ShopFragment shopFragment = new ShopFragment();
    private ClansFragment clansFragment = new ClansFragment();
    private PlayerFragment playerFragment = new PlayerFragment();
    private QuestsFragment battlesFragment = new QuestsFragment();
    private GameMapFragment gameMapFragment = new GameMapFragment();
    private String shopFragmentTag = ShopFragment.class.getSimpleName();
    private String clansFragmentTag = ClansFragment.class.getSimpleName();
    private String playerFragmentTag = PlayerFragment.class.getSimpleName();
    private String battlesFragmentTag = QuestsFragment.class.getSimpleName();
    private String gameMapFragmentTag = GameMapFragment.class.getSimpleName();
    private PlacesInfoDialog mPlaceInfoDialog;

    private LocationManager locationManager;

    private ArrayList<Fragment> allFragments = new ArrayList<>();

    private boolean activityActive = false;

    private EnergyRecoveryManager mRecoveryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        initToolbars();
        initFragments();
        initSocketSubscriptions();

        toolbarBottom.findViewById(R.id.menu_map).performClick();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GCMManager.MESSAGE_RECEIVED);
        intentFilter.addAction(GCMManager.QUEST_RECEIVED);
        App.getLocalBroadcastManager().registerReceiver(gcmMessageReceiver, intentFilter);

        processNewIntent(getIntent());

        mRecoveryManager = new EnergyRecoveryManager(getApplicationContext());
        mRecoveryManager.start();

        checkGPSConnection();

        showTutorial(false);
    }

    private void showTutorial(boolean isOnButtonClicked) {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        if (sPref.getBoolean("isTutorialOn", true) || isOnButtonClicked) {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean("isTutorialOn", false);
            ed.apply();
            Toast.makeText(getApplicationContext(),"Future Tutorial", Toast.LENGTH_LONG).show();
        }
    }

    private void checkGPSConnection() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder agBuilder = new AlertDialog.Builder(this);
            agBuilder.setMessage("Your GPS seems to be turned off, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            final AlertDialog ad = agBuilder.create();
            ad.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecoveryManager.stop();
    }

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

    private Quest getQuestFromIntent(Intent intent) {
        Quest quest = new Quest();
        Quest.QuestType type = Quest.QuestType.values()[intent.getIntExtra("type", 0)];
        quest.setId(intent.getLongExtra("id", 0));
        quest.setType(type);
        quest.setTitle(intent.getStringExtra("title"));
        quest.setStatus(Quest.QuestStatus.values()[intent.getIntExtra("status", 0)]);
        quest.setExpirationTime(intent.getStringExtra("expirationTime"));
        quest.setCredits(intent.getLongExtra("credits", 0));
        quest.setExperience(intent.getLongExtra("experience", 0));

        if (type == Quest.QuestType.Distance) {
            quest = new DistanceQuest(quest, intent.getIntExtra("distance", 1));
        } else if (type == Quest.QuestType.Capture) {
            quest = new CaptureQuest(quest, GamePlace.PlaceType.values()[intent.getIntExtra("placeType", 0)], intent.getIntExtra("placeTypeValue", 0));
        } else if (type == Quest.QuestType.Collect) {
            String characteristic = intent.getStringExtra("characteristic");
            Constants.Characteristic c = Constants.Characteristic.None;
            if (characteristic.equals(Constants.Characteristic.Level.toString())) {
                c = Constants.Characteristic.Level;
            } else if (characteristic.equals(Constants.Characteristic.Experience.toString())) {
                c = Constants.Characteristic.Experience;
            } else if (characteristic.equals(Constants.Characteristic.Energy.toString())) {
                c = Constants.Characteristic.Energy;
            } else if (characteristic.equals(Constants.Characteristic.Immunity.toString())) {
                c = Constants.Characteristic.Immunity;
            }

            quest = new CollectQuest(quest, c, intent.getIntExtra("characteristicAmount", 0));
        }

        return quest;
    }

    private void initFragments() {
        container = (FrameLayout) findViewById(R.id.container);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .add(R.id.container, gameMapFragment, gameMapFragmentTag)
            .add(R.id.container, shopFragment, shopFragmentTag)
            .add(R.id.container, playerFragment, playerFragmentTag)
            .add(R.id.container, clansFragment, clansFragmentTag)
            .add(R.id.container, battlesFragment, battlesFragmentTag).commit();

        allFragments.addAll(Arrays.asList(
            shopFragment,
            clansFragment,
            playerFragment,
            battlesFragment,
            gameMapFragment)
        );
    }

    private void initToolbars() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
        GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (object == null) {
                                return;
                            }

                            userProfilePicture.setProfileId(object.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();

        userProfilePicture = (ProfilePictureView) toolbarTop.findViewById(R.id.mapUserAvatar);

        tvImmunityValue = (TextView) toolbarTop.findViewById(R.id.tvImmunityValue);
        tvEnergyValue = (TextView) toolbarTop.findViewById(R.id.tvEnergyValue);
        tvExperienceValue = (TextView) toolbarTop.findViewById(R.id.tvExperienceValue);
        tvLevelValue = (TextView) toolbarTop.findViewById(R.id.tvLevelValue);
        ivTutorialBtn = (ImageView) toolbarTop.findViewById(R.id.ivTutorialBtn);
        ivTutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTutorial(true);
            }
        });
        setSupportActionBar(toolbarTop);

        toolbarBottom = (SplitToolbar) findViewById(R.id.toolbarBottom);
        toolbarBottom.inflateMenu(R.menu.toolbar_menu);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                deselectMenu();
                switch (item.getItemId()) {
                    case R.id.menu_shop:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_shop_selected, getTheme()));
                                } else {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_shop_selected));
                                }
                            }
                        });
                        showShop();
                        return true;
                    case R.id.menu_profile:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_profile_selected, getTheme()));
                                } else {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_profile_selected));
                                }
                            }
                        });
                        showProfile();
                        return true;
                    case R.id.menu_map:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_map_selected, getTheme()));
                                } else {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_map_selected));
                                }
                            }
                        });
                        showMap();
                        return true;
                    case R.id.menu_chat:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_chat_selected, getTheme()));
                                } else {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_chat_selected));
                                }
                            }
                        });
                        showTeams();
                        return true;
                    case R.id.menu_quests:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_quests_selected, getTheme()));
                                } else {
                                    item.setIcon(getResources().getDrawable(R.drawable.menu_quests_selected));
                                }
                            }
                        });
                        showBattles();
                        return true;
                    default:
                        break;
                }

                return false;
            }
        });
    }

    private void deselectMenu() {
        for (int i = 0; i < toolbarBottom.getMenu().size(); i++) {
            MenuItem item = toolbarBottom.getMenu().getItem(i);
            switch (item.getItemId()) {
                case R.id.menu_shop:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_shop, getTheme()));
                    } else {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_shop));
                    }
                    break;
                case R.id.menu_profile:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_profile, getTheme()));
                    } else {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_profile));
                    }
                    break;
                case R.id.menu_map:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_map, getTheme()));
                    } else {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_map));
                    }
                    break;
                case R.id.menu_chat:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_chat, getTheme()));
                    } else {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_chat));
                    }
                    break;
                case R.id.menu_quests:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_quests, getTheme()));
                    } else {
                        item.setIcon(getResources().getDrawable(R.drawable.menu_quests));
                    }
                    break;
                default: break;
            }
        }
    }

    private void hideAllFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        for (Fragment f : allFragments) {
            ft.hide(f);
        }
        ft.commit();
    }

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
        showFragment(gameMapFragment);
    }

    // TODO: Replace with transaction to real profile fragment
    private void showProfile() {
        showFragment(playerFragment);
    }

    // TODO: Replace with transaction to real shop fragment
    private void showShop() {
        showFragment(shopFragment);
    }

    // TODO: Replace with transaction to real teams fragment
    private void showTeams() {
        showFragment(clansFragment);
    }

    // TODO: Replace with transaction to real battles fragment
    private void showBattles() {
        showFragment(battlesFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();

        activityActive = false;

        App.getUserManager().updateCurrentUserLocally();
        App.getLocationManager().stopLocationUpdates();
        App.getGoogleApiHelper().disconnect();

        App.getLocalBroadcastManager().unregisterReceiver(updateUIReceiver);
        App.getLocalBroadcastManager().unregisterReceiver(App.getLocationManager().getGoogleApiClientReceiver());
        App.getLocalBroadcastManager().unregisterReceiver(showPlaceInfoDialog);
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

        App.getGoogleApiHelper().connect();

        App.getLocalBroadcastManager().registerReceiver(
                showPlaceInfoDialog,
                new IntentFilter(Constants.INTENT_FILTER_SHOW_PLACE_INFO_DIALOG)
        );
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

    /**
     * Handles intent when the new quest arrives. Shows prompt with accept / decline for the user.
     *
     * @param intent - specific new quest intent with quest in extras.
     */
    private void handleQuestIntent(Intent intent) {
        final Quest q = getQuestFromIntent(intent);
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
                notificationManager.cancel((int) q.getId());
            }

            @Override
            public void onDecline() {
                notificationManager.cancel((int) q.getId());
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

    public Toolbar getToolbarTop() {
        return toolbarTop;
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

        tvImmunityValue.setText(String.format(getResources().getString(R.string.immunity_value), user.getImmunity()));
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

    @Override
    public void OnRelocate(String placeID) {
        if (mPlaceInfoDialog != null) {
            mPlaceInfoDialog.dismiss();

            toolbarBottom.findViewById(R.id.menu_map).performClick();
            App.getLocationManager().showPlaceAtPosition(placeID);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}