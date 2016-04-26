package com.nosad.sample.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.SpellsAdapter;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.CustomTypefaceSpan;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.MainActivity;
import com.nosad.sample.view.custom.GifView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles user with map activity (actual game)
 */
public class GameMapFragment extends Fragment {
    private SupportMapFragment mapFragment;
    private MainActivity activity;

    private ProfilePictureView userProfilePicture;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tvMentalityValue, tvImmunityValue, tvExperienceValue, tvLevelValue;
    private TextView tvAttributions;
    private ImageView ivWardsSwitch;

    private ActionMode spellMode;

    private SpellsAdapter spellsAdapter;

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

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
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

    @Override
    public void onResume() {
        Log.v(Constants.TAG, this.toString() + " onResume()");
        super.onResume();
        updateUIInfo();

        App.getLocalBroadcastManager().registerReceiver(
                wardsCountChangedReceiver,
                new IntentFilter(Constants.INTENT_FILTER_WARDS_COUNT)
        );
        App.getLocalBroadcastManager().registerReceiver(
                explosion,
                new IntentFilter("explosion")
        );
        App.getLocalBroadcastManager().registerReceiver(
                updateUIReceiver,
                new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI)
        );
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                App.getLocationManager().updateMap(googleMap);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

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

        userProfilePicture = (ProfilePictureView) view.findViewById(R.id.mapUserAvatar);

        tvImmunityValue = (TextView) view.findViewById(R.id.tvImmunityValue);
        tvMentalityValue = (TextView) view.findViewById(R.id.tvMentalityValue);
        tvExperienceValue = (TextView) view.findViewById(R.id.tvExperienceValue);
        tvLevelValue = (TextView) view.findViewById(R.id.tvLevelValue);
        tvAttributions = (TextView) view.findViewById(R.id.tvAttributions);

        ivWardsSwitch = (ImageView) view.findViewById(R.id.ivWardsSwitch);
        ivWardsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    App.getSpellManager().moveToNextWard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        initDrawer(view);

        return view;
    }

    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUIInfo();
        }
    };

    private void updateUIInfo() {
        User user = App.getUserManager().getCurrentUser();
        if (user == null) {
            return;
        }

        tvImmunityValue.setText(String.format(getResources().getString(R.string.immunity_value), user.getImmunity()));
        tvMentalityValue.setText(String.format(getResources().getString(R.string.mentality_value), user.getMentality()));
        tvExperienceValue.setText(String.format(getResources().getString(R.string.experience_value), user.getExperience()));
        tvLevelValue.setText(String.format(getResources().getString(R.string.level_value), user.getLevel()));
    }

    private void initDrawer(View view) {
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerMap);

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

        spellsAdapter = new SpellsAdapter(activity, R.layout.spell_item);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        drawerList = (ListView) view.findViewById(R.id.lvSpells);
        drawerList.setAdapter(spellsAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (spellMode == null) {
                    App.getSpellManager().setIsSpellMode(true);
                    App.getSpellManager().setCurrentSpell(spellsAdapter.getItem(position));
                    spellMode = activity.startSupportActionMode(new ActionBarSpell());
                    spellMode.setTitle(String.format(getResources().getString(R.string.spell_selected), spellsAdapter.getItem(position).getTitle()));
                } else {
                    if (App.getSpellManager().getCurrentSpell() == spellsAdapter.getItem(position)) {
                        App.getSpellManager().setIsSpellMode(false);
                        spellMode.finish();
                        spellMode = null;
                    } else {
                        App.getSpellManager().setCurrentSpell(spellsAdapter.getItem(position));
                        spellMode.setTitle(String.format(getResources().getString(R.string.spell_selected), spellsAdapter.getItem(position).getTitle()));
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
        App.getLocalBroadcastManager().unregisterReceiver(wardsCountChangedReceiver);
        App.getLocalBroadcastManager().unregisterReceiver(updateUIReceiver);
        App.getLocalBroadcastManager().unregisterReceiver(explosion);
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
            App.getSpellManager().setIsSpellMode(false);
            spellMode.finish();
            spellMode = null;
        }
    }

    private BroadcastReceiver wardsCountChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (App.getSpellManager().hasWards()) {
                ivWardsSwitch.setVisibility(View.VISIBLE);
            } else {
                ivWardsSwitch.setVisibility(View.GONE);
            }
        }
    };

    private BroadcastReceiver explosion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Point p = (Point) intent.getExtras().get("point");
            final GifView gifView = new GifView(activity);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = p.x - 100;
            params.topMargin = p.y - 100;
            ViewGroup view = (ViewGroup) getView();
            view.addView(gifView, params);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ViewGroup) getView()).removeView(gifView);
                }
            }, 2000);
        }
    };
}