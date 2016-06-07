package com.nosad.sample.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.facebook.HttpMethod;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.ChipAdapter;
import com.nosad.sample.engine.network.RequestCallback;
import com.nosad.sample.entity.GamePlace;
import com.nosad.sample.utils.CustomTypefaceSpan;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.Dialogs.PlacesInfoDialog;
import com.nosad.sample.view.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.internal.Util;

/**
 * Handles user with map activity (actual game)
 */
public class GameMapFragment extends Fragment implements View.OnClickListener {
    private SupportMapFragment mapFragment;
    private MainActivity activity;
    private TextView tvAttributions;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private LinearLayout mPlaceActions;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ActionMode spellMode;

    private ChipAdapter chipAdapter;
    private String mPlaceIDtoCapture;

    public GameMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
    }

    @Override
    public void onResume() {
        Log.v(Constants.TAG, this.toString() + " onResume()");
        super.onResume();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                App.getLocationManager().updateMap(googleMap);
            }
        });

        App.getLocalBroadcastManager().registerReceiver(
                showPlaceActions,
                new IntentFilter(Constants.INTENT_FILTER_SHOW_PLACE_ACTIONS)
        );

        App.getLocalBroadcastManager().registerReceiver(
                hidePlaceActions,
                new IntentFilter(Constants.INTENT_FILTER_HIDE_PLACE_ACTIONS)
        );

        App.getLocalBroadcastManager().registerReceiver(
                requestPlacesFromGoogle,
                new IntentFilter(Constants.INTENT_FILTER_REQUEST_PLACES));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        tvAttributions = (TextView) view.findViewById(R.id.tvAttributions);

        initDrawer(view);

        return view;
    }

    private void initDrawer(View view) {
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerMap);

        mPlaceActions = (LinearLayout)view.findViewById(R.id.placeActions);
        mPlaceActions.findViewById(R.id.captureButton).setOnClickListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                activity, drawerLayout, activity.getToolbarTop(),
                R.string.drawerOpened,
                R.string.drawerClosed) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                SpannableString s = new SpannableString(getResources().getString(R.string.title));
                Typeface prometheus = Typeface.createFromAsset(activity.getAssets(), "fonts/zekton.ttf");
                s.setSpan(new CustomTypefaceSpan("", prometheus), 0, s.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                activity.getSupportActionBar().setTitle(s);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                SpannableString s = new SpannableString(getResources().getString(R.string.title));
                Typeface prometheus = Typeface.createFromAsset(activity.getAssets(), "fonts/zekton.ttf");
                s.setSpan(new CustomTypefaceSpan("", prometheus), 0, s.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                activity.getSupportActionBar().setTitle(s);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        chipAdapter = new ChipAdapter(activity, R.layout.spell_item);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        drawerList = (ListView) view.findViewById(R.id.lvSpells);
        drawerList.setAdapter(chipAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (spellMode == null) {
                    App.getSpellManager().setCurrentChip(chipAdapter.getItem(position));
                    spellMode = activity.startSupportActionMode(new ActionBarSpell());
                    spellMode.setTitle(String.format(getResources().getString(R.string.spell_selected), chipAdapter.getItem(position).getTitle()));
                } else {
                    App.getSpellManager().setCurrentChip(chipAdapter.getItem(position));

                    if (App.getSpellManager().getCurrentChip() == null) {
                        spellMode.finish();
                        spellMode = null;
                    } else {
                        spellMode.setTitle(String.format(getResources().getString(R.string.spell_selected), chipAdapter.getItem(position).getTitle()));
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onPause() {
        Log.v(Constants.TAG, this.toString() + " onPause()");
        super.onPause();

        App.getLocalBroadcastManager().unregisterReceiver(showPlaceActions);
        App.getLocalBroadcastManager().unregisterReceiver(hidePlaceActions);
    }

    private BroadcastReceiver showPlaceActions = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle intentData = intent.getExtras();
            mPlaceIDtoCapture = intentData.getString(Constants.PLACE_ID);

            // TODO: AF: for now do not show action if user owns this place
            GamePlace place = App.getPlacesManager().findPlaceByID(mPlaceIDtoCapture);
            if (place.hasOwner(App.getUserManager().getCurrentUser().getId())) {

                if (mPlaceActions.getVisibility() == View.VISIBLE) {
                    App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_PLACE_ACTIONS));
                }

                return;
            }

            if (mPlaceActions.getVisibility() == View.INVISIBLE) {

                mPlaceActions.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.place_actions_slide_in);
                mPlaceActions.startAnimation(anim);
            }
        }
    };

    private BroadcastReceiver hidePlaceActions = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mPlaceActions.getVisibility() == View.VISIBLE) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.place_actions_slide_out);
                mPlaceActions.startAnimation(anim);
                mPlaceActions.setVisibility(View.INVISIBLE);
            }
        }
    };

    private BroadcastReceiver requestPlacesFromGoogle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        com.google.android.gms.common.api.PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(App.getGoogleApiHelper().getGoogleApiClient(), null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                Log.i(Constants.TAG, String.format("Attributions are: %s", likelyPlaces.getAttributions()));
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    App.getLocationManager().displayPlaceMarker(placeLikelihood.getPlace());
                }
                likelyPlaces.release();
                tvAttributions.setText(likelyPlaces.getAttributions() == null ? "" : likelyPlaces.getAttributions());
            }
        });
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.captureButton:

                if (!mPlaceIDtoCapture.isEmpty()) {
                    App.getPlacesManager().addOwnerToPlace(mPlaceIDtoCapture, App.getUserManager().getCurrentUser().getId());
                    App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_PLACE_ACTIONS));

                    Toast.makeText(getContext(), "Place has been captured", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private class ActionBarSpell implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.spell_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            spellMode.finish();
            spellMode = null;
        }
    }
}