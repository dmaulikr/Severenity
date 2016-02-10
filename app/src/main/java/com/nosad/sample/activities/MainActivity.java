package com.nosad.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.facebook.Profile;
import com.nosad.sample.R;
import com.nosad.sample.common.Constants;
import com.nosad.sample.fragments.GameMapFragment;
import com.nosad.sample.fragments.MainFragment;
import com.nosad.sample.network.WebSocketManager;

public class MainActivity extends AppCompatActivity implements GameMapFragment.OnPauseGameListener,
        MainFragment.OnResumeGameListener {
    private FrameLayout container;
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

        container = (FrameLayout) findViewById(R.id.container);
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.container, mainFragment, mainFragmentTag).commit();
    }

    @Override
    public void onPauseGame() {
        Log.d(Constants.TAG, this + " onPauseGame");

        // disconnect web socket client so server is not notified about location changes.
        WebSocketManager.instance.disconnectWebSocketClient();

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
}
