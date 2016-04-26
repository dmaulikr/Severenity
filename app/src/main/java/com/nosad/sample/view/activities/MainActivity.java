package com.nosad.sample.view.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.managers.location.StepManager;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.CustomTypefaceSpan;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.custom.SplitToolbar;
import com.nosad.sample.view.fragments.BattlesFragment;
import com.nosad.sample.view.fragments.GameMapFragment;
import com.nosad.sample.view.fragments.ProfileFragment;
import com.nosad.sample.view.fragments.ShopFragment;
import com.nosad.sample.view.fragments.TeamsFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private StepManager stepManager;

    private FrameLayout container;
    private SplitToolbar toolbarBottom;
    private Toolbar toolbarTop;

    private FragmentManager fragmentManager;

    private ShopFragment shopFragment = new ShopFragment();
    private TeamsFragment teamsFragment = new TeamsFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private BattlesFragment battlesFragment = new BattlesFragment();
    private GameMapFragment gameMapFragment = new GameMapFragment();
    private String shopFragmentTag = ShopFragment.class.getSimpleName();
    private String teamsFragmentTag = TeamsFragment.class.getSimpleName();
    private String profileFragmentTag = ProfileFragment.class.getSimpleName();
    private String battlesFragmentTag = BattlesFragment.class.getSimpleName();
    private String gameMapFragmentTag = GameMapFragment.class.getSimpleName();

    private ArrayList<Fragment> allFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveCurrentUserFBData();
        initToolbars();
        initFragments();

        stepManager = new StepManager(getApplicationContext());

        toolbarBottom.findViewById(R.id.menu_map).performClick();

        App.getLocalBroadcastManager().registerReceiver(
            App.getLocationManager().getStepsCountReceiver(),
            new IntentFilter(Constants.INTENT_FILTER_STEPS)
        );
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

                            user = App.getUserManager().addUser(user);
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
                .add(R.id.container, teamsFragment, teamsFragmentTag)
                .add(R.id.container, battlesFragment, battlesFragmentTag).commit();

        allFragments.addAll(
                Arrays.asList(
                        shopFragment,
                        teamsFragment,
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
        showFragment(teamsFragment);
    }

    // TODO: Replace with transaction to real battles fragment
    private void showBattles() {
        showFragment(battlesFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();

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

        App.getLocalBroadcastManager().unregisterReceiver(
                App.getLocationManager().getStepsCountReceiver()
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

    public Toolbar getToolbarTop() {
        return toolbarTop;
    }
}