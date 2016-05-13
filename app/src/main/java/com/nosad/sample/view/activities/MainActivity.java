package com.nosad.sample.view.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.managers.messaging.GCMManager;
import com.nosad.sample.engine.managers.messaging.RegistrationIntentService;
import com.nosad.sample.entity.User;
import com.nosad.sample.entity.quest.CaptureQuest;
import com.nosad.sample.entity.quest.CollectQuest;
import com.nosad.sample.entity.quest.DistanceQuest;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.CustomTypefaceSpan;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.custom.SplitToolbar;
import com.nosad.sample.view.fragments.GameMapFragment;
import com.nosad.sample.view.fragments.MessagesFragment;
import com.nosad.sample.view.fragments.ProfileFragment;
import com.nosad.sample.view.fragments.QuestsFragment;
import com.nosad.sample.view.fragments.ShopFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private FrameLayout container;
    private SplitToolbar toolbarBottom;
    private Toolbar toolbarTop;

    private FragmentManager fragmentManager;

    private ShopFragment shopFragment = new ShopFragment();
    private MessagesFragment messagesFragment = new MessagesFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private QuestsFragment battlesFragment = new QuestsFragment();
    private GameMapFragment gameMapFragment = new GameMapFragment();
    private String shopFragmentTag = ShopFragment.class.getSimpleName();
    private String messagesFragmentTag = MessagesFragment.class.getSimpleName();
    private String profileFragmentTag = ProfileFragment.class.getSimpleName();
    private String battlesFragmentTag = QuestsFragment.class.getSimpleName();
    private String gameMapFragmentTag = GameMapFragment.class.getSimpleName();

    private ArrayList<Fragment> allFragments = new ArrayList<>();

    private boolean activityActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveCurrentUserFBData();
        initToolbars();
        initFragments();

        toolbarBottom.findViewById(R.id.menu_map).performClick();

        // If device was not registered - start registration service
        if (!App.getInstance().isCurrentDeviceRegistered()) {
            startDeviceRegistrationService();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GCMManager.REGISTRATION_PROCESS);
        intentFilter.addAction(GCMManager.MESSAGE_RECEIVED);
        intentFilter.addAction(GCMManager.QUEST_RECEIVED);
        App.getLocalBroadcastManager().registerReceiver(gcmMessageReceiver, intentFilter);

        processNewIntent(getIntent());
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
            quest = new CaptureQuest(quest, intent.getStringExtra("placeType"), intent.getIntExtra("placeTypeValue", 0));
        } else if (type == Quest.QuestType.Collect) {
            String characteristic = intent.getStringExtra("characteristic");
            Constants.Characteristic c = Constants.Characteristic.None;
            if (characteristic.equals(Constants.Characteristic.Level.toString())) {
                c = Constants.Characteristic.Level;
            } else if (characteristic.equals(Constants.Characteristic.Experience.toString())) {
                c = Constants.Characteristic.Experience;
            } else if (characteristic.equals(Constants.Characteristic.Mentality.toString())) {
                c = Constants.Characteristic.Mentality;
            } else if (characteristic.equals(Constants.Characteristic.Immunity.toString())) {
                c = Constants.Characteristic.Immunity;
            }

            quest = new CollectQuest(quest, c, intent.getIntExtra("characteristicAmount", 0));
        }

        return quest;
    }

    /**
     * Starts {@link RegistrationIntentService} to register current device.
     */
    private void startDeviceRegistrationService() {
        Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_ID, Utils.getDeviceId(this));
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_NAME, Utils.getDeviceName());
        startService(intent);
    }

    private void retrieveCurrentUserFBData() {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,email");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.i(Constants.TAG, String.valueOf(response.getJSONObject()));
                            if (response.getJSONObject() == null) {
                                return;
                            }

                            User user = new User();
                            user.setEmail(response.getJSONObject().getString("email"));
                            user.setName(response.getJSONObject().getString("name"));
                            user.setId(response.getJSONObject().getString("id"));

                            if (!App.getUserManager().addUser(user)) {
                                return;
                            }

                            App.getUserManager().setCurrentUser(user);
                            App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_UPDATE_UI));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
    }

    private void initFragments() {
        container = (FrameLayout) findViewById(R.id.container);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, gameMapFragment, gameMapFragmentTag)
                .add(R.id.container, shopFragment, shopFragmentTag)
                .add(R.id.container, profileFragment, profileFragmentTag)
                .add(R.id.container, messagesFragment, messagesFragmentTag)
                .add(R.id.container, battlesFragment, battlesFragmentTag).commit();

        allFragments.addAll(
                Arrays.asList(
                        shopFragment,
                        messagesFragment,
                        profileFragment,
                        battlesFragment,
                        gameMapFragment)
        );
    }

    private void initToolbars() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
        toolbarTop.setNavigationIcon(R.mipmap.menu_arrow_left);
        SpannableString s = new SpannableString(getResources().getString(R.string.title));
        Typeface prometheus = Typeface.createFromAsset(getAssets(), "fonts/zekton.ttf");
        s.setSpan(new CustomTypefaceSpan("", prometheus), 0, s.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        toolbarTop.setTitleTextColor(getResources().getColor(R.color.colorAccent, getTheme()));
        toolbarTop.setTitle(s);

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
                    case R.id.menu_teams:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_teams_selected, getTheme()));
                            }
                        });
                        showTeams();
                        return true;
                    case R.id.menu_battles:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.setIcon(getResources().getDrawable(R.drawable.menu_chalice_selected, getTheme()));
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
                    item.setIcon(getResources().getDrawable(R.drawable.menu_shop, getTheme()));
                    break;
                case R.id.menu_profile:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_profile, getTheme()));
                    break;
                case R.id.menu_map:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_map, getTheme()));
                    break;
                case R.id.menu_teams:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_teams, getTheme()));
                    break;
                case R.id.menu_battles:
                    item.setIcon(getResources().getDrawable(R.drawable.menu_chalice, getTheme()));
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
        showFragment(profileFragment);
    }

    // TODO: Replace with transaction to real shop fragment
    private void showShop() {
        showFragment(shopFragment);
    }

    // TODO: Replace with transaction to real teams fragment
    private void showTeams() {
        showFragment(messagesFragment);
    }

    // TODO: Replace with transaction to real battles fragment
    private void showBattles() {
        showFragment(battlesFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();

        activityActive = false;

        App.getUserManager().updateCurrentUserInDB();
        App.getLocationManager().stopLocationUpdates();
        App.getGoogleApiHelper().disconnect();
        App.getLocalBroadcastManager().unregisterReceiver(
                App.getLocationManager().getGoogleApiClientReceiver()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityActive = true;

        App.getLocalBroadcastManager().registerReceiver(
                App.getLocationManager().getGoogleApiClientReceiver(),
                new IntentFilter(Constants.INTENT_FILTER_GAC)
        );
        App.getGoogleApiHelper().connect();
    }

    /**
     * GCM receiver for the message sent from {@link com.nosad.sample.engine.managers.messaging.GCMListener}
     * Reacts to registration and messages.
     */
    private BroadcastReceiver gcmMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GCMManager.REGISTRATION_PROCESS)) {
                String result  = intent.getStringExtra("result");
                String message = intent.getStringExtra("message");

                if (result.equals("success")) {
                    App.getSharedPreferences().edit().putBoolean(Constants.PREFS_DEVICE_REGISTERED, true).apply();
                } else {
                    App.getSharedPreferences().edit().putBoolean(Constants.PREFS_DEVICE_REGISTERED, false).apply();
                }

                Log.d(Constants.TAG, "onReceive: " + result + message);
                Toast.makeText(context, result + " : " + message, Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(GCMManager.MESSAGE_RECEIVED) && activityActive) {
                String message = intent.getStringExtra("message");
                Utils.showAlertDialog(message, MainActivity.this);
            } else if (intent.getAction().equals(GCMManager.QUEST_RECEIVED) && activityActive) {
                handleQuestIntent(intent);
            }
        }
    };

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
                        App.getQuestManager().onQuestReceived(q);
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
        if (v.getId() == R.id.lvQuests) {
            getMenuInflater().inflate(R.menu.list_menu, menu);
        }
    }

    public Toolbar getToolbarTop() {
        return toolbarTop;
    }
}