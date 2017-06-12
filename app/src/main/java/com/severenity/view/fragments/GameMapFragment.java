package com.severenity.view.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.GamePlace;
import com.severenity.entity.UsersActions;
import com.severenity.utils.common.Constants;

/**
 * Handles user with map activity (actual game)
 */
public class GameMapFragment extends Fragment {
    private SupportMapFragment mapFragment;
    private TextView tvAttributions;
    private UsersActions mUserActions;

    public GameMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                App.getLocationManager().updateMap(googleMap);
            }
        });

        App.getLocalBroadcastManager().registerReceiver(showUserActions, new IntentFilter(Constants.INTENT_FILTER_SHOW_USER_ACTIONS));
        App.getLocalBroadcastManager().registerReceiver(hideUserActions, new IntentFilter(Constants.INTENT_FILTER_HIDE_USER_ACTIONS));

        App.getLocalBroadcastManager().registerReceiver(
                requestPlacesFromGoogle,
                new IntentFilter(Constants.INTENT_FILTER_REQUEST_PLACES));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        tvAttributions = (TextView) view.findViewById(R.id.tvAttributions);
        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                App.getLocationManager().updateMap(googleMap);
            }
        });

        mUserActions = new UsersActions(view, getActivity().getApplicationContext());

        // TODO: Restore drawer if we will need this later.
        // initDrawer(view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        App.getLocalBroadcastManager().unregisterReceiver(showUserActions);
        App.getLocalBroadcastManager().unregisterReceiver(hideUserActions);
    }

    private BroadcastReceiver showUserActions = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentData = intent.getExtras();

            switch (intentData.getInt(Constants.OBJECT_TYPE_IDENTIFIER)) {
                case Constants.TYPE_PLACE: {
                    String placeId = intentData.getString(Constants.PLACE_ID);
                    // TODO: AF: for now do not show action if user owns this place
                    GamePlace place = App.getPlacesManager().findPlaceByID(placeId);
                    if (place.hasOwner(App.getUserManager().getCurrentUser().getId())) {
                        if (mUserActions.isActionsDisplaying()) {
                            App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_USER_ACTIONS));
                        }
                        return;
                    }

                    mUserActions.setSelectedItemId(placeId);
                    mUserActions.showActionPanel(UsersActions.ActionsType.ActionsOnPlace);
                    break;
                }

                case Constants.TYPE_USER: {
                    String userId = intentData.getString(Constants.USER_ID);
                    mUserActions.setSelectedItemId(userId);
                    mUserActions.showActionPanel(UsersActions.ActionsType.ActionsOnUser);
                    break;
                }

                default:
                    Log.e(Constants.TAG, "Unknown object type: " + intentData.getInt(Constants.OBJECT_TYPE_IDENTIFIER));
            }
        }
    };

    private BroadcastReceiver hideUserActions = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mUserActions.hideActionPanel(context);
        }
    };

    private BroadcastReceiver requestPlacesFromGoogle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Location receive permission is not granted. Please allow Severenity to use your location.", Toast.LENGTH_SHORT).show();
                return;
            }

        com.google.android.gms.common.api.PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(App.getGoogleApiHelper().getGoogleApiClient(), null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
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
}