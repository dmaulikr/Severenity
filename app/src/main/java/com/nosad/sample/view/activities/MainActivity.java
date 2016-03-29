package com.nosad.sample.view.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.network.WebSocketManager;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.custom.SplitToolbar;
import com.nosad.sample.view.fragments.GameMapFragment;
import com.nosad.sample.view.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements GameMapFragment.OnPauseGameListener,
        MainFragment.OnResumeGameListener {

    private FrameLayout container;
    private SplitToolbar toolbarBottom;
    private Toolbar toolbarTop;

    public Profile profile;

    private FragmentManager fragmentManager;

    private GameMapFragment gameMapFragment = new GameMapFragment();
    private MainFragment mainFragment = new MainFragment();
    private String gameMapFragmentTag = GameMapFragment.class.getSimpleName();
    private String mainFragmentTag = MainFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginActivityIntent = getIntent();
        profile = loginActivityIntent.getParcelableExtra("profile");

        Log.d(Constants.TAG, "Profile is " + (profile == null ? "null" : profile.toString()));

        container = (FrameLayout) findViewById(R.id.container);
        toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
        toolbarTop.setNavigationIcon(R.mipmap.menu_arrow_left);
        toolbarTop.setTitleTextColor(getResources().getColor(R.color.colorAccent, getTheme()));
        toolbarTop.setTitle(R.string.title);

        setSupportActionBar(toolbarTop);

        toolbarBottom = (SplitToolbar) findViewById(R.id.toolbarBottom);
        toolbarBottom.inflateMenu(R.menu.toolbar_menu);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.menu_shop:
                        showShop();
                        return true;
                    case R.id.menu_profile:
                        showProfile();
                        return true;
                    case R.id.menu_map:
                        showMap();
                        return true;
                    case R.id.menu_teams:
                        showTeams();
                        return true;
                    case R.id.menu_battles:
                        showBattles();
                        return true;
                    default:
                        break;
                }

                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.container, mainFragment, mainFragmentTag).commit();
    }

    @Override
    public void onPauseGame() {
        Log.d(Constants.TAG, this + " onPauseGame");

        // disconnect web socket client so server is not notified about location changes.
//        WebSocketManager.instance.disconnectWebSocketClient();

        // open main menu fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment, mainFragmentTag)
                .commit();

        // store game data
    }

    @Override
    public void onResumeGame() {
        Log.d(Constants.TAG, this + " onResumeGame");

        // create and connect web socket client so server is notified about location changes.
        // TODO: Uncomment this when server will be deployed.
//        WebSocketManager.instance.createWebSocket(Constants.WS_ADDRESS, true);

        // open game map fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, gameMapFragment, gameMapFragmentTag)
                .addToBackStack(gameMapFragmentTag)
                .commit();

        // resume game data
    }

    private void showMap() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, gameMapFragment, gameMapFragmentTag)
                .commit();
    }

    // TODO: Replace with transaction to real profile fragment
    private void showProfile() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment, mainFragmentTag)
                .commit();
    }

    // TODO: Replace with transaction to real shop fragment
    private void showShop() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment, mainFragmentTag)
                .commit();
    }

    // TODO: Replace with transaction to real teams fragment
    private void showTeams() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment, mainFragmentTag)
                .commit();
    }

    // TODO: Replace with transaction to real battles fragment
    private void showBattles() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment, mainFragmentTag)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        App.getLocationManager().stopLocationUpdates();
        App.getGoogleApiHelper().disconnect();
        App.getLocalBroadcastManager().unregisterReceiver(
            App.getLocationManager().getGoogleApiClientReceiver()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        App.getLocalBroadcastManager().registerReceiver(
            App.getLocationManager().getGoogleApiClientReceiver(),
            new IntentFilter(Constants.INTENT_FILTER_GAC)
        );
        App.getGoogleApiHelper().connect();
    }
}